package com.example.library_book.Customer.Auth;

import com.example.library_book.Config.JwtService;
import com.example.library_book.Customer.Customer;
import com.example.library_book.Customer.CustomerRepository;
import com.example.library_book.Customer.Otp.OtpService;
import com.example.library_book.Customer.Token.Token;
import com.example.library_book.Customer.Token.TokenRepo;
import com.example.library_book.Customer.Token.TokenType;
import com.example.library_book.Exception.FindByNoId;
import com.example.library_book.Exception.login3time5mException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    public OtpService otpService;
    @Autowired
    public CustomerRepository customerRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    public JwtService jwtService;
    @Autowired
    public AuthenticationManager authenticationManager;
    @Autowired
    public TokenRepo tokenRepo;
    private Map<String, LoginInfo> loginInfoMap =  new HashMap<>();

    public AuthenticationResponse register (Customer request){
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String keyinfo = otpService.hashSHA256(key.getKey()+request.getEmail());

        var user = customerRepository.save(Customer
                .builder()
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fonos(0)
                .privateKey(keyinfo)
                .build());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }
    public AuthenticationResponse Login(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = customerRepository.findByEmail(request.getEmail()).orElseThrow(()-> new FindByNoId("not found"));
        if (checkLogin3tAnd5M(request.getEmail())) {
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
        }
        throw new login3time5mException("loi me roi");
    }
    private void saveUserToken(Customer user, String jwtToken) {
        var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false).build();
        tokenRepo.save(token);
    }

    private void revokeAllUserTokens(Customer user) {
        var validUserTokens = tokenRepo.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);
    }


    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extracUsername(refreshToken);
        if (userEmail != null) {
            var user = this.customerRepository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    public boolean checkLogin3tAnd5M(String username) {
        if (loginInfoMap.containsKey(username)) {
            LoginInfo info = loginInfoMap.get(username);
            if (info.getAttempts() >= 3){
                if (info.getLastAttempt().plusMinutes(5).isAfter(LocalDateTime.now())){
                    return false;
                }else {
                    info.setAttempts(0);
                }
            }
            info.setAttempts(info.getAttempts() + 1);
            info.setLastAttempt(LocalDateTime.now());
        } else {
            // Người dùng chưa đăng nhập lần nào, tạo thông tin đăng nhập mới
            loginInfoMap.put(username, new LoginInfo(1, LocalDateTime.now()));
        }

        // Đăng nhập thành công
        return true;
    }
}

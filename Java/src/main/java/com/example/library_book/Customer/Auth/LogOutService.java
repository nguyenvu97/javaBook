package com.example.library_book.Customer.Auth;

import com.example.library_book.Customer.Token.TokenRepo;
import com.example.library_book.Oauth2.Oauth2Token.Oauth2TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogOutService implements LogoutHandler {
    @Autowired
    public TokenRepo tokenRepo;
    @Autowired
    public Oauth2TokenRepository oauth2TokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final  String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null && !authHeader.startsWith("Bearer ")){
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepo.findByToken(jwt).orElse(null);
        if (storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepo.save(storedToken);
            SecurityContextHolder.clearContext();
        }
        var oauthtoken = oauth2TokenRepository.findByToken(jwt);
        if (oauthtoken != null){
            oauthtoken.setExpired(true);
            oauth2TokenRepository.save(oauthtoken);
            SecurityContextHolder.clearContext();
        }
    }
}

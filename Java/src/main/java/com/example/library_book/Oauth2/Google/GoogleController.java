package com.example.library_book.Oauth2.Google;

import com.example.library_book.Customer.Auth.AuthenticationResponse;
import com.example.library_book.Customer.Token.TokenType;
import com.example.library_book.Exception.FindByNoId;
import com.example.library_book.Oauth2.LoginStatus;
import com.example.library_book.Oauth2.Oauth2;
import com.example.library_book.Oauth2.Oauth2Repository;
import com.example.library_book.Oauth2.Oauth2Token.Oauth2Token;
import com.example.library_book.Oauth2.Oauth2Token.Oauth2TokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
public class GoogleController {
//    @Autowired
//    public GoogleRepo googleRepo;
    @Autowired
    public RestTemplate restTemplate;
    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;
    @Autowired
    public Oauth2Repository oauth2Repository;
    @Autowired
    public Oauth2TokenRepository oauth2TokenRepository;
    /*
    sendRedirect Google strat for api
    * */
    @GetMapping("/login")
    public void googleLogin(HttpServletResponse response) throws IOException {
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "scope=https%3A//www.googleapis.com/auth/drive.metadata.readonly&" +
                "access_type=offline&include_granted_scopes=true&response_type=code&state=state_parameter_passthrough_value&" +
                "redirect_uri=http://localhost:8083/login/oauth2/code/google&client_id=438887675887-l9f6rhmcuno8rkpuq3ts520okdupdots.apps.googleusercontent.com";
        response.sendRedirect(authUrl);
    }
    /*

    toke a code Auth 2 keyword = "code"
    * */
    @GetMapping("/login/oauth2/code/google")
    public AuthenticationResponse handleGoogleAuthorizationCode(@RequestParam("code") String authorizationCode, HttpServletResponse response) {
        System.out.println("Authorization Code: " + authorizationCode);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.put("code", Collections.singletonList(authorizationCode));
        body.put("client_id", Collections.singletonList(clientId));
        body.put("client_secret", Collections.singletonList(clientSecret));
        body.put("redirect_uri", Collections.singletonList(redirectUri));
        body.put("grant_type", Collections.singletonList("authorization_code"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        org.springframework.http.HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                entity,
                Map.class
        );
        Map<String, Object> responseBody = responseEntity.getBody();
        String accessToken = (String) responseBody.get("access_token");
        String refestoken = (String)  responseBody.get("refresh_token");
        System.out.println("access_token " + accessToken);
        System.out.println("refestoken " + refestoken);
        userInfo(accessToken,refestoken);
       return AuthenticationResponse
               .builder()
               .accessToken(accessToken)
               .refreshToken(refestoken)
               .build();
    }

    /*
    save user info google accessToken
    * */
    public void userInfo(String token,String refestoken){
        String userInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo";
        HttpHeaders headers = new HttpHeaders();
        System.out.println(token);
        headers.set("Authorization", "Bearer " +token);
        org.springframework.http.HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Map> response1 = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET,httpEntity,Map.class
        );
        if (response1.getBody() == null){
            System.out.println("loi me roi");
        }
        Map<String, Object> userInfo = response1.getBody();
        String email = (String) userInfo.get("email");
        String sub = (String) userInfo.get("sub");
        String id = (String) userInfo.get("id");
        String picture =(String) userInfo.get("picture");
        Oauth2 oauth2 = oauth2Repository.findByUserId(id);
        if (oauth2 == null){
            oauth2Repository.save(Oauth2
                    .builder()
                            .userId(id)
                            .email(email)
                            .fonos(0)
                            .picture(picture)
                            .loginStatus(LoginStatus.Google)
                    .build());
            oauth2TokenRepository.save(Oauth2Token
                    .builder()
                            .userId(id)
                            .expired(false)
                            .tokenType(TokenType.BEARER)
                            .token(token)
                            .refreshToken(refestoken)
                            .created_at(LocalDateTime.now())
                    .build());
        }else {
            oauth2TokenRepository.save(Oauth2Token
                    .builder()
                    .userId(id)
                    .expired(false)
                    .tokenType(TokenType.BEARER)
                    .token(token)
                    .created_at(LocalDateTime.now())
                    .refreshToken(refestoken)
                    .build());
        }
        System.out.println(response1.getBody());
    }
    /*
    refresh_token api google
    * */

    public AuthenticationResponse refresh_token ( String refreshToken ){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.put("client_id", Collections.singletonList(clientId));
        body.put("client_secret", Collections.singletonList(clientSecret));
        body.put("grant_type", Collections.singletonList("refresh_token"));
        body.put("refresh_token", Collections.singletonList(refreshToken));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        org.springframework.http.HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                entity,
                Map.class
        );
        Map<String, Object> responseBody = responseEntity.getBody();
        String accessToken = (String) responseBody.get("access_token");
        System.out.println("access_token " + accessToken);
        return AuthenticationResponse
                .builder()
                .accessToken(accessToken)
                .build();
    }

    //Check tg token xong de con lay lai token sua la jwtdecode

    public void checkTimeLIneToken(String token){
        var oauth2Token = oauth2TokenRepository.findByToken(token);
        if (oauth2Token == null){
            throw new FindByNoId("token not found" + token);
        }
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime expTime = oauth2Token.getCreated_at().plusMinutes(45);
        if (timeNow.isAfter(expTime)){
            refresh_token(oauth2Token.getRefreshToken());
        }else {
            oauth2Token.setExpired(true);
            oauth2TokenRepository.save(oauth2Token);
        }
    }
}

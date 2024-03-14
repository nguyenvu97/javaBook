package com.example.library_book.Customer.Auth;

import com.example.library_book.Customer.Customer;
import com.example.library_book.Exception.login3time5mException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final  LogOutService logOutService;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody Customer request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(service.Login(request, response));
        } catch (login3time5mException e) {
            return ResponseEntity.badRequest().body("Xác thực người dùng không thành công vi da vuot qua so lan dang nhap " );
        }
    }
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }
    @GetMapping("/logout")
    public void logoutUser(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        logOutService.logout(request, response, authentication);
    }

}

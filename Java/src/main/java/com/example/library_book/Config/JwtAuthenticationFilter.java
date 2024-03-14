package com.example.library_book.Config;


import com.example.library_book.Customer.Token.TokenRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtservice;
    private final UserDetailsService userDetailsService;
    private final TokenRepo tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authheader = request.getHeader("Authorization");
        final String jwt;
        final String username;
            /*
            HttpServletRequest have  startsWith and substring ;
             substring use when delete 7 string form start ;
             startsWith find by String about "BEARER"
            * */
        if (authheader == null || !authheader.startsWith("BEARER")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authheader.substring(7);
        username = jwtservice.extracUsername(jwt);
            /* username  = jwtservice.extractUsername(jwt); take jwtservice.extractUsername(jwt)
            public String extractUsername(String token) {return extractClaim(token, Claims::getSubject)}
            Claims::getSubject được sử dụng để trích xuất Subject (một phần quan trọng trong chuỗi JWT, thường chứa tên người dùng) từ chuỗi JWT.
*/
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            var isTokenValid = tokenRepository.findByToken(jwt).map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);

            /*
            var isTokenValid = tokenRepository.findByToken(jwt) when isExpired and isRevoked == false ok
            // co nghi la khi 2 gia tri isExpired and isRevoked  la false trong db thi co nghi la dung
            username  = jwtservice.extractUsername(jwt) check when username != null and seciritycontextholder == null ;
             seciritycontextholder is when login ok will save  securitycontext when logout securitycontext will delete
            * */
            if (jwtservice.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                /*
                userDetails save user of information ,
                 null not need  password because use userDetails.getAuthorities() login form token
                 t
                * */
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)

                        /*

                        save information vd login time , ip , extract
                        * */);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                /*
                save token when secirity check know extract
                * */
            }
        }
        filterChain.doFilter(request, response);
    }
}
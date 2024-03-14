package com.example.library_book.Oauth2.Oauth2Token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface Oauth2TokenRepository extends JpaRepository<Oauth2Token,Integer> {
    @Query("select o.userId,o.exp from Oauth2Token as o where o.expired = false ")
    Oauth2Token findByToken(String token);
}

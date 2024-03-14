package com.example.library_book.Oauth2;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Oauth2Repository extends JpaRepository<Oauth2,Integer> {
    Oauth2 findByUserId(String id);



    Oauth2 findByUserIdAndEmail(String id, String sub);
}

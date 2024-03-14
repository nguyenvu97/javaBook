package com.example.library_book.Premiun.UserPremiun;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPremiunRepository extends JpaRepository<UserPremiun,Integer> {
    Optional<UserPremiun> findByUserId(Integer id);
}

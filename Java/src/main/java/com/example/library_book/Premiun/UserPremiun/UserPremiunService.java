package com.example.library_book.Premiun.UserPremiun;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional

public class UserPremiunService {
    @Autowired
    public UserPremiunRepository userPremiunRepository;
}

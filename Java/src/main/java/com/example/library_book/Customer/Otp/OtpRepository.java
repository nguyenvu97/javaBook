package com.example.library_book.Customer.Otp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp,Integer> {
    Otp findByNumberAndExp(int otp, boolean b);
}

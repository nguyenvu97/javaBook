package com.example.library_book.Customer.Auth;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginInfo {
    private int attempts;
    private LocalDateTime lastAttempt;
}

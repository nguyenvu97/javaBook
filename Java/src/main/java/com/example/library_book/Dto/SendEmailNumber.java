package com.example.library_book.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SendEmailNumber {
    private int attempts;
    private LocalDateTime lastAttempt;
}

package com.example.library_book.Dto;

import com.example.library_book.Oauth2.LoginStatus;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserByPreminuDto {
    public int userId;
    private String email;
    private LoginStatus loginStatus;
    private String premiunStatus;
    private LocalDateTime premiunTime;
    private String picture;
    private int fonos;

}

package com.example.library_book.Dto;

import lombok.*;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class MemberData {
    private Integer id;
    private String sub;
    private Long iat;
    private Long exp;
}

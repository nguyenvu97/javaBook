package com.example.library_book.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FindByNoId extends RuntimeException{
    String data;
}

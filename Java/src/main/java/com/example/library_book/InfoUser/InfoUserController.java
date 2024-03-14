package com.example.library_book.InfoUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/infoUser")
public class InfoUserController {
    @Autowired
    public InfoUserService infoUserService;

    @PostMapping("create")
    public ResponseEntity<InfoUser>createInfoUser(@RequestBody InfoUser infoUser ,
                                                  @RequestHeader("") String token,
                                                  @RequestParam String loginStatus){
        return ResponseEntity.ok().body(infoUserService.addInfoUser(infoUser,token,loginStatus));
    }
}

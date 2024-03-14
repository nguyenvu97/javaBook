package com.example.library_book.InfoUser;

import com.example.library_book.Dto.MemberData;
import com.example.library_book.Exception.FindByNoId;
import com.example.library_book.JwtDecode.JwtDecode;
import com.example.library_book.Oauth2.Oauth2Repository;
import com.example.library_book.Oauth2.Oauth2Token.Oauth2TokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class InfoUserService {
    @Autowired
    public InfoUserRepository infoUserRepository;
    @Autowired
    private JwtDecode jwtDecode;

    public InfoUser addInfoUser(InfoUser infoUser,String token,String loginStatus){
        if (infoUser == null && token == null ){
            throw  new FindByNoId("not found" + token);
        }
            MemberData memberData = jwtDecode.decode(token,loginStatus);
            return infoUserRepository.save(InfoUser
                    .builder()
                            .phone(infoUser.getPhone())
                            .address(infoUser.getAddress())
                            .userId(String.valueOf(memberData.getId()))
                    .build());
        }


}

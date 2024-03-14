package com.example.library_book.JwtDecode;

import com.example.library_book.Customer.Customer;
import com.example.library_book.Customer.CustomerRepository;
import com.example.library_book.Dto.MemberData;
import com.example.library_book.Exception.FindByNoId;
import com.example.library_book.Oauth2.Google.GoogleController;
import com.example.library_book.Oauth2.LoginStatus;
import com.example.library_book.Oauth2.Oauth2;
import com.example.library_book.Oauth2.Oauth2Repository;
import com.example.library_book.Oauth2.Oauth2Token.Oauth2Token;
import com.example.library_book.Oauth2.Oauth2Token.Oauth2TokenRepository;
import com.example.library_book.Premiun.PremiunService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Base64;
import java.util.Optional;

@Service
public class JwtDecode {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private Oauth2TokenRepository oauth2TokenRepository;
    @Autowired
    private Oauth2Repository oauth2Repository;
    @Autowired
    private GoogleController googleController;
    @Autowired
    private PremiunService premiunService;


    public MemberData decode( String token,String loginStatus) {
        if (loginStatus.equals(LoginStatus.Github) || loginStatus.equals(LoginStatus.Google)) {
            Oauth2Token oauth2Token = oauth2TokenRepository.findByToken(token);
            if (oauth2Token == null){
                throw new FindByNoId("not found");
            }
            Oauth2 oauth2 = oauth2Repository.findByUserId(oauth2Token.getUserId());
            MemberData memberData = new MemberData();
            memberData.setSub(oauth2.getEmail());
            memberData.setId(Integer.valueOf(oauth2Token.getUserId()));
            memberData.setExp(Long.valueOf(oauth2Token.getExp()));
            googleController.checkTimeLIneToken(token);
            premiunService.checkTimeLine(token,loginStatus);
            return memberData;
        } else {
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length == 3) {
                String payload = tokenParts[1];
                byte[] decodedPayload = Base64.getDecoder().decode(payload);
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    MemberData memberData = objectMapper.readValue(decodedPayload, MemberData.class);
                    Optional<Customer> foundMember = customerRepository.findByEmail(memberData.getSub());
                    if (foundMember.isPresent()) {
                        Customer user = foundMember.get();
                        memberData.setId(user.getId());
                        premiunService.checkTimeLine(token,loginStatus);
                        return memberData;
                    }
                } catch (Exception e) {
                    e.getMessage();
                    return null;
                }
            }

        }
        return null;
    }



}

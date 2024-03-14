package com.example.library_book.Premiun;

import com.example.library_book.Customer.Auth.LoginInfo;
import com.example.library_book.Customer.Customer;
import com.example.library_book.Customer.CustomerRepository;
import com.example.library_book.Dto.MemberData;
import com.example.library_book.Dto.SendEmailNumber;
import com.example.library_book.Dto.UserByPreminuDto;
import com.example.library_book.Exception.FindByNoId;
import com.example.library_book.JwtDecode.JwtDecode;
import com.example.library_book.Oauth2.LoginStatus;
import com.example.library_book.Oauth2.Oauth2;
import com.example.library_book.Oauth2.Oauth2Repository;
import com.example.library_book.OrderBook.OrderStatus;
import com.example.library_book.Premiun.UserPremiun.UserPremiun;
import com.example.library_book.Premiun.UserPremiun.UserPremiunRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PremiunService {
    @Autowired
    public PremiunRepository premiunRepository;
    @Autowired
    private JwtDecode jwtDecode;
    @Autowired
    private Oauth2Repository oauth2Repository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserPremiunRepository userPremiunRepository;
    Map<String, SendEmailNumber> SendEmailNumberMap = new HashMap<>();


    public List<Premiun> createPremiun(List<Premiun>premiuns){
        List<Premiun> savedPremiuns = premiuns.stream()
                .map(premiun -> premiunRepository.save(premiun))
                .collect(Collectors.toList());

        return savedPremiuns;
    }
    public String byPreminu(String token, String loginStatus,int preminuId){
        if (preminuId <=0){
            throw new FindByNoId("not found" + preminuId);
        }
        MemberData memberData = jwtDecode.decode(token,loginStatus);
        Premiun premiun = premiunRepository.findById(preminuId).orElseThrow(null);
        if (loginStatus.equals(LoginStatus.Github)|| loginStatus.equals(LoginStatus.Google)){
            Oauth2 oauth2 = oauth2Repository.findByUserIdAndEmail(String.valueOf(memberData.getId()),memberData.getSub());
            PremiunStatus(oauth2,premiun,preminuId);
        }else {
            Customer oauth2 = customerRepository.findByEmailAndId(memberData.getSub(),memberData.getId());
            oauth2.setPremiunStatus(premiun.getPremiunStatus());
            if (premiun.getPremiunStatus() == "PremiunPro"){
                oauth2.setFonos(2);
                convetUser(preminuId,premiun, oauth2.getId());
                customerRepository.save(oauth2);
                return "ok";
            }else {
                oauth2.setFonos(1);
                convetUser(preminuId,premiun, oauth2.getId());
                customerRepository.save(oauth2);
                return "ok";
            }
        }
    return null;
    }
    private String PremiunStatus(Oauth2 oauth2,Premiun premiun ,int preminuId){
        oauth2.setPremiunStatus(premiun.getPremiunStatus());
        if (premiun.getPremiunStatus() == "PremiunPro"){
            oauth2.setFonos(2);
            convetUser(preminuId,premiun, Integer.parseInt(oauth2.getUserId()));
            oauth2Repository.save(oauth2);
            return "ok";
        }else {
            oauth2.setFonos(1);
            convetUser(preminuId,premiun, Integer.parseInt(oauth2.getUserId()));
            oauth2Repository.save(oauth2);
            return "ok";
        }
    }
    public void convetUser(int preminuId,Premiun premiun,int userId){
        UserPremiun userPremiun = new UserPremiun();
        userPremiun.setPremiunId(preminuId);
        userPremiun.setPremiunSatus(premiun.getPremiunStatus());
        userPremiun.setUserId(userId);
        userPremiun.setPremiunPrice(premiun.getPremiunPrice());
        userPremiun.setOrderStatus(OrderStatus.OK);
        userPremiun.setCreateTime(LocalDateTime.now());
       userPremiunRepository.save(userPremiun);
    }

    public void checkTimeLine(String token,String loginStatus){
        MemberData memberData = jwtDecode.decode(token,loginStatus);
        UserPremiun userPremiun = userPremiunRepository.findByUserId(memberData.getId()).orElseThrow(()-> new FindByNoId("not foudn"+ token));

        if (  loginStatus.equals(LoginStatus.Google)||loginStatus.equals(LoginStatus.Github)){
            Oauth2 oauth2 = oauth2Repository.findByUserIdAndEmail(String.valueOf(memberData.getId()),memberData.getSub());
            timeConvetOauth2(userPremiun,oauth2);
        }else {
            Customer customer = customerRepository.findByEmailAndId(memberData.getSub(),memberData.getId());
            timeConvetCustomer(userPremiun,customer);
        }
    }
    private void timeConvetOauth2(UserPremiun userPremiun,Oauth2 oauth2) {
        LocalDateTime expirationTime = LocalDateTime.now().plus(1, ChronoUnit.MONTHS);
        LocalDateTime expiredTime = LocalDateTime.now().plus(1, ChronoUnit.YEARS);
        LocalDateTime time = userPremiun.getCreateTime();
        if (userPremiun.getPremiunSatus().equals("PremiunPro")) {

            if (time.isBefore(expirationTime)) {
                int fons = oauth2.getFonos() + 2;
                oauth2.setFonos(fons);
            }
            if (time.isBefore(expiredTime)) {
                // Send email for user
                checkSendNumber(oauth2.getEmail(),oauth2);
            }
        } else {
            if (time.isBefore(expirationTime)){
                //send email
                checkSendNumber(oauth2.getEmail(),oauth2);
            }

        }
    }
    private void timeConvetCustomer(UserPremiun userPremiun,Customer oauth2) {
        LocalDateTime expirationTime = LocalDateTime.now().plus(1, ChronoUnit.MONTHS);
        LocalDateTime expiredTime = LocalDateTime.now().plus(1, ChronoUnit.YEARS);
        LocalDateTime time = userPremiun.getCreateTime();
        if (userPremiun.getPremiunSatus().equals("PremiunPro")) {

            if (time.isBefore(expirationTime)) {
                int fons = oauth2.getFonos() + 2;
                oauth2.setFonos(fons);
            }
            if (time.isBefore(expiredTime)) {
                checkSendNumber(oauth2.getEmail(),oauth2);
            }
        } else if ("Premiun".equals(userPremiun.getPremiunSatus())){
            if (time.isBefore(expirationTime)){
                //send email
                sendEmail();
            }
        else {
            return;
            }
        }
    }
    private String sendEmail(){
        return "send Email";
    }

    private Boolean checkSendNumber(String email,Oauth2 oauth2){

       if (SendEmailNumberMap.containsKey(email)){
           SendEmailNumber sendEmailNumber = SendEmailNumberMap.get(email);
           if (sendEmailNumber.getAttempts() >2){
               oauth2.setPremiunStatus("free");
               oauth2Repository.save(oauth2);
               sendEmail();
               return true;
           }
       }
        return false;
    }
    private Boolean checkSendNumber(String email,Customer oauth2){

        if (SendEmailNumberMap.containsKey(email)){
            SendEmailNumber sendEmailNumber = SendEmailNumberMap.get(email);
            if (sendEmailNumber.getAttempts() >2){
                oauth2.setPremiunStatus("free");
                customerRepository.save(oauth2);
                sendEmail();
                return true;
            }
        }
        return false;
    }
}



package com.example.library_book.Customer.Otp;

import com.example.library_book.Customer.Customer;
import com.example.library_book.Customer.CustomerRepository;
import com.example.library_book.Exception.FindByNoId;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class OtpService {
    @Autowired
    public OtpRepository otpReposittory;
    @Autowired
    public CustomerRepository customerRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;
    public  String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean authenticateUser(String email){
        Customer customer = customerRepository.findByEmail(email).orElseThrow(()-> new FindByNoId("not found" + email));
        generateAndSaveOTP(customer.getEmail());
        return true;
    }

    private int generateAndSaveOTP(String email) {
        Customer user = customerRepository.findByEmail(email).orElseThrow(() -> new FindByNoId("not user in in db" + email));
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(zoneId);
        long currentTimeMillis = zonedDateTime.toEpochSecond() * 1000;
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        int otp = gAuth.getTotpPassword(user.getPrivateKey(),currentTimeMillis);
        otpReposittory.save(Otp
                .builder()
                .createAt(LocalDateTime.now())
                .exp(false)
                .number(otp)
                .email(email)
                .build());
        log.info("ma otp la " +otp);
        return otp;
    }
    public String verifyOTP(int otp,String password) {
        Otp totp = otpReposittory.findByNumberAndExp(otp,false);
        if (totp == null) {
            throw new FindByNoId("OTP not found");
        }
        LocalDateTime createTotpTime = totp.getCreateAt();
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(createTotpTime, currentTime);
        long minutesDifference = duration.toMinutes();

        if (CheckTime(minutesDifference)) {
            Customer user = customerRepository.findByEmail(totp.getEmail()).orElseThrow(null);

            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            boolean isValid = gAuth.authorize(user.getPrivateKey(), otp);
            if (isValid) {
                user.setPassword(passwordEncoder.encode(password));
                customerRepository.save(user);
                totp.setExp(true);
                otpReposittory.save(totp);
                return "change the password OK";
            }
        }
        totp.setExp(true);
        otpReposittory.save(totp);
        throw  new RuntimeException("Expired otp code");
    }


    private boolean CheckTime(long time ) {
        return time <= 2;
    }
}

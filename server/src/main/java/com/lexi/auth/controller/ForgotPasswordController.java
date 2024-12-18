package com.lexi.auth.controller;

import com.lexi.auth.dto.MailBody;
import com.lexi.auth.entities.ForgetPassword;
import com.lexi.auth.model.User;
import com.lexi.auth.repository.ForgotPasswordRepository;
import com.lexi.auth.repository.UserRepository;
import com.lexi.auth.service.EmailService;
import com.lexi.auth.util.ChangePassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("auth")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final UserRepository userRepositoty;
    private final PasswordEncoder passwordEncoder;



    private ForgotPasswordController(UserRepository userRepository, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder,EmailService emailService) {
    this.userRepositoty=userRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService=emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
    }

    //send mail fro email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String>verifyEmail(@PathVariable String email) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Please Provide an Valid Email!"));

        int otp=otpGenerator();
        MailBody mailBody=MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forget Password request :"+otp)
                .subject("OTP for Forgot Password request")
                .build();
        ForgetPassword fp= ForgetPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis()+ 10* 60*1000))
                .user(user)
                .build();
        emailService.sendSimpleMail(mailBody);
        forgotPasswordRepository.save(fp);
        return ResponseEntity.ok("Email sent for Verification");

    }
    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String>verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Please Provide an Valid Email!"));
        ForgetPassword fp=forgotPasswordRepository.findByOtpAndUser(otp,user)
                .orElseThrow() ->new RuntimeException("Invalid OTP for email: " +email);

        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok("OTP has been verified");
    }
   public ResponseEntity<String>changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                      @PathVariable String email){
        if(!Objects.equals(changePassword.password(),changePassword.repeatPassword())){
            return new ResponseEntity<>("Please enter the password again!",HttpStatus.EXPECTATION_FAILED );
        }
        String encodedPassword=passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email,encodedPassword);

        return ResponseEntity.ok("Password has been changed");
   }

    private Integer otpGenerator(){
        Random random=new Random();
        return random.nextInt(100000,999999);
    }
}

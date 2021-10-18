package com.raredevelopers.callrupee.controller;

import com.raredevelopers.callrupee.model.Auth;
import com.raredevelopers.callrupee.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    AuthRepository authRepository;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody Auth auth) {
        String result = "Failed to send OTP";
        String phoneNumber = auth.getPhoneNumber();
        String code = generateCode();
        if (sendOtpToPhone(phoneNumber, code)) {
            result = "Success";
            auth.setCode(code);
            authRepository.save(auth);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Auth auth) {
        String result = "Invalid OTP";
        String enteredPhoneNumber = auth.getPhoneNumber();
        String enteredCode = auth.getCode();
        Optional<Auth> authFromDB = authRepository.findByPhoneNumber(enteredPhoneNumber);
        if (authFromDB.isPresent() && enteredCode != null) {
            if (enteredCode.equalsIgnoreCase(authFromDB.get().getCode())) {
                result = "Success";
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private static String generateCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }

    private boolean sendOtpToPhone(String phoneNumber, String code) {
        return true;
    }

}

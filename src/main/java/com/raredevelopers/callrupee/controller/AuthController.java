package com.raredevelopers.callrupee.controller;

import com.raredevelopers.callrupee.ConfigUtil;
import com.raredevelopers.callrupee.model.Auth;
import com.raredevelopers.callrupee.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private ConfigUtil configUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody Auth auth) {
        String result = "Failed to send OTP";
        String phoneNumber = auth.getPhoneNumber();
        String code = generateCode();
        String host = configUtil.getProperty("AUTH_HOST");
        if (sendOtpToPhone(host, phoneNumber, code)) {
            result = "Success";
            auth.setCode(code);
            auth.setCount(0);
            auth.setStatus(true);
            auth.setUsed(false);
            authRepository.save(auth);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody Auth auth) {
        String result = "Invalid OTP";
        String enteredPhoneNumber = auth.getPhoneNumber();
        String enteredCode = auth.getCode();
        Optional<Auth> authOptional = authRepository.findById(enteredPhoneNumber);
        if (authOptional.isPresent() && enteredCode != null) {
            Auth authFromDB = authOptional.get();
            if (authFromDB.isUsed()) {
                result = "Already used OTP";
            } else {
                if (authFromDB.isStatus()) {
                    if (enteredCode.equalsIgnoreCase(authFromDB.getCode())) {
                        result = "Success";
                        authFromDB.setUsed(true);
                    } else {
                        int attempts = Integer.parseInt(configUtil.getProperty("OTP_VERIFICATION_ATTEMPTS"));
                        authFromDB.setStatus(authFromDB.getCount() < attempts);
                    }
                    authFromDB.setCount(authFromDB.getCount() + 1);
                    authRepository.save(authFromDB);
                } else {
                    result = "Exceeds Attempts to Verify OTP";
                }
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private static String generateCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }

    private static boolean sendOtpToPhone(String host, String phoneNumber, String code) {
        String message = "Hi, " + code + " is your Call Rupee verification code. Enjoy Earning.";

        if (phoneNumber.startsWith("+"))
            phoneNumber = phoneNumber.substring(1);

        String data = "{\"phone\": \"" + phoneNumber + "\",\"body\": \"" + message + "\"}";

        HttpURLConnection http = null;
        try {
            URL url = new URL(host);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");
            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            return http.getResponseCode() == 200;
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
        return false;
    }
}

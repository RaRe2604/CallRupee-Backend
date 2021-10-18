package com.raredevelopers.callrupee.model;

import com.mongodb.lang.Nullable;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "auth")
public class Auth {
    private String phoneNumber;
    @Nullable
    private String code;

    public Auth(String phoneNumber, @Nullable String code) {
        this.phoneNumber = phoneNumber;
        this.code = code;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getCode() {
        return code;
    }

    public void setCode(@Nullable String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}

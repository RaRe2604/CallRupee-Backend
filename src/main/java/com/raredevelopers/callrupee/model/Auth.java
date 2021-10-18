package com.raredevelopers.callrupee.model;

import com.mongodb.lang.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "auth")
public class Auth {
    @Id
    private String phoneNumber;
    @Nullable
    private String code;
    @Nullable
    private int count;
    @Nullable
    private boolean status;
    @Nullable
    private boolean used;

    public Auth(String phoneNumber, @Nullable String code) {
        this.phoneNumber = phoneNumber;
        this.code = code;
        this.count = 0;
        this.status = true;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}

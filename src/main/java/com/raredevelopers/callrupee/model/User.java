package com.raredevelopers.callrupee.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "users")
public class User {
  @Id
  private String id;
  private String name;
  @Indexed
  private String phone;
  private String email;
  private boolean active;
  private boolean admin;
  private float balance;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date createdDate;

  public User() {
    this.createdDate = new Date();
  }

  public User(String name, String phone, String email, boolean active, boolean admin, float balance) {
    this.name = name;
    this.phone = phone;
    this.email = email;
    this.active = active;
    this.admin = admin;
    this.balance = balance;
    this.createdDate = new Date();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public float getBalance() {
    return balance;
  }

  public void setBalance(float balance) {
    this.balance = balance;
  }
}

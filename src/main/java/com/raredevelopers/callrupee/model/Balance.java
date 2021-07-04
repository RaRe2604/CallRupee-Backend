package com.raredevelopers.callrupee.model;

import com.mongodb.lang.Nullable;
import com.raredevelopers.callrupee.constant.Process;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "balance")
public class Balance {
  @Id
  private String id;
  @Indexed
  private String fromPhone;
  private String toPhone;
  @Nullable
  private float amount;
  @Nullable
  private int duration;
  private Process process;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Date createdDate;

  public Balance() {
    this.createdDate = new Date();
  }

  public Balance(String fromPhone, String toPhone, float amount, int duration, Process process) {
    this.fromPhone = fromPhone;
    this.toPhone = toPhone;
    this.amount = amount;
    this.duration = duration;
    this.process = process;
    this.createdDate = new Date();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFromPhone() {
    return fromPhone;
  }

  public void setFromPhone(String fromPhone) {
    this.fromPhone = fromPhone;
  }

  public String getToPhone() {
    return toPhone;
  }

  public void setToPhone(String toPhone) {
    this.toPhone = toPhone;
  }

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public Process getProcess() {
    return process;
  }

  public void setProcess(Process process) {
    this.process = process;
  }
}

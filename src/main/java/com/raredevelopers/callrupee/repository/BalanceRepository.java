package com.raredevelopers.callrupee.repository;

import com.raredevelopers.callrupee.model.Balance;
import com.raredevelopers.callrupee.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BalanceRepository extends MongoRepository<Balance, String> {
  List<Balance> findByFromPhone(String phone);
}

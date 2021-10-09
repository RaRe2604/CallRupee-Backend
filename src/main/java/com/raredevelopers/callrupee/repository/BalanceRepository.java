package com.raredevelopers.callrupee.repository;

import com.raredevelopers.callrupee.model.Balance;
import com.raredevelopers.callrupee.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BalanceRepository extends MongoRepository<Balance, String> {
  List<Balance> findByFromPhone(String phone);
  List<Balance> findByProcess(String process);
  Optional<Balance> findById(String id);
}

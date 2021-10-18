package com.raredevelopers.callrupee.repository;

import com.raredevelopers.callrupee.model.Auth;
import com.raredevelopers.callrupee.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AuthRepository extends MongoRepository<Auth, String> {
  Optional<Auth> findByPhoneNumber(String phone);
}

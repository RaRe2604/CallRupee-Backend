package com.raredevelopers.callrupee.repository;

import java.util.List;
import java.util.Optional;

import com.raredevelopers.callrupee.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
  List<User> findByActive(boolean active);
  Optional<User> findByPhone(String phone);
}

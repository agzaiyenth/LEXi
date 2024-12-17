package com.lexi.predict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lexi.predict.model.UserResponse;

public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {
}


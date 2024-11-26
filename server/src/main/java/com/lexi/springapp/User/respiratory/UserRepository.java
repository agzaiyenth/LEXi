package com.lexi.springapp.User.respiratory;


import com.lexi.springapp.User.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be added here if needed
}
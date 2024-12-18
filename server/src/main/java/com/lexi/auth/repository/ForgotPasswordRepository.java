package com.lexi.auth.repository;

import com.lexi.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ForgotPasswordRepository<User> extends JpaRepository<User ,Integer> {
    @Query("select fp from User fp where fp.otp=?1 and fp.user=?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);
}

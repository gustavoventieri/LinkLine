package com.gustavoventieri.framework.driver.repository.client;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gustavoventieri.framework.entity.ResetPassword;


public interface ResetPasswordOrm extends JpaRepository<ResetPassword, Long>{

    Optional<ResetPassword> findByEmail(String email);
    Optional<ResetPassword> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
} 

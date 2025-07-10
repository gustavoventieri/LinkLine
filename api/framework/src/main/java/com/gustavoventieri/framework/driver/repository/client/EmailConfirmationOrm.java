package com.gustavoventieri.framework.driver.repository.client;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gustavoventieri.framework.entity.EmailConfirmation;


public interface EmailConfirmationOrm extends JpaRepository<EmailConfirmation, Long> {

    Optional<EmailConfirmation> findByEmail(String email);
    Optional<EmailConfirmation> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
} 

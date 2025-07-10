package com.gustavoventieri.framework.driver.repository.client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gustavoventieri.framework.entity.User;


public interface UserRepositoryOrm extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCaseAndUsernameBetween(String partial, String start, String end);

    List<User> findByUsernameContainingAndIdNot(String username, UUID id);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :email")
    void updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

}

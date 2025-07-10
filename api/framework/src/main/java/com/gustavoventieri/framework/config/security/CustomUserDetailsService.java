package com.gustavoventieri.framework.config.security;



import java.util.ArrayList;
import java.util.UUID;

import org.gustavoventieri.domain.entity.UserDomain;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.gustavoventieri.framework.driver.repository.UserRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryImpl userRepositoryImpl;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        UUID userIdToUUID = UUID.fromString(userId);
        UserDomain user = userRepositoryImpl.findById(userIdToUUID)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        return new org.springframework.security.core.userdetails.User(
            user.email(),
            user.password(),
            new ArrayList<>()
        );
    }
}
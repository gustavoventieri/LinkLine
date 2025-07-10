package org.gustavoventieri.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.UserDomain;

public interface UserRepository {

    UserDomain save(UserDomain user);

    Optional<UserDomain> findByEmail(String email);

    Optional<UserDomain> findByUsername(String username);

    Optional<UserDomain> findById(UUID userId);

    void updatePasswordByEmail(String email, String newPassword);

    List<UserDomain> searchByApproximateUsername(String searchTerm, UUID currentUserId);
}
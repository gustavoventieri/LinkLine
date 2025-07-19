package org.gustavoventieri.domain.service;

import java.util.List;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.PotentialFriendResponse;

public interface UserService {
    List<PotentialFriendResponse> findUsersByUsername(String searchTerm, UUID currentUserId, int searchLimit);

    void updateUserPasswordByEmail(String email, String newPassword);
}

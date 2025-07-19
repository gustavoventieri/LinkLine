package org.gustavoventieri.domain.service;

import java.util.List;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.GetAllFriendships;

public interface FriendshipService {

    void createFriendship(UUID userId, String friendUsername);

    void updateFriendship(UUID friendshipId, String newStatus);

    void deleteFriendship(UUID userId, UUID friendId);

    List<GetAllFriendships> getAllByUserId(UUID userId);
}

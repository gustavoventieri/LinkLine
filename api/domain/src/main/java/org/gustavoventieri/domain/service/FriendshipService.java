package org.gustavoventieri.domain.service;

import java.util.Map;
import java.util.UUID;

public interface FriendshipService {

    void createFriendship(UUID userId, String friendUsername);

    void updateFriendship(UUID chatRequestId, String newStatus);

    void deleteFriendship(UUID userId, UUID friendId);

    Map<String, Object> getFriendshipsByStatus(String type, String status, UUID userId);
}

package org.gustavoventieri.domain.repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.FriendshipDomain;
import org.gustavoventieri.domain.enums.RequestStatus;

public interface FriendshipRepository {

    List<FriendshipDomain> getChatRequestsByStatus(UUID userId, RequestStatus status, boolean sent);

    FriendshipDomain create(UUID senderId, UUID receiverId);

    Optional<FriendshipDomain> updateStatus(UUID requestId, RequestStatus status);

    Optional<FriendshipDomain> deleteById(UUID requestId);

    Optional<FriendshipDomain> findById(UUID requestId);

    Optional<FriendshipDomain> findExisting(UUID senderId, UUID receiverId);

    Optional<FriendshipDomain> findAcceptedBetweenUsers(UUID userId1, UUID userId2);
}

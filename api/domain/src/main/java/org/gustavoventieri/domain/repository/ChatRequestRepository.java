package org.gustavoventieri.domain.repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.ChatRequestDomain;
import org.gustavoventieri.domain.enums.RequestStatus;

public interface ChatRequestRepository {

    List<ChatRequestDomain> getChatRequestsByStatus(UUID userId, RequestStatus status, boolean sent);

    ChatRequestDomain create(UUID senderId, UUID receiverId);

    Optional<ChatRequestDomain> updateStatus(UUID requestId, RequestStatus status);

    Optional<ChatRequestDomain> deleteById(UUID requestId);

    Optional<ChatRequestDomain> findById(UUID requestId);

    Optional<ChatRequestDomain> findExisting(UUID senderId, UUID receiverId);

    Optional<ChatRequestDomain> findAcceptedBetweenUsers(UUID userId1, UUID userId2);
}

package org.gustavoventieri.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.FriendshipDomain;
import org.gustavoventieri.domain.enums.RequestStatus;

public interface FriendshipRepository {

    List<FriendshipDomain> getAllByUserId(UUID userId);

    void save(FriendshipDomain friendshipDomain);

    void updateStatus(UUID requestId, RequestStatus status);

    void deleteById(UUID requestId);

    Optional<FriendshipDomain> findById(UUID requestId);

    Optional<FriendshipDomain> findExisting(UUID userId1, UUID userId2, List<RequestStatus> statuses);

}

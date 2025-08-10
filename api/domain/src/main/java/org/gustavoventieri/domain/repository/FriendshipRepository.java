package org.gustavoventieri.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.FriendshipDomain;
import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.enums.RequestStatus;

public interface FriendshipRepository {

    List<FriendshipDomain> getAllByUserId(final UUID userId);

    void save(final FriendshipDomain friendshipDomain);

    void updateFriendship(final UUID requestId, final UserDomain sender, final UserDomain receiver,
            final RequestStatus status);

    Optional<FriendshipDomain> findById(final UUID requestId);

    List<FriendshipDomain> findExisting(final UUID userId1, final UUID userId2,
            final List<RequestStatus> statuses);

}

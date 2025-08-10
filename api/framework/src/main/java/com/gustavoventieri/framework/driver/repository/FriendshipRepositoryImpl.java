package com.gustavoventieri.framework.driver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.FriendshipDomain;
import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.enums.RequestStatus;
import org.gustavoventieri.domain.exception.Conflict;
import org.gustavoventieri.domain.exception.InternalServerError;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.repository.FriendshipRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.gustavoventieri.framework.adapter.mapper.FriendshipMapper;
import com.gustavoventieri.framework.adapter.mapper.UserMapper;
import com.gustavoventieri.framework.driver.repository.client.FriendshipOrm;
import com.gustavoventieri.framework.entity.Friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link FriendshipRepository} interface using the ORM
 * layer.
 * Responsible for managing Friendship entities' persistence and retrieval.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class FriendshipRepositoryImpl implements FriendshipRepository {

    private final FriendshipOrm friendshipOrm;

    /**
     * Saves a new friendship record to the database.
     *
     * @param friendshipDomain the friendship domain object to be saved.
     * @throws Conflict            if there is a data integrity violation during
     *                             save.
     * @throws InternalServerError if any other error occurs.
     */
    @Override
    public void save(final FriendshipDomain friendshipDomain) {
        log.debug("Saving friendship: {}", friendshipDomain);
        try {
            friendshipOrm.save(FriendshipMapper.toEntityComplete(friendshipDomain));
        } catch (DataIntegrityViolationException e) {
            log.error("Conflict detected while saving friendship: {}", friendshipDomain, e);
            throw new Conflict("Conflict detected while saving friendship", e);
        } catch (Exception e) {
            log.error("Internal error occurred while saving friendship: {}", friendshipDomain, e);
            throw new InternalServerError("Internal error occurred while saving friendship", e);
        }
    }

    /**
     * Updates the status of an existing friendship by its request ID.
     *
     * @param requestId the unique identifier of the friendship request.
     * @param status    the new status to set.
     * @throws NotFound            if the friendship with given ID does not exist.
     * @throws InternalServerError if any internal error occurs.
     */
    @Override
    public void updateFriendship(final UUID requestId, final UserDomain sender, final UserDomain receiver,
            final RequestStatus status) {
        try {
            log.debug("Checking existence of friendship {}", requestId);

            final Friendship friendship = friendshipOrm.findById(requestId)
                    .orElseThrow(() -> new NotFound("Friendship not found"));

            friendship.setStatus(status);
            friendship.setSender(UserMapper.toEntityBasic(sender));
            friendship.setReceiver(UserMapper.toEntityBasic(receiver));

            friendshipOrm.save(friendship);

            log.info("Friendship status with ID {} updated to {}", requestId, status);

        } catch (final Exception e) {
            log.error("Internal error occurred while updating friendship status {}", requestId, e);
            throw new InternalServerError("Internal error occurred while updating friendship status", e);
        }
    }

    /**
     * Finds a friendship by its unique request ID.
     *
     * @param requestId the unique identifier of the friendship request.
     * @return an Optional containing the FriendshipDomain if found, or empty
     *         otherwise.
     * @throws InternalServerError if any internal error occurs.
     */
    @Override
    public Optional<FriendshipDomain> findById(final UUID requestId) {
        try {
            log.debug("Checking existence of friendship {}", requestId);

            final Optional<Friendship> friendship = friendshipOrm.findById(requestId);

            return friendship.map(FriendshipMapper::toDomainComplete);

        } catch (final Exception e) {
            log.error("Internal error occurred while fetching friendship {}", requestId, e);
            throw new InternalServerError("Internal error occurred while fetching friendship", e);
        }
    }

    /**
     * Finds an existing friendship between two users with given statuses.
     *
     * @param userId1  the first user's UUID.
     * @param userId2  the second user's UUID.
     * @param statuses the list of statuses to filter by.
     * @return an Optional containing the FriendshipDomain if found, or empty
     *         otherwise.
     * @throws InternalServerError if any internal error occurs.
     */
    @Override
    public List<FriendshipDomain> findExisting(final UUID userId1, final UUID userId2,
            final List<RequestStatus> statuses) {
        try {
            log.debug("Checking existence of friendship between {} and {}", userId1, userId2);

            final List<Friendship> friendships = friendshipOrm.findByUsersAndStatuses(userId1, userId2,
                    statuses);

            return friendships.stream()
                    .map(FriendshipMapper::toDomainComplete)
                    .toList();

        } catch (final Exception e) {
            log.error("Internal error occurred while fetching friendship between {} and {}", userId1, userId2, e);
            throw new InternalServerError("Internal error occurred while fetching friendship", e);
        }
    }

    /**
     * Retrieves all friendships and friendship requests related to a user.
     *
     * @param userId the UUID of the user.
     * @return a list of FriendshipDomain objects.
     * @throws InternalServerError if any internal error occurs.
     */
    @Override
    public List<FriendshipDomain> getAllByUserId(final UUID userId) {
        try {
            log.debug("Fetching friendships and requests for user: {}", userId);
            final List<Friendship> friendships = friendshipOrm
                    .findAllBySender_IdOrReceiver_IdOrderByCreatedAtDesc(userId, userId);
            return friendships.stream()
                    .filter(friendship -> (friendship.getReceiver().getId().equals(userId)
                            && friendship.getStatus() == RequestStatus.PENDING
                            || friendship.getReceiver().getId().equals(userId)
                                    && friendship.getStatus() == RequestStatus.ACCEPTED)
                            ||
                            (friendship.getSender().getId().equals(userId)
                                    && friendship.getStatus() == RequestStatus.ACCEPTED
                                    || friendship.getSender().getId().equals(userId)
                                            && friendship.getStatus() == RequestStatus.PENDING))
                    .map(FriendshipMapper::toDomainComplete)
                    .toList();
        } catch (final Exception e) {
            log.error("Internal error occurred while fetching friendships for user: {}", userId, e);
            throw new InternalServerError("Internal error occurred while finding friendships", e);
        }
    }

}

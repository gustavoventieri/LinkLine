package com.gustavoventieri.framework.useCase.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.GetAllFriendships;
import org.gustavoventieri.domain.entity.FriendshipDomain;
import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.enums.RequestStatus;
import org.gustavoventieri.domain.repository.FriendshipRepository;
import org.gustavoventieri.domain.repository.UserRepository;
import org.gustavoventieri.domain.service.FriendshipService;
import org.springframework.stereotype.Service;

import com.gustavoventieri.framework.adapter.mapper.FriendshipMapper;
import com.gustavoventieri.framework.adapter.mapper.UserMapper;
import com.gustavoventieri.framework.entity.Friendship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshiplServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new friendship request between two users.
     *
     * @param senderId         ID of the requesting user
     * @param receiverUsername Username of the user receiving the request
     */
    @Override
    @Transactional
    public void createFriendship(final UUID senderId, final String receiverUsername) {
        log.debug("Starting friendship creation: senderId={}, receiverUsername={}", senderId, receiverUsername);

        final UserDomain sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Requesting user not found"));

        final UserDomain receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        if (sender.id().equals(receiver.id())) {
            log.warn("User {} attempted to send a friendship request to themselves", senderId);
            throw new IllegalArgumentException("You cannot send a friendship request to yourself.");
        }

        final List<RequestStatus> blockedStatuses = Arrays.asList(RequestStatus.PENDING, RequestStatus.ACCEPTED,
                RequestStatus.REMOVED);

        friendshipRepository.findExisting(senderId, receiver.id(), blockedStatuses)
                .ifPresent(existing -> {
                    if (reactivateFriendshipIfRemoved(existing)) {
                        return;
                    }
                    log.warn("User {} already has a pending request or is already friends with {}", senderId,
                            receiverUsername);
                    throw new IllegalStateException("You already sent a request or are already friends.");
                });

        final Friendship friendship = new Friendship(
                null,
                UserMapper.toEntityBasic(sender),
                UserMapper.toEntityBasic(receiver),
                RequestStatus.PENDING,
                null,
                null);

        friendshipRepository.save(FriendshipMapper.toDomainComplete(friendship));

        log.info("Friendship request sent from {} to {}", sender.username(), receiver.username());
    }

    /**
     * Updates the status of a friendship request.
     *
     * @param friendshipId  ID of the friendship
     * @param newStatus     New status to be set
     * @param currentUserId ID of the user performing the update
     */
    @Override
    @Transactional
    public void updateFriendship(final UUID friendshipId, final RequestStatus newStatus, final UUID currentUserId) {
        log.debug("Attempting to update friendship {} to {}", friendshipId, newStatus);

        final FriendshipDomain friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));

        final UUID senderId = friendship.sender().id();
        final UUID receiverId = friendship.receiver().id();

        // Check if the current user is either the sender or receiver
        if (!currentUserId.equals(senderId) && !currentUserId.equals(receiverId)) {
            throw new SecurityException("You do not have permission to update this friendship.");
        }

        // Only the receiver can accept the friendship
        if (newStatus == RequestStatus.ACCEPTED && !currentUserId.equals(receiverId)) {
            throw new SecurityException("Only the recipient of the request can accept the friendship.");
        }

        friendshipRepository.updateStatus(friendshipId, newStatus);

        log.info("Friendship {} status updated to {} by user {}", friendshipId, newStatus, currentUserId);
    }

    /**
     * Retrieves all friendships and requests for a given user.
     *
     * @param userId ID of the user
     * @return list of friendships and notifications
     */
    @Override
    public List<GetAllFriendships> getAllByUserId(final UUID userId) {
        log.debug("Fetching all friendships and requests for user: {}", userId);

        final List<FriendshipDomain> friendships = friendshipRepository.getAllByUserId(userId);

        final List<GetAllFriendships> result = friendships.stream()
                .map(friendship -> FriendshipMapper.toNotificationDTO(friendship))
                .toList();

        log.info("Found {} friendships/requests for user {}", result.size(), userId);
        return result;
    }

    // Helpers

    private boolean reactivateFriendshipIfRemoved(FriendshipDomain existingFriendship) {
        if (existingFriendship.status() == RequestStatus.REMOVED) {
            friendshipRepository.updateStatus(existingFriendship.id(), RequestStatus.PENDING);
            log.info("Friendship reactivated between user {} and user {}",
                    existingFriendship.sender(), existingFriendship.receiver());
            return true;
        }
        return false;
    }

}

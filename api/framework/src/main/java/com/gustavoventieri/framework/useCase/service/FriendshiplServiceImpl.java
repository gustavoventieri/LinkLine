package com.gustavoventieri.framework.useCase.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.GetAllFriendships;
import org.gustavoventieri.domain.dto.response.PotentialFriendResponse;
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

                Optional<FriendshipDomain> existing = friendshipRepository
                                .findExisting(senderId, receiver.id(), blockedStatuses)
                                .stream()
                                .findFirst();

                if (existing.isPresent()) {
                        if (reactivateFriendshipIfRemoved(existing.get(), sender, receiver)) {
                                return;
                        }
                        throw new IllegalStateException(
                                        "You already sent a request or are already friends.");
                }

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

                friendshipRepository.updateFriendship(friendshipId, friendship.sender(), friendship.receiver(), newStatus);

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

        /**
         * Searches for users by a partial or full username (excluding the current user)
         * and returns
         * a list of potential friends along with the current friendship status.
         *
         * <p>
         * The search is limited by the specified {@code searchLimit} to avoid returning
         * too many results.
         * If a friendship exists between the current user and a potential match, the
         * friendship status
         * (e.g., PENDING, ACCEPTED) is returned. Otherwise, the status will be
         * "NOT_FRIEND".
         * </p>
         *
         * @param searchTerm    the username or partial username to search for
         * @param currentUserId the UUID of the currently authenticated user (to exclude
         *                      from results)
         * @param searchLimit   the maximum number of users to return
         * @return a list of {@link PotentialFriendResponse} objects containing basic
         *         user info and friendship status
         */
        @Override
        public List<PotentialFriendResponse> findUsersByUsername(final String searchTerm, final UUID currentUserId,
                        final int searchLimit) {
                log.debug("Searching users by username. Term: {}, currentUserId: {}", searchTerm, currentUserId);

                List<UserDomain> potentialFriends = userRepository
                                .searchByApproximateUsername(searchTerm, currentUserId)
                                .stream()
                                .limit(searchLimit)
                                .toList();

                return potentialFriends.stream()
                                .map(potential -> {
                                        UUID otherUserId = potential.id();

                                        // Verifica se existe amizade entre os dois usuários (transforma List ->
                                        // Optional)
                                        Optional<FriendshipDomain> optionalFriendship = friendshipRepository
                                                        .findExisting(
                                                                        currentUserId,
                                                                        otherUserId,
                                                                        List.of(RequestStatus.PENDING,
                                                                                        RequestStatus.ACCEPTED))
                                                        .stream()
                                                        .findFirst();

                                        // Define o status como String para lidar com "NOT_FRIEND"
                                        String status = optionalFriendship
                                                        .map(friendship -> friendship.status().name())
                                                        .orElse("NOT_FRIEND");

                                        return new PotentialFriendResponse(
                                                        potential.username(),
                                                        potential.avatarUrl(),
                                                        status);
                                })
                                .toList();
        }

        // Helpers

        private boolean reactivateFriendshipIfRemoved(final FriendshipDomain existingFriendship,
                        final UserDomain sender, final UserDomain receiver) {

                if (existingFriendship.status() == RequestStatus.REMOVED) {
                        friendshipRepository.updateFriendship(existingFriendship.id(), sender, receiver,
                                        RequestStatus.PENDING);
                        log.info("Friendship reactivated between user {} and user {}",
                                        existingFriendship.sender(), existingFriendship.receiver());
                        return true;
                }
                return false;
        }

}

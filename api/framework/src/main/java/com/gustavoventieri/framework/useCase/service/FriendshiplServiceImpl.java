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

    // Dependency Inversion Principle
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createFriendship(final UUID userId, final String friendUsername) {
        final UserDomain sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário solicitante não encontrado"));

        final UserDomain friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new IllegalArgumentException("Usuário destinatário não encontrado"));

        if (sender.id().equals(friend.id())) {
            throw new IllegalArgumentException("Você não pode enviar solicitação de amizade para si mesmo.");
        }

        final List<RequestStatus> blockedStatuses = Arrays.asList(RequestStatus.PENDING, RequestStatus.ACCEPTED);

        friendshipRepository.findExisting(userId, friend.id(), blockedStatuses)
                .ifPresent(friendship -> {
                    throw new IllegalStateException("Você já enviou uma solicitação ou já são amigos.");
                });

        final Friendship friendship = new Friendship(
                null,
                UserMapper.toEntityBasic(sender),
                UserMapper.toEntityBasic(friend),
                UserMapper.toEntityBasic(sender),
                RequestStatus.PENDING,
                null,
                null);

        friendshipRepository.save(FriendshipMapper.toDomainComplete(friendship));
    }

    @Override
    public void updateFriendship(final UUID friendshipId, final String newStatus) {

        // FriendshipDomain friendship =
        // friendshipRepository.findById(friendshipId).orElseThrow();

    }

    @Override
    public void deleteFriendship(final UUID userId, final UUID friendId) {

    }

    @Override
    public List<GetAllFriendships> getAllByUserId(final UUID userId) {

        log.debug("Buscando amizades e solicitações do user: {}", userId);
        final List<FriendshipDomain> friendships = friendshipRepository.getAllByUserId(userId);
        return friendships.stream()
                .map(friendship -> FriendshipMapper.toNotificationDTO(friendship, userId))
                .toList();

    }

}

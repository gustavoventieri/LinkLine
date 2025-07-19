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
    public void createFriendship(UUID userId, String friendUsername) {
        UserDomain sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário solicitante não encontrado"));

        UserDomain friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new IllegalArgumentException("Usuário destinatário não encontrado"));

        if (sender.id().equals(friend.id())) {
            throw new IllegalArgumentException("Você não pode enviar solicitação de amizade para si mesmo.");
        }

        List<RequestStatus> blockedStatuses = Arrays.asList(RequestStatus.PENDING, RequestStatus.ACCEPTED);

        friendshipRepository.findExisting(userId, friend.id(), blockedStatuses)
                .ifPresent(friendship -> {
                    throw new IllegalStateException("Você já enviou uma solicitação ou já são amigos.");
                });

        Friendship friendship = new Friendship(
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
    public void updateFriendship(UUID friendshipId, String newStatus) {

        // FriendshipDomain friendship =
        // friendshipRepository.findById(friendshipId).orElseThrow();

    }

    @Override
    public void deleteFriendship(UUID userId, UUID friendId) {

    }

    @Override
    public List<GetAllFriendships> getAllByUserId(UUID userId) {

        log.debug("Buscando amizades e solicitações do user: {}", userId);
        List<FriendshipDomain> friendships = friendshipRepository.getAllByUserId(userId);
        return friendships.stream()
                .map(friendship -> FriendshipMapper.toNotificationDTO(friendship, userId))
                .toList();

    }

}

package com.gustavoventieri.framework.useCase.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.enums.RequestStatus;
import org.gustavoventieri.domain.service.FriendshipService;
import org.springframework.stereotype.Service;

import com.gustavoventieri.framework.adapter.mapper.FriendshipMapper;
import com.gustavoventieri.framework.adapter.mapper.UserMapper;
import com.gustavoventieri.framework.driver.repository.FriendshipRepositoryImpl;
import com.gustavoventieri.framework.driver.repository.UserRepositoryImpl;
import com.gustavoventieri.framework.entity.Friendship;
import com.gustavoventieri.framework.entity.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendshiplServiceImpl implements FriendshipService {

    private final FriendshipRepositoryImpl friendshipRepositoryImpl;
    private final UserRepositoryImpl userRepositoryImpl;

    @Override
    @Transactional
    public void createFriendship(UUID userId, String friendUsername) {
        Optional<UserDomain> senderOpt = userRepositoryImpl.findById(userId);
        if (senderOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário solicitante não encontrado");
        }

        Optional<UserDomain> friendOpt = userRepositoryImpl.findByUsername(friendUsername);
        if (friendOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário destinatário não encontrado");
        }

        User sender = UserMapper.toEntityBasic(senderOpt.get());
        User friend = UserMapper.toEntityBasic(friendOpt.get());

        if (sender.getId().equals(friend.getId())) {
            throw new IllegalArgumentException("Você não pode enviar solicitação de amizade para si mesmo.");
        }

        boolean alreadyExists = friendshipRepositoryImpl
                .findExisting(sender.getId(), friend.getId()).isPresent()
                || friendshipRepositoryImpl
                        .findExisting(friend.getId(), sender.getId()).isPresent();

        if (alreadyExists) {
            throw new IllegalStateException("Solicitação de amizade já existe ou já são amigos.");
        }

        Friendship friendship = new Friendship(
                null,
                sender,
                friend,
                sender,
                RequestStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now());

        friendshipRepositoryImpl.save(FriendshipMapper.toDomainComplete(friendship));
    }

    @Override
    public void updateFriendship(UUID chatRequestId, String newStatus) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateFriendship'");
    }

    @Override
    public void deleteFriendship(UUID userId, UUID friendId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFriendship'");
    }

    @Override
    public Map<String, Object> getFriendshipsByStatus(String type, String status, UUID userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFriendshipsByStatus'");
    }

}

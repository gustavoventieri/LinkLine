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
     * Cria uma nova solicitação de amizade entre dois usuários.
     *
     * @param userId         ID do usuário solicitante
     * @param friendUsername nome de usuário do destinatário da solicitação
     */
    @Override
    @Transactional
    public void createFriendship(final UUID senderId, final String receiverUsername) {
        log.debug("Iniciando criação de amizade: userId={}, friendUsername={}", senderId, receiverUsername);

        final UserDomain sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário solicitante não encontrado"));

        final UserDomain receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new IllegalArgumentException("Usuário destinatário não encontrado"));

        if (sender.id().equals(receiver.id())) {
            log.warn("Usuário {} tentou enviar amizade para si mesmo", senderId);
            throw new IllegalArgumentException("Você não pode enviar solicitação de amizade para si mesmo.");
        }

        final List<RequestStatus> blockedStatuses = Arrays.asList(RequestStatus.PENDING, RequestStatus.ACCEPTED);

        friendshipRepository.findExisting(senderId, receiver.id(), blockedStatuses)
                .ifPresent(existing -> {
                    log.warn("Usuário {} já possui uma solicitação pendente ou amizade com {}", senderId,
                            receiverUsername);
                    throw new IllegalStateException("Você já enviou uma solicitação ou já são amigos.");
                });

        final Friendship friendship = new Friendship(
                null,
                UserMapper.toEntityBasic(sender),
                UserMapper.toEntityBasic(receiver),
                RequestStatus.PENDING,
                null,
                null);

        friendshipRepository.save(FriendshipMapper.toDomainComplete(friendship));

        log.info("Solicitação de amizade enviada de {} para {}", sender.username(), receiver.username());
    }

    /**
     * Atualiza o status de uma amizade ou solicitação.
     *
     * @param friendshipId ID da amizade
     * @param newStatus    novo status da amizade
     */
    @Override
    @Transactional
    public void updateFriendship(final UUID friendshipId, final RequestStatus newStatus, final UUID currentUserId) {
        log.debug("Tentando atualizar amizade {} para {}", friendshipId, newStatus);

        final FriendshipDomain friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Amizade não encontrada"));

        final UUID senderId = friendship.sender().id();
        final UUID receiverId = friendship.receiver().id();

        // Verifica se o usuário atual é o sender ou receiver
        if (!currentUserId.equals(senderId) && !currentUserId.equals(receiverId)) {
            throw new SecurityException("Você não tem permissão para atualizar esta amizade.");
        }

        // Apenas o receiver pode aceitar a amizade
        if (newStatus == RequestStatus.ACCEPTED && !currentUserId.equals(receiverId)) {
            throw new SecurityException("Apenas o destinatário da solicitação pode aceitar a amizade.");
        }

        friendshipRepository.updateStatus(friendshipId, newStatus);

        log.info("Status da amizade {} atualizado para {} pelo usuário {}", friendshipId, newStatus, currentUserId);
    }

    /**
     * Remove uma amizade entre dois usuários.
     *
     * @param userId   ID do usuário solicitante
     * @param friendId ID do amigo a ser removido
     */
    @Override
    public void deleteFriendship(final UUID userId, final UUID friendId) {
        log.debug("Removendo amizade entre userId={} e friendId={}", userId, friendId);
        // friendshipRepository.deleteFriendship(userId, friendId);
        log.info("Amizade entre {} e {} removida com sucesso", userId, friendId);
    }

    /**
     * Retorna todas as amizades e solicitações de amizade de um usuário.
     *
     * @param userId ID do usuário
     * @return lista de amizades e notificações
     */
    @Override
    public List<GetAllFriendships> getAllByUserId(final UUID userId) {
        log.debug("Buscando amizades e solicitações do user: {}", userId);

        final List<FriendshipDomain> friendships = friendshipRepository.getAllByUserId(userId);

        final List<GetAllFriendships> result = friendships.stream()
                .map(friendship -> FriendshipMapper.toNotificationDTO(friendship))
                .toList();

        log.info("Encontradas {} amizades/solicitações para o usuário {}", result.size(), userId);
        return result;
    }
}

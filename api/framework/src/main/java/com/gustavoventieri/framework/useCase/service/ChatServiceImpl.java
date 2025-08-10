package com.gustavoventieri.framework.useCase.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.ParticipantDTO;
import org.gustavoventieri.domain.dto.response.PrivateChatResponseDTO;
import org.gustavoventieri.domain.entity.ChatDomain;
import org.gustavoventieri.domain.entity.FriendshipDomain;
import org.gustavoventieri.domain.entity.UserDomain;
import org.gustavoventieri.domain.enums.ChatType;
import org.gustavoventieri.domain.enums.RequestStatus;
import org.gustavoventieri.domain.exception.Conflict;
import org.gustavoventieri.domain.exception.NotFound;
import org.gustavoventieri.domain.repository.ChatRepository;
import org.gustavoventieri.domain.repository.FriendshipRepository;
import org.gustavoventieri.domain.repository.UserRepository;
import org.gustavoventieri.domain.service.ChatService;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gustavoventieri.framework.adapter.mapper.ChatMapper;
import com.gustavoventieri.framework.adapter.mapper.UserMapper;
import com.gustavoventieri.framework.entity.Chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

        private final ChatRepository chatRepository;
        private final UserRepository userRepository;
        private final FriendshipRepository friendshipRepository;

        /**
         * Cria um novo chat privado entre o usuário atual e um usuário alvo.
         *
         * @param currentUserId  O ID do usuário que está iniciando a ação.
         * @param targetUsername O nome de usuário do outro participante.
         * @throws NotFoundException        se um dos usuários não for encontrado.
         * @throws IllegalArgumentException se um usuário tentar criar um chat consigo
         *                                  mesmo.
         * @throws ConflictException        se um chat entre esses dois usuários já
         *                                  existir.
         */
        @Override
        @Transactional
        public void createPrivateChat(final UUID currentUserId, final String targetUsername) {
                log.debug("Iniciando criação de chat privado entre o usuário {} e {}", currentUserId, targetUsername);

                final UserDomain currentUser = userRepository.findById(currentUserId)
                                .orElseThrow(() -> new NotFound("Usuário atual não encontrado."));

                final UserDomain targetUser = userRepository.findByUsername(targetUsername)
                                .orElseThrow(() -> new NotFound(
                                                "Usuário alvo '" + targetUsername + "' não encontrado."));

                if (currentUser.id().equals(targetUser.id())) {
                        log.warn("Usuário {} tentou criar um chat consigo mesmo.", currentUserId);
                        throw new IllegalArgumentException("Você não pode criar um chat consigo mesmo.");
                }

                List<FriendshipDomain> friendships = friendshipRepository.findExisting(
                                currentUserId, targetUser.id(), List.of(RequestStatus.ACCEPTED));

                if (friendships.isEmpty()) {
                        throw new NotFound("Não existe uma amizade aceita com este usuário.");
                }

                chatRepository.findExistingChat(currentUser.id(), targetUser.id())
                                .ifPresent(existingChat -> {
                                        log.warn("Tentativa de criar um chat duplicado entre {} e {}", currentUser.id(),
                                                        targetUser.id());
                                        throw new Conflict("Um chat com este usuário já existe.");
                                });

                final Chat newChat = new Chat(null, ChatType.PRIVATE, null, null, null,
                                Set.of(UserMapper.toEntityBasic(targetUser), UserMapper.toEntityBasic(currentUser)),
                                Set.of());

                chatRepository.save(ChatMapper.toDomainComplete(newChat));
                log.info("Chat privado criado com sucesso entre {} e {}", currentUser.username(),
                                targetUser.username());
        }

        /**
         * Obtém todos os chats privados de um usuário.
         *
         * @param userId O ID do usuário.
         * @return Um Map contendo uma lista de chats, formatados para exibição.
         */
        @Override
        public Map<String, List<PrivateChatResponseDTO>> getPrivateChats(final UUID userId) {
                log.debug("Buscando todos os chats privados para o usuário: {}", userId);
                final List<ChatDomain> chats = chatRepository.findAllByUserId(userId);

                final List<PrivateChatResponseDTO> formattedChats = chats.stream()
                                .map(chat -> {
                                        // Encontra o outro participante no chat privado
                                        final UserDomain otherParticipant = chat.participants().stream()
                                                        .filter(participant -> !participant.id().equals(userId))
                                                        .findFirst()
                                                        // Lança uma exceção se um chat privado não tiver outro
                                                        // participante,
                                                        // o que indica um estado de dados inconsistente.
                                                        .orElseThrow(() -> new IllegalStateException(
                                                                        "Chat privado " + chat.id()
                                                                                        + " não tem um segundo participante."));

                                        // Cria o DTO do participante
                                        final ParticipantDTO participantDTO = new ParticipantDTO(
                                                        otherParticipant.username(),
                                                        otherParticipant.avatarUrl());

                                        // Cria o DTO principal do chat
                                        return new PrivateChatResponseDTO(
                                                        chat.id(),
                                                        chat.createdAt(),
                                                        participantDTO);

                                }).toList();

                log.info("Encontrados e formatados {} chats para o usuário {}", formattedChats.size(), userId);
                return Map.of("chats", formattedChats);
        }

        /**
         * Obtém os detalhes completos de um chat, incluindo seus participantes e
         * mensagens.
         *
         * @param chatId O ID do chat.
         * @return Um Map contendo os detalhes do chat.
         * @throws NotFoundException se o chat não for encontrado.
         */
        @Override
        public Map<String, Object> getChatById(final UUID chatId) {
                log.debug("Buscando detalhes do chat com ID: {}", chatId);
                final ChatDomain chat = chatRepository.findByIdWithParticipantsAndMessages(chatId)
                                .orElseThrow(() -> new NotFound("Chat com ID " + chatId + " não encontrado."));

                log.info("Detalhes do chat {} encontrados com sucesso.", chatId);
                return Map.of("chat", chat);
        }

        /**
         * Deleta um chat privado e todas as suas mensagens.
         *
         * @param chatId O ID do chat a ser deletado.
         * @return Um Map com uma mensagem de confirmação.
         */
        @Override
        @Transactional
        public Map<String, String> deletePrivateChat(final UUID chatId) {
                log.debug("Tentando deletar o chat com ID: {}", chatId);
                // A validação de existência e a exclusão em cascata são tratadas pelo
                // repositório.
                chatRepository.deleteChatById(chatId);
                log.info("Chat com ID {} deletado com sucesso.", chatId);
                return Map.of("message", "Chat deletado com sucesso.");
        }
}
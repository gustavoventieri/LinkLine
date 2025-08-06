package org.gustavoventieri.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.ChatDomain;

public interface ChatRepository {

    Optional<ChatDomain> findExistingChat(UUID userId1, UUID userId2);

    List<ChatDomain> findAllByUserId(UUID userId);

    void save(ChatDomain chatDomain);

    void deleteChatById(UUID chatId);

    Optional<ChatDomain> findByIdWithParticipantsAndMessages(UUID chatId);
}
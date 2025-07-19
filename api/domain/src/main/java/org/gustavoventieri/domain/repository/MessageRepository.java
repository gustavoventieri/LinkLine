package org.gustavoventieri.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.entity.MessageDomain;

public interface MessageRepository {

    void save(MessageDomain message);

    void update(UUID id, MessageDomain updatedMessage);

    void deleteById(UUID id);

    Optional<MessageDomain> findById(UUID id);

    List<MessageDomain> findByPrivateChatId(UUID privateChatId);

    List<MessageDomain> findByGroupChatId(UUID groupChatId);
}

package com.gustavoventieri.framework.adapter.mapper;

import java.util.stream.Collectors;

import org.gustavoventieri.domain.entity.ChatDomain;

import com.gustavoventieri.framework.entity.Chat;

public class ChatMapper {
    public static Chat toEntityBasic(ChatDomain domain) {
    if (domain == null) return null;

    return new Chat(
        domain.id(),
        domain.type(),
        domain.name(),
        domain.avatarUrl(),
        domain.createdAt(),
        null, // participants
        null  // messages
    );
}

    public static ChatDomain toDomainBasic(Chat entity) {
        if (entity == null) return null;

        return new ChatDomain(
            entity.getId(),
            entity.getType(),
            entity.getName(),
            entity.getAvatarUrl(),
            null, // participants
            null, // messages
            entity.getCreatedAt()
        );
    }

        public static Chat toEntityComplete(ChatDomain domain) {
        if (domain == null) return null;

        return new Chat(
            domain.id(),
            domain.type(),
            domain.name(),
            domain.avatarUrl(),
            domain.createdAt(),
            domain.participants() != null ? domain.participants().stream()
                .map(UserMapper::toEntityBasic)
                .collect(Collectors.toSet()) : null,
            domain.messages() != null ? domain.messages().stream()
                .map(MessageMapper::toEntityBasic)
                .collect(Collectors.toSet()) : null
        );
    }

    public static ChatDomain toDomainComplete(Chat entity) {
        if (entity == null) return null;

        return new ChatDomain(
            entity.getId(),
            entity.getType(),
            entity.getName(),
            entity.getAvatarUrl(),
            entity.getParticipants() != null ? entity.getParticipants().stream()
                .map(UserMapper::toDomainBasic)
                .collect(Collectors.toSet()) : null,
            entity.getMessages() != null ? entity.getMessages().stream()
                .map(MessageMapper::toDomainBasic)
                .collect(Collectors.toSet()) : null,
            entity.getCreatedAt()
        );
    }


} 
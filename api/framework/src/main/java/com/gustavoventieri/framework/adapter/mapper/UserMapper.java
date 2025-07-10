package com.gustavoventieri.framework.adapter.mapper;

import org.gustavoventieri.domain.entity.UserDomain;
import com.gustavoventieri.framework.entity.User;

import java.util.stream.Collectors;

public class UserMapper {

    // Mapeamento básico: apenas campos primitivos
    public static User toEntityBasic(UserDomain domain) {
        if (domain == null) return null;

        return new User(
            domain.id(),
            domain.username(),
            domain.email(),
            domain.password(),
            domain.avatarUrl(),
            domain.createdAt(),
            domain.updatedAt(),
            null,  // chats
            null,  // messagesSent
            null,  // messagesReceived
            null,  // sentChatRequests
            null   // receivedChatRequests
        );
    }

    public static UserDomain toDomainBasic(User entity) {
        if (entity == null) return null;

        return new UserDomain(
            entity.getId(),
            entity.getUsername(),
            entity.getEmail(),
            entity.getPassword(),
            entity.getAvatarUrl(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            null,  // chats
            null,  // messagesSent
            null,  // messagesReceived
            null,  // sentChatRequests
            null   // receivedChatRequests
        );
    }

    // Mapeamento completo: incluindo relacionamentos
    public static User toEntityComplete(UserDomain domain) {
        if (domain == null) return null;

        return new User(
            domain.id(),
            domain.username(),
            domain.email(),
            domain.password(),
            domain.avatarUrl(),
            domain.createdAt(),
            domain.updatedAt(),
            domain.chats() != null ? domain.chats().stream()
                .map(ChatMapper::toEntityBasic)
                .collect(Collectors.toSet()) : null,
            domain.messagesSent() != null ? domain.messagesSent().stream()
                .map(MessageMapper::toEntityBasic)
                .collect(Collectors.toSet()) : null,
            domain.messagesReceived() != null ? domain.messagesReceived().stream()
                .map(MessageMapper::toEntityBasic)
                .collect(Collectors.toSet()) : null,
            domain.sentChatRequests() != null ? domain.sentChatRequests().stream()
                .map(ChatRequestMapper::toEntityBasic)
                .collect(Collectors.toSet()) : null,
            domain.receivedChatRequests() != null ? domain.receivedChatRequests().stream()
                .map(ChatRequestMapper::toEntityBasic)
                .collect(Collectors.toSet()) : null
        );
    }

    public static UserDomain toDomainComplete(User entity) {
        if (entity == null) return null;

        return new UserDomain(
            entity.getId(),
            entity.getUsername(),
            entity.getEmail(),
            entity.getPassword(),
            entity.getAvatarUrl(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getChats() != null ? entity.getChats().stream()
                .map(ChatMapper::toDomainBasic)
                .collect(Collectors.toSet()) : null,
            entity.getMessagesSent() != null ? entity.getMessagesSent().stream()
                .map(MessageMapper::toDomainBasic)
                .collect(Collectors.toSet()) : null,
            entity.getMessagesReceived() != null ? entity.getMessagesReceived().stream()
                .map(MessageMapper::toDomainBasic)
                .collect(Collectors.toSet()) : null,
            entity.getSentChatRequests() != null ? entity.getSentChatRequests().stream()
                .map(ChatRequestMapper::toDomainBasic)
                .collect(Collectors.toSet()) : null,
            entity.getReceivedChatRequests() != null ? entity.getReceivedChatRequests().stream()
                .map(ChatRequestMapper::toDomainBasic)
                .collect(Collectors.toSet()) : null
        );
    }
}

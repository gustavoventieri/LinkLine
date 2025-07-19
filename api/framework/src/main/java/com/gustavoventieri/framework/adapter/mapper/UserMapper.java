package com.gustavoventieri.framework.adapter.mapper;

import org.gustavoventieri.domain.entity.UserDomain;
import com.gustavoventieri.framework.entity.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static User toEntityBasic(UserDomain domain) {
        if (domain == null)
            return null;

        return new User(
                domain.id(),
                domain.username(),
                domain.email(),
                domain.password(),
                domain.avatarUrl(),
                domain.createdAt(),
                domain.updatedAt(),
                null, // chats
                null, // messagesSent
                null, // friendshipsUser
                null // friendshipsFriend

        );
    }

    public static UserDomain toDomainBasic(User entity) {
        if (entity == null)
            return null;

        return new UserDomain(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getAvatarUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                null, // chats
                null, // messagesSent
                null, // friendshipsUsers
                null // friendshipsFriend
               
        );
    }

    public static User toEntityComplete(UserDomain domain) {
        if (domain == null)
            return null;

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
                domain.friendshipsSender() != null ? domain.friendshipsSender().stream()
                        .map(FriendshipMapper::toEntityBasic)
                        .collect(Collectors.toSet()) : null,
                domain.friendshipsReceiver() != null ? domain.friendshipsReceiver().stream()
                        .map(FriendshipMapper::toEntityBasic)
                        .collect(Collectors.toSet()) : null

        );
    }

    public static UserDomain toDomainComplete(User entity) {
        if (entity == null)
            return null;

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
                entity.getFriendshipsSent() != null ? entity.getFriendshipsSent().stream()
                        .map(FriendshipMapper::toDomainBasic)
                        .collect(Collectors.toSet()) : null,
                entity.getFriendshipsReceived() != null ? entity.getFriendshipsReceived().stream()
                        .map(FriendshipMapper::toDomainBasic)
                        .collect(Collectors.toSet()) : null
        // Se quiser adicionar friendshipsRequested, só seguir o padrão
        );
    }

}

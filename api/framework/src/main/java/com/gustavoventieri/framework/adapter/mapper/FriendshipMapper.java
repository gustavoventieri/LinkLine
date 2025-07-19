package com.gustavoventieri.framework.adapter.mapper;

import java.util.UUID;

import org.gustavoventieri.domain.dto.response.GetAllFriendships;
import org.gustavoventieri.domain.entity.FriendshipDomain;

import com.gustavoventieri.framework.entity.Friendship;
import com.gustavoventieri.framework.entity.User;

public class FriendshipMapper {

    public static Friendship toEntityBasic(FriendshipDomain domain) {
        if (domain == null)
            return null;

        return new Friendship(
                domain.id(),
                null,
                null,
                domain.status(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public static FriendshipDomain toDomainBasic(Friendship entity) {
        if (entity == null)
            return null;

        return new FriendshipDomain(
                entity.getId(),
                null,
                null,
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static Friendship toEntityComplete(FriendshipDomain domain) {
        if (domain == null)
            return null;

        User sender = UserMapper.toEntityBasic(domain.sender());
        User receiver = UserMapper.toEntityBasic(domain.receiver());

        return new Friendship(
                domain.id(),
                sender,
                receiver,
                domain.status(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public static FriendshipDomain toDomainComplete(Friendship entity) {
        if (entity == null)
            return null;

        return new FriendshipDomain(
                entity.getId(),
                UserMapper.toDomainBasic(entity.getSender()),
                UserMapper.toDomainBasic(entity.getReceiver()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static GetAllFriendships toNotificationDTO(FriendshipDomain friendship) {
        String senderUsername = friendship.sender() != null ? friendship.sender().username() : null;
        String receiverUsername = friendship.receiver() != null ? friendship.receiver().username() : null;

        return new GetAllFriendships(
                friendship.id(),
                senderUsername,
                receiverUsername,
                friendship.status().name());
    }

}

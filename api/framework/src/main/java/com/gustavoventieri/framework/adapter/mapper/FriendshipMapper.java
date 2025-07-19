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
                null, // user
                null, // friend
                null, // requestedBy
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()

        );
    }

    public static Friendship toEntityComplete(FriendshipDomain domain) {
        if (domain == null)
            return null;

        User user = UserMapper.toEntityBasic(domain.user());
        User friend = UserMapper.toEntityBasic(domain.friend());
        User requestedBy = UserMapper.toEntityBasic(domain.requestedBy());

        return new Friendship(
                domain.id(),
                user,
                friend,
                requestedBy,
                domain.status(),
                domain.createdAt(),
                domain.updatedAt()

        );
    }

    public static FriendshipDomain toDomainComplete(Friendship entity) {
        if (entity == null)
            return null;

        return new FriendshipDomain(
                entity.getId(),
                UserMapper.toDomainBasic(entity.getUser()),
                UserMapper.toDomainBasic(entity.getFriend()),
                UserMapper.toDomainBasic(entity.getRequestedBy()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static GetAllFriendships toNotificationDTO(FriendshipDomain friendship, UUID requesterId) {
        return friendship.user().id().equals(requesterId)
                ? new GetAllFriendships(
                        friendship.id(),
                        friendship.user().username(),
                        friendship.friend().username(),
                        friendship.status().name())
                : new GetAllFriendships(
                        friendship.id(),
                        friendship.user().username(),
                        friendship.friend().username(),
                        friendship.status().name());
    }

}

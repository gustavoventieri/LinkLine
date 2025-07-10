package com.gustavoventieri.framework.adapter.mapper;

import org.gustavoventieri.domain.entity.ChatRequestDomain;
import com.gustavoventieri.framework.entity.ChatRequest;
import com.gustavoventieri.framework.entity.User;


public class ChatRequestMapper {

    public static ChatRequest toEntityBasic(ChatRequestDomain domain) {
    if (domain == null) return null;

    return new ChatRequest(
        domain.id(),
        domain.status(),
        domain.createdAt(),
        null, // sender
        null  // receiver
    );
    }

    public static ChatRequestDomain toDomainBasic(ChatRequest entity) {
        if (entity == null) return null;

        return new ChatRequestDomain(
            entity.getId(),
            null, // senderId
            null, // receiverId
            entity.getStatus(),
            entity.getCreatedAt()
        );
    }

    public static ChatRequest toEntityComplete(ChatRequestDomain domain) {
        if (domain == null) return null;

        User senderEntity = UserMapper.toEntityBasic(domain.sender());
        User receiverEntity = UserMapper.toEntityBasic(domain.receiver());

        return new ChatRequest(
            domain.id(),
            domain.status(),
            domain.createdAt(),
            senderEntity,
            receiverEntity
        );
    }

    public static ChatRequestDomain toDomainComplete(ChatRequest entity) {
        if (entity == null) return null;

        return new ChatRequestDomain(
            entity.getId(),
            UserMapper.toDomainBasic(entity.getSender()),
            UserMapper.toDomainBasic(entity.getReceiver()),
            entity.getStatus(),
            entity.getCreatedAt()
        );
    }



}

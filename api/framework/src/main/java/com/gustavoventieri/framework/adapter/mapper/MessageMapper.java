package com.gustavoventieri.framework.adapter.mapper;

import org.gustavoventieri.domain.entity.MessageDomain;
import com.gustavoventieri.framework.entity.Message;
import com.gustavoventieri.framework.entity.User;
import com.gustavoventieri.framework.entity.Chat;

public class MessageMapper {

    public static Message toEntityBasic(MessageDomain domain) {
        if (domain == null) return null;

        return new Message(
            domain.id(),
            domain.content(),
            domain.sentAt(),
            null, // sender
            null  // chat
        );
    }

    public static MessageDomain toDomainBasic(Message entity) {
        if (entity == null) return null;

        return new MessageDomain(
            entity.getId(),
            entity.getContent(),
            entity.getSentAt(),
            null, // sender
            null  // chat
        );
    }

    public static Message toEntityComplete(MessageDomain domain) {
        if (domain == null) return null;

        User sender = UserMapper.toEntityBasic(domain.sender());
        Chat chat = ChatMapper.toEntityBasic(domain.chat());

        return new Message(
            domain.id(),
            domain.content(),
            domain.sentAt(),
            sender,
            chat
        );
    }

    public static MessageDomain toDomainComplete(Message entity) {
        if (entity == null) return null;

        return new MessageDomain(
            entity.getId(),
            entity.getContent(),
            entity.getSentAt(),
            UserMapper.toDomainBasic(entity.getSender()),
            ChatMapper.toDomainBasic(entity.getChatId())
        );
    }
}

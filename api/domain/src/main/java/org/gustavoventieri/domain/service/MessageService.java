    package org.gustavoventieri.domain.service;


    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;

    import org.gustavoventieri.domain.entity.MessageDomain;

    public interface MessageService {

        MessageDomain sendMessage(MessageDomain message);

        Optional<MessageDomain> updateMessage(UUID messageId, MessageDomain updatedMessage);

        Optional<MessageDomain> deleteMessage(UUID messageId);

        Optional<MessageDomain> getMessageById(UUID messageId);

        List<MessageDomain> getMessagesByPrivateChatId(UUID privateChatId);

        List<MessageDomain> getMessagesByGroupChatId(UUID groupChatId);
    }

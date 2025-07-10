package org.gustavoventieri.domain.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserDomain(
    UUID id,
    String username,
    String email,
    String password,
    String avatarUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<ChatDomain> chats,                     
    Set<MessageDomain> messagesSent,            
    Set<MessageDomain> messagesReceived,        
    Set<ChatRequestDomain> sentChatRequests,    
    Set<ChatRequestDomain> receivedChatRequests 
) {}

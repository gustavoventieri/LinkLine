package org.gustavoventieri.domain.service;

import java.util.Map;
import java.util.UUID;

public interface ChatRequestService {

    void createChatRequest(UUID senderId, String receiverUsername);

    void updateChatRequest(UUID chatRequestId, String newStatus);

    void deleteChatRequest(UUID userId, UUID receiverId);

    Map<String, Object> getChatRequestsByStatus(String type, String status, UUID userId);
}

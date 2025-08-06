package org.gustavoventieri.domain.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.PrivateChatResponseDTO;

public interface ChatService {

    void createPrivateChat(UUID currentUserId, String username);

    Map<String, List<PrivateChatResponseDTO>> getPrivateChats(UUID userId);

    Map<String, Object> getChatById(UUID chatId);

    Map<String, String> deletePrivateChat(UUID chatId);

}

package org.gustavoventieri.domain.service;

import java.util.Map;
import java.util.UUID;

public interface ChatService {

    void createPrivateChat(UUID currentUserId, String username);

    Map<String, Object> getPrivateChats(UUID userId);

    Map<String, Object> getChatById(UUID chatId);

    Map<String, String> deletePrivateChat(UUID chatId);

}

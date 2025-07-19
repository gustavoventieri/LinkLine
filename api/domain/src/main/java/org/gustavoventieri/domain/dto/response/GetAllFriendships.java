package org.gustavoventieri.domain.dto.response;

import java.util.UUID;

public record GetAllFriendships(
        UUID friendshipId,
        String sender,
        String receiver,
        String status) {

}

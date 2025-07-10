package org.gustavoventieri.domain.dto.response;

public record PotentialFriendResponse(
    String username,
    String avatarSrc,
    String friendshipStatus
) {}


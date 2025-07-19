package com.gustavoventieri.framework.adapter.dto.request.friendship;

import org.gustavoventieri.domain.enums.RequestStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateFriendshipRequest(
        @NotNull(message = "O status é obrigatório.") RequestStatus status) {

}

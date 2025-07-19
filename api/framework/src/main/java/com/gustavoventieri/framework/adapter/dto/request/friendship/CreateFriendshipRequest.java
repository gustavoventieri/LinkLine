package com.gustavoventieri.framework.adapter.dto.request.friendship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFriendshipRequest(
        @NotBlank(message = "Username cannot be empty.") @Size(min = 2, message = "Username cannot be less than 2 characters.") @Size(max = 100, message = "Username cannot be more than 100 characters.") String friendUsername) {

}

package com.gustavoventieri.framework.adapter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public  record ChatRequestUpdateRequestImpl(
    @NotBlank(message = "newStatus é obrigatório")
    @Pattern(regexp = "^(PENDING|ACCEPTED|DECLINED)$", message = "Status inválido")
    String newStatus
) {
}
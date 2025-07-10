package com.gustavoventieri.framework.adapter.controller;

import java.util.List;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.PotentialFriendResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavoventieri.framework.adapter.dto.request.SearchUserRequest;
import com.gustavoventieri.framework.useCase.service.UserServiceImpl;
import com.gustavoventieri.framework.useCase.utils.JWTUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@SecurityRequirement(name = "cookieAuth")
@Slf4j
@Validated
public class UserController {
    

    private final JWTUtils jwtUtils;
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/search")
    public ResponseEntity<List<PotentialFriendResponse>> findUsersByUsername(
            @Valid @RequestBody SearchUserRequest searchUserRequest,
            HttpServletRequest request
    ) {
        UUID currentUserId = jwtUtils.getUserIdFromCookie(request);

        log.info("Iniciando busca de usuários por '{}', limite={}, userId={}",
                searchUserRequest.searchTerm(), searchUserRequest.limit(), currentUserId);

        List<PotentialFriendResponse> results = userServiceImpl.findUsersByUsername(
                searchUserRequest.searchTerm(),
                currentUserId,
                searchUserRequest.limit()
        );

        log.info("Busca concluída. {} usuários encontrados.", results.size());
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }
}

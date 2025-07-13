package com.gustavoventieri.framework.adapter.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavoventieri.framework.adapter.dto.request.friendship.CreateFriendshipRequest;
import com.gustavoventieri.framework.useCase.service.FriendshiplServiceImpl;
import com.gustavoventieri.framework.useCase.utils.JWTUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/friendship")
@RequiredArgsConstructor
@SecurityRequirement(name = "cookieAuth")
@Slf4j
public class FriendshipController {

    private final FriendshiplServiceImpl friendshiplServiceImpl;
    private final JWTUtils jwtUtils;

    @PostMapping("/create")
    public ResponseEntity<String> createFriendship(
            @RequestBody @Valid CreateFriendshipRequest createFriendshipRequest, HttpServletRequest httpServletRequest) {

        UUID userId = jwtUtils.getUserIdFromCookie(httpServletRequest);
        friendshiplServiceImpl.createFriendship(userId,
                createFriendshipRequest.friendUsername());

        return ResponseEntity.status(HttpStatus.OK).body("Amizade Criada");
    }
}

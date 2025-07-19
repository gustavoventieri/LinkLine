package com.gustavoventieri.framework.adapter.controller;

import java.util.List;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.GetAllFriendships;
import org.gustavoventieri.domain.service.FriendshipService;
import org.gustavoventieri.domain.utils.JWTUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavoventieri.framework.adapter.dto.request.friendship.CreateFriendshipRequest;

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

    private final FriendshipService friendshiplService;
    private final JWTUtils jwtUtils;

    @PostMapping("/create")
    public ResponseEntity<String> createFriendship(
            @RequestBody @Valid CreateFriendshipRequest createFriendshipRequest,
            HttpServletRequest httpServletRequest) {

        UUID userId = jwtUtils.getUserIdFromCookie(httpServletRequest);
        friendshiplService.createFriendship(userId,
                createFriendshipRequest.friendUsername());

        return ResponseEntity.status(HttpStatus.OK).body("Friendship request sent");
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<GetAllFriendships>> getAllFriendshipsByUserId(HttpServletRequest httpServletRequest) {

        UUID userId = jwtUtils.getUserIdFromCookie(httpServletRequest);
        List<GetAllFriendships> allFriendships = friendshiplService.getAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(allFriendships);

    }
}

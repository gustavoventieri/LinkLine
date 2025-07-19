package com.gustavoventieri.framework.adapter.controller;

import java.util.List;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.GetAllFriendships;
import org.gustavoventieri.domain.service.FriendshipService;
import org.gustavoventieri.domain.utils.JWTUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gustavoventieri.framework.adapter.dto.request.friendship.CreateFriendshipRequest;
import com.gustavoventieri.framework.adapter.dto.request.friendship.UpdateFriendshipRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsible for handling friendship-related operations.
 *
 * Provides endpoints for creating, retrieving, and updating friendship
 * requests.
 */
@RestController
@RequestMapping("/api/v1/friendship")
@RequiredArgsConstructor
@SecurityRequirement(name = "cookieAuth")
@Slf4j
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final JWTUtils jwtUtils;

    /**
     * Creates a new friendship request to the specified user.
     *
     * @param createFriendshipRequest Object containing the friend's username.
     * @param httpServletRequest      Request containing the authenticated user.
     * @return HTTP 200 if the request was sent successfully.
     */
    @PostMapping("/create")
    public ResponseEntity<String> createFriendship(
            @RequestBody @Valid CreateFriendshipRequest createFriendshipRequest,
            HttpServletRequest httpServletRequest) {

        UUID userId = jwtUtils.getUserIdFromCookie(httpServletRequest);
        friendshipService.createFriendship(userId, createFriendshipRequest.friendUsername());

        return ResponseEntity.status(HttpStatus.OK).body("Friendship request sent");
    }

    /**
     * Retrieves all friendship requests related to the authenticated user.
     *
     * @param httpServletRequest Request containing the authenticated user.
     * @return A list of friendship requests (sent and received).
     */
    @GetMapping("/get-all")
    public ResponseEntity<List<GetAllFriendships>> getAllFriendshipsByUserId(HttpServletRequest httpServletRequest) {

        UUID userId = jwtUtils.getUserIdFromCookie(httpServletRequest);
        List<GetAllFriendships> allFriendships = friendshipService.getAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(allFriendships);
    }

    /**
     * Updates the status of a friendship request.
     *
     * @param friendshipId            The ID of the friendship to update.
     * @param updateFriendshipRequest Object containing the new status (PENDING,
     *                                ACCEPTED, REJECTED).
     * @param httpServletRequest      Request containing the authenticated user.
     * @return HTTP 200 with success message if update was successful.
     */
    @PutMapping("/update/{friendshipId}")
    public ResponseEntity<String> updateFriendship(
            @PathVariable final UUID friendshipId,
            @RequestBody @Valid UpdateFriendshipRequest updateFriendshipRequest,
            HttpServletRequest httpServletRequest) {

        log.info("Request to update friendship {} to status {}", friendshipId, updateFriendshipRequest.status());

        UUID userId = jwtUtils.getUserIdFromCookie(httpServletRequest);
        friendshipService.updateFriendship(friendshipId, updateFriendshipRequest.status(), userId);
        return ResponseEntity.status(HttpStatus.OK).body("Friendship status successfully updated.");
    }
}

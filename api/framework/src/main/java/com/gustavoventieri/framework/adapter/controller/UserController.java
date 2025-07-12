package com.gustavoventieri.framework.adapter.controller;


import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gustavoventieri.framework.useCase.service.UserServiceImpl;
import com.gustavoventieri.framework.useCase.utils.JWTUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@SecurityRequirement(name = "cookieAuth")
@Slf4j
@Validated
public class UserController {
    

    private final JWTUtils jwtUtils;
    private final UserServiceImpl userServiceImpl;

//     @PostMapping("/search")
//     public ResponseEntity<List<PotentialFriendResponse>> findUsersByUsername(
        //     @Valid @RequestBody SearchUserRequest searchUserRequest,
        //     HttpServletRequest request
//     ) {
        // UUID currentUserId = jwtUtils.getUserIdFromCookie(request);
// 
        // log.info("Iniciando busca de usuários por '{}', limite={}, userId={}",
                // searchUserRequest.searchTerm(), searchUserRequest.limit(), currentUserId);
// 
        // List<PotentialFriendResponse> results = userServiceImpl.findUsersByUsername(
                // searchUserRequest.searchTerm(),
                // currentUserId,
                // searchUserRequest.limit()
        // );
// 
        // log.info("Busca concluída. {} usuários encontrados.", results.size());
        // return ResponseEntity.status(HttpStatus.OK).body(results);
//     }
}

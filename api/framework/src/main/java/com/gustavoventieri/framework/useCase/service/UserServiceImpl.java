package com.gustavoventieri.framework.useCase.service;

import java.util.List;
import java.util.UUID;

import org.gustavoventieri.domain.dto.response.PotentialFriendResponse;

import org.gustavoventieri.domain.service.UserService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public List<PotentialFriendResponse> findUsersByUsername(String searchTerm, UUID currentUserId, int searchLimit) {

        throw new UnsupportedOperationException("Unimplemented method 'findUsersByUsername'");
    }

}

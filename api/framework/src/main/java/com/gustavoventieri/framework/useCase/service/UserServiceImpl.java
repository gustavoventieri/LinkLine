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

    //private final UserRepositoryImpl userRepositoryImpl;

    @Override
    public List<PotentialFriendResponse> findUsersByUsername(String searchTerm, UUID currentUserId, int searchLimit) {
       
        throw new UnsupportedOperationException("Unimplemented method 'findUsersByUsername'");
    }
   

    

    // public List<PotentialFriendResponse> findUsersByUsername(String searchTerm, UUID currentUserId, int limit) {
        // List<UserDomain> users = userRepositoryImpl.searchByApproximateUsername(searchTerm.toLowerCase(), currentUserId);
// 
        
        // List<UserDomain> limitedUsers = users.stream()
                // .filter(user -> !user.id().equals(currentUserId))
                // .limit(limit)
                // .toList();
// 
        // return limitedUsers.stream()
                // .map(user -> {
                    // String status = findFriendshipStatusBetween(currentUserId, user.id());
                    // return new PotentialFriendResponse(
                            // user.username(),
                            // user.avatarUrl(),
                            // status
                    // );
                // })
                // .toList();
    // }
// 
    // private String findFriendshipStatusBetween(UUID currentUserId, UUID otherUserId) {
        // Optional<ChatRequestDomain> sentRequest = chatRequestRepository.findExisting(currentUserId, otherUserId);
        // Optional<ChatRequestDomain> receivedRequest = chatRequestRepository.findExisting(otherUserId, currentUserId);
// 
        // return Stream.of(sentRequest, receivedRequest)
                // .filter(Optional::isPresent)
                // .map(Optional::get)
                // .map(ChatRequestDomain::status)
                // .filter(status -> status == RequestStatus.ACCEPTED || status == RequestStatus.PENDING)
                // .map(Enum::name)
                // .findFirst()
                // .orElse(null);
    // }
    
}

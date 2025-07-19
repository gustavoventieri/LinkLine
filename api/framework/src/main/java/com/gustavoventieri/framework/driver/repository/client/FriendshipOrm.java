package com.gustavoventieri.framework.driver.repository.client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.gustavoventieri.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gustavoventieri.framework.entity.Friendship;

public interface FriendshipOrm extends JpaRepository<Friendship, UUID> {
   
    @Query("SELECT f FROM Friendship f WHERE " +
            "((f.user.id = :userId1 AND f.friend.id = :userId2) OR " +
            " (f.user.id = :userId2 AND f.friend.id = :userId1)) AND f.status IN :statuses")
    Optional<Friendship> findByUsersAndStatuses(@Param("userId1") UUID userId1,
            @Param("userId2") UUID userId2,
            @Param("statuses") List<RequestStatus> statuses);

    List<Friendship> findAllByUser_IdOrFriend_IdOrderByCreatedAtDesc(UUID userId1, UUID userId2);

}

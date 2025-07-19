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
                        "((f.sender.id = :userId1 AND f.receiver.id = :userId2) OR " +
                        " (f.sender.id = :userId2 AND f.receiver.id = :userId1)) AND f.status IN :statuses")
        Optional<Friendship> findByUsersAndStatuses(@Param("userId1") UUID userId1,
                        @Param("userId2") UUID userId2,
                        @Param("statuses") List<RequestStatus> statuses);

        List<Friendship> findAllBySender_IdOrReceiver_IdOrderByCreatedAtDesc(UUID userId1, UUID userId2);

}

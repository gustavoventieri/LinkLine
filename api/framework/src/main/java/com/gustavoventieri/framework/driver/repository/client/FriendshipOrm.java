package com.gustavoventieri.framework.driver.repository.client;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gustavoventieri.framework.entity.Friendship;

public interface FriendshipOrm extends JpaRepository<Friendship, UUID> {
    Optional<Friendship> findByUser_IdAndFriend_Id(UUID userId, UUID friendId);
}

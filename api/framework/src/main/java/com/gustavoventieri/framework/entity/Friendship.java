package com.gustavoventieri.framework.entity;

import jakarta.persistence.*;
import lombok.*;
import org.gustavoventieri.domain.enums.RequestStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "friendships")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-friendships")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "friend_id", nullable = false)
    @JsonBackReference(value = "friend-friendships")
    private User friend;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    @JsonBackReference(value = "requested-by-friendships")
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

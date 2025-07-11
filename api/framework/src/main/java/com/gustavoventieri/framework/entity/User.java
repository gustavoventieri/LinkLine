package com.gustavoventieri.framework.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String avatarUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // === Chats ===
    @JsonBackReference(value = "chat-participants")
    @ManyToMany(mappedBy = "participants")
    private Set<Chat> chats;

    // === Mensagens ===
    @JsonManagedReference(value = "messages-sent")
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messagesSent;

    // === Amizades ===
    @JsonManagedReference(value = "user-friendships")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Friendship> friendshipsUser;

    @JsonManagedReference(value = "friend-friendships")
    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Friendship> friendshipsFriend;

    @JsonManagedReference(value = "requested-by-friendships")
    @OneToMany(mappedBy = "requestedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Friendship> friendshipsRequested;
}

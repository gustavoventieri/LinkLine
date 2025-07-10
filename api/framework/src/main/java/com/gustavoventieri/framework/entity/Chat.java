package com.gustavoventieri.framework.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.gustavoventieri.domain.enums.ChatType;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatType type;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String avatarUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonManagedReference(value = "chat-participants")
    @ManyToMany
    @JoinTable(
        name = "chat_users",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants;

    @JsonManagedReference(value = "chat-messages")
    @OneToMany(mappedBy = "chatId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages;
}

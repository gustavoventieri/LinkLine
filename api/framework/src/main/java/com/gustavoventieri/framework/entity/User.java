package com.gustavoventieri.framework.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @JsonBackReference(value = "chat-participants")
    @ManyToMany(mappedBy = "participants")
    private Set<Chat> chats;

    @JsonManagedReference(value = "messages-sent")
    @OneToMany(mappedBy = "senderId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messagesSent;

    @JsonManagedReference(value = "messages-received")
    @OneToMany(mappedBy = "receiverId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messagesReceived;

    @JsonManagedReference(value = "sent-requests")
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatRequest> sentChatRequests;

    @JsonManagedReference(value = "received-requests")
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatRequest> receivedChatRequests;


}

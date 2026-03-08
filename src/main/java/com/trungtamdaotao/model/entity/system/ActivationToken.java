package com.trungtamdaotao.model.entity.system;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activation_tokens")
public class ActivationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, columnDefinition = "BIGINT")
    private UserAccount user;

    private String token;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    // Constructors, Getters, Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean isExpired(){
        return expiryTime.isAfter(LocalDateTime.now());
    }
}
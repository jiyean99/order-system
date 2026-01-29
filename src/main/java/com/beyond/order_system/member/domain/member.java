package com.beyond.order_system.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class member {
    @Id
    private Long id;
    private String email;
    private String password;
    private String name;
    private LocalDateTime createdTime;
}

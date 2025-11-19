package com.example.demo.models;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.utils.GameStatus;
import com.example.demo.utils.RouletteColor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;

    @Column
    private Integer winningNumber;

    @Enumerated(EnumType.STRING)
    @Column
    private RouletteColor winningColor;

    @Column
    private String spinSeed;

    @Column
    private LocalDateTime bettingTimeStart;

    @Column
    private LocalDateTime bettingTimeEnd;

    @Column
    private LocalDateTime start;

    @Column
    private LocalDateTime end;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;
}

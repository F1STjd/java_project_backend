package com.example.demo.repository;

import com.example.demo.models.Game;
import com.example.demo.utils.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByStatus(GameStatus status);

    List<Game> findByStatusAndBettingTimeStartBefore(GameStatus status, LocalDateTime time);

    List<Game> findByStatusAndBettingTimeEndBefore(GameStatus status, LocalDateTime time);

    List<Game> findByStatusAndStartBefore(GameStatus status, LocalDateTime time);

    boolean existsByStartBetween(LocalDateTime start, LocalDateTime end);
}

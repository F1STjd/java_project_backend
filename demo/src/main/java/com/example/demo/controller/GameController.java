package com.example.demo.controller;

import com.example.demo.models.Game;
import com.example.demo.service.GameService;
import com.example.demo.utils.GameStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GameResponse {
        private Long id;
        private GameStatus status;
        private Integer winningNumber;
        private String winningColor;
        private String resultHash;
        private String resultKey;
        private LocalDateTime bettingTimeStart;
        private LocalDateTime bettingTimeEnd;
        private LocalDateTime start;
        private LocalDateTime end;
        
        public static GameResponse fromGame(Game game) {
            GameResponse response = new GameResponse(
                game.getId(),
                game.getStatus(),
                null,
                null,
                game.getResultHash(),
                null,
                game.getBettingTimeStart(),
                game.getBettingTimeEnd(),
                game.getStart(),
                game.getEnd()
            );
            
            if (game.getStatus() == GameStatus.FINISHED || 
                game.getStatus() == GameStatus.SETTLED || 
                game.getStatus() == GameStatus.CANCELLED) {
                response.setWinningNumber(game.getWinningNumber());
                response.setWinningColor(game.getWinningColor() != null ? game.getWinningColor().toString() : null);
                response.setResultKey(game.getResultKey());
            }
            
            return response;
        }
    }

    @GetMapping("/planned")
    public ResponseEntity<List<GameResponse>> getPlannedGames() {
        return ResponseEntity.ok(
            gameService.getPlannedGames().stream()
                .map(GameResponse::fromGame)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/open")
    public ResponseEntity<List<GameResponse>> getOpenGames() {
        return ResponseEntity.ok(
            gameService.getOpenGames().stream()
                .map(GameResponse::fromGame)
                .collect(Collectors.toList())
        );
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GameResponse>> generateGames(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime targetDate) {
        List<Game> games = gameService.generateDailyGames(targetDate);
        return ResponseEntity.ok(
            games.stream()
                .map(GameResponse::fromGame)
                .collect(Collectors.toList())
        );
    }
}

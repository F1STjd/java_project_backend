package com.example.demo.service;

import com.example.demo.models.Game;
import com.example.demo.repository.GameRepository;
import com.example.demo.utils.GameStatus;
import com.example.demo.utils.RouletteColor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;

    @Transactional
    public List<Game> generateDailyGames(LocalDateTime targetDate) {
        log.info("Generating games starting from: {}", targetDate);

        List<Game> games = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            LocalDateTime gameStart = targetDate.plusMinutes(i * 3L + 1);
            
            if (!gameRepository.existsByStartBetween(gameStart.minusSeconds(30), gameStart.plusSeconds(30))) {
                Game game = createGame(gameStart);
                games.add(gameRepository.save(game));
                log.debug("Created game for time: {}", gameStart);
            }
        }

        log.info("Generated {} new games", games.size());
        return games;
    }

    private Game createGame(LocalDateTime startTime) {
        Game game = new Game();
        game.setStatus(GameStatus.PLANNED);
        
        generateProvablyFairResult(game);
        
        game.setBettingTimeStart(startTime.minusMinutes(1));
        game.setBettingTimeEnd(startTime);
        game.setStart(startTime);
        game.setEnd(startTime.plusMinutes(1));
        
        return game;
    }

    private void generateProvablyFairResult(Game game) {
        try {
            SecureRandom random = new SecureRandom();
            
            byte[] keyBytes = new byte[32];
            random.nextBytes(keyBytes);
            String key = Base64.getEncoder().encodeToString(keyBytes);
            
            int winningNumber = random.nextInt(37);
            RouletteColor winningColor = determineRouletteColor(winningNumber);
            
            String result = winningNumber + ":" + winningColor + ":" + key;
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(result.getBytes(StandardCharsets.UTF_8));
            String hash = Base64.getEncoder().encodeToString(hashBytes);
            
            game.setWinningNumber(winningNumber);
            game.setWinningColor(winningColor);
            game.setResultKey(key);
            game.setResultHash(hash);
            
            log.debug("Generated provably fair result - Number: {}, Color: {}, Hash: {}", 
                     winningNumber, winningColor, hash.substring(0, 10) + "...");
            
        } catch (Exception e) {
            log.error("Error generating provably fair result", e);
            throw new RuntimeException("Failed to generate game result", e);
        }
    }

    private RouletteColor determineRouletteColor(int number) {
        if (number == 0) {
            return RouletteColor.GREEN;
        }
        
        int[] redNumbers = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int red : redNumbers) {
            if (number == red) {
                return RouletteColor.RED;
            }
        }
        
        return RouletteColor.BLACK;
    }

    @Transactional
    public void openBetting() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> gamesToOpen = gameRepository.findByStatusAndBettingTimeStartBefore(
            GameStatus.PLANNED, now
        );

        for (Game game : gamesToOpen) {
            game.setStatus(GameStatus.BETTING_OPEN);
            gameRepository.save(game);
            log.info("Opened betting for game ID: {} at {}", game.getId(), now);
        }
    }

    @Transactional
    public void closeBetting() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> gamesToClose = gameRepository.findByStatusAndBettingTimeEndBefore(
            GameStatus.BETTING_OPEN, now
        );

        for (Game game : gamesToClose) {
            game.setStatus(GameStatus.BETTING_CLOSED);
            gameRepository.save(game);
            log.info("Closed betting for game ID: {} at {}", game.getId(), now);
        }
    }

    @Transactional
    public void startGames() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> gamesToStart = gameRepository.findByStatusAndStartBefore(
            GameStatus.BETTING_CLOSED, now
        );

        for (Game game : gamesToStart) {
            game.setStatus(GameStatus.SPINNING);
            gameRepository.save(game);
            log.info("Started game ID: {} at {} - Result: {} {}", 
                    game.getId(), now, game.getWinningNumber(), game.getWinningColor());
        }
    }

    @Transactional
    public void finishGames() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> gamesToFinish = gameRepository.findByStatusAndStartBefore(
            GameStatus.SPINNING, now.minusMinutes(1)
        );

        for (Game game : gamesToFinish) {
            game.setStatus(GameStatus.FINISHED);
            gameRepository.save(game);
            log.info("Finished game ID: {} - Revealed key for verification", game.getId());
        }
    }

    public List<Game> getPlannedGames() {
        return gameRepository.findByStatus(GameStatus.PLANNED);
    }

    public List<Game> getOpenGames() {
        return gameRepository.findByStatus(GameStatus.BETTING_OPEN);
    }
}

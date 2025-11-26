package com.example.demo.scheduler;

import com.example.demo.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameScheduler {

    private final GameService gameService;

    @Scheduled(cron = "0 45 23 * * *")
    public void generateNextDayGames() {
        log.info("Starting scheduled generation of games");
        try {
            LocalDateTime now = LocalDateTime.now();
            gameService.generateDailyGames(now);
            log.info("Successfully generated games");
        } catch (Exception e) {
            log.error("Error generating games", e);
        }
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void generateUpcomingGames() {
        log.info("Checking if more games need to be generated");
        try {
            LocalDateTime now = LocalDateTime.now();
            gameService.generateDailyGames(now);
        } catch (Exception e) {
            log.error("Error generating upcoming games", e);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void openScheduledBetting() {
        try {
            gameService.openBetting();
        } catch (Exception e) {
            log.error("Error opening betting", e);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void closeScheduledBetting() {
        try {
            gameService.closeBetting();
        } catch (Exception e) {
            log.error("Error closing betting", e);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void startScheduledGames() {
        try {
            gameService.startGames();
        } catch (Exception e) {
            log.error("Error starting games", e);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void finishScheduledGames() {
        try {
            gameService.finishGames();
        } catch (Exception e) {
            log.error("Error finishing games", e);
        }
    }
}

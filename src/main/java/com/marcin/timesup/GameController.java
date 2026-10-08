package com.marcin.timesup;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GameController {

    private final GameService gameService;


    public GameController(
            GameService gameService
    ) {

        this.gameService =
                gameService;
    }


    @PostMapping("/api/game/start")
    public Game startGame(
            @RequestBody StartGameRequest request
    ) {

        return gameService.startGame(
                request.teamNames()
        );
    }


    @GetMapping("/api/game")
    public Game getGame() {

        return gameService
                .getCurrentGame();
    }


    @PostMapping("/api/game/start-turn")
    public Game startTurn() {

        return gameService
                .startTurn();
    }


    @PostMapping("/api/game/guess")
    public Game guess() {

        return gameService
                .guess();
    }


    @PostMapping("/api/game/skip")
    public Game skip() {

        return gameService
                .skip();
    }


    @PostMapping("/api/game/next-round")
    public Game nextRound() {

        return gameService
                .nextRound();
    }
}
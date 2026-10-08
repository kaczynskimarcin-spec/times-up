package com.marcin.timesup;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class GameService {

    private final CardBank cardBank;

    private Game currentGame;


    public GameService(CardBank cardBank) {
        this.cardBank = cardBank;
    }


    public Game getCurrentGame() {
        checkTimer();

        return currentGame;
    }


    public Game startGame(List<String> teamNames) {

        List<String> validNames;

        if (teamNames == null) {

            validNames = new ArrayList<>();

        } else {

            validNames = teamNames
                    .stream()
                    .filter(name ->
                            name != null
                                    && !name.isBlank()
                    )
                    .map(String::trim)
                    .toList();
        }


        if (validNames.size() < 2) {

            validNames = List.of(
                    "Drużyna 1",
                    "Drużyna 2"
            );
        }


        Game game =
                new Game(validNames);


        List<Card> drawnCards =
                cardBank.drawCards(40);


        drawnCards.forEach(
                game::addCard
        );


        this.currentGame = game;


        return currentGame;
    }


    public Game startTurn() {

        if (currentGame == null) {
            return null;
        }


        checkTimer();


        currentGame.startTurn();


        return currentGame;
    }


    public Game guess() {

        if (currentGame == null) {
            return null;
        }


        checkTimer();


        currentGame.guessCurrentCard();


        return currentGame;
    }


    public Game skip() {

        if (currentGame == null) {
            return null;
        }


        checkTimer();


        currentGame.skipCurrentCard();


        return currentGame;
    }


    public Game nextRound() {

        if (currentGame == null) {
            return null;
        }


        currentGame.startNextRound();


        return currentGame;
    }


    private void checkTimer() {

        if (currentGame == null) {
            return;
        }


        if (currentGame.isTurnExpired()) {
            currentGame.endTurn();
        }
    }
}
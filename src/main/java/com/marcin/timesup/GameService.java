package com.marcin.timesup;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private Game currentGame;

    public Game startGame() {

        Game game = new Game("Drużyna A", "Drużyna B");

        CardBank cardBank = new CardBank();

        List<Card> drawnCards = cardBank.drawCards(40);

        for (Card card : drawnCards) {
            game.addCard(card.getText());
        }

        this.currentGame = game;

        return currentGame;
    }

    public Game getCurrentGame() {

        checkTimer();

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
package com.marcin.timesup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private final Team teamA;
    private final Team teamB;

    private final List<Card> cards;
    private final List<Round> rounds;

    private int currentRound;
    private String currentTeam;
    private int currentCardIndex;

    private GameStatus status;

    private long turnEndTimeMillis;

    public Game(String teamAName, String teamBName) {

        this.teamA = new Team(teamAName);
        this.teamB = new Team(teamBName);

        this.cards = new ArrayList<>();

        this.rounds = List.of(
                new Round(1, 45, false, "Opisuj hasło"),
                new Round(2, 60, true, "Jedno słowo"),
                new Round(3, 60, true, "Kalambury")
        );

        this.currentRound = 1;
        this.currentTeam = "A";
        this.currentCardIndex = 0;

        this.status = GameStatus.WAITING_FOR_TURN;

        this.turnEndTimeMillis = 0;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public String getCurrentTeam() {
        return currentTeam;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getCurrentRoundDurationSeconds() {
        return rounds
                .get(currentRound - 1)
                .getDurationSeconds();
    }

    public int getSecondsRemaining() {

        if (status != GameStatus.TURN_ACTIVE) {
            return 0;
        }

        long remainingMillis =
                turnEndTimeMillis - System.currentTimeMillis();

        if (remainingMillis <= 0) {
            return 0;
        }

        return (int) Math.ceil(remainingMillis / 1000.0);
    }

    public boolean isTurnExpired() {
        return status == GameStatus.TURN_ACTIVE
                && getSecondsRemaining() <= 0;
    }

    public void addCard(String text) {
        cards.add(new Card(text));
    }

    public Card getCurrentCard() {

        if (status == GameStatus.GAME_FINISHED) {
            return null;
        }

        if (status == GameStatus.ROUND_FINISHED) {
            return null;
        }

        if (currentCardIndex >= cards.size()) {
            return null;
        }

        return cards.get(currentCardIndex);
    }

    public void startTurn() {

        if (status != GameStatus.WAITING_FOR_TURN) {
            return;
        }

        int durationSeconds = getCurrentRoundDurationSeconds();

        turnEndTimeMillis =
                System.currentTimeMillis()
                        + durationSeconds * 1000L;

        status = GameStatus.TURN_ACTIVE;
    }

    public void endTurn() {

        if (status != GameStatus.TURN_ACTIVE) {
            return;
        }

        switchTeam();

        turnEndTimeMillis = 0;

        status = GameStatus.WAITING_FOR_TURN;
    }

    public void guessCurrentCard() {

        if (status != GameStatus.TURN_ACTIVE) {
            return;
        }

        if (currentCardIndex >= cards.size()) {
            return;
        }

        if (currentTeam.equals("A")) {
            teamA.addPoint();
        } else {
            teamB.addPoint();
        }

        currentCardIndex++;

        if (currentCardIndex >= cards.size()) {
            finishRound();
        }
    }

    public boolean skipCurrentCard() {

        if (status != GameStatus.TURN_ACTIVE) {
            return false;
        }

        if (currentRound == 1) {
            return false;
        }

        if (currentCardIndex >= cards.size()) {
            return false;
        }

        Card skippedCard = cards.remove(currentCardIndex);

        cards.add(skippedCard);

        return true;
    }

    private void finishRound() {

        turnEndTimeMillis = 0;

        if (currentRound == 3) {
            status = GameStatus.GAME_FINISHED;
            return;
        }

        switchTeam();

        status = GameStatus.ROUND_FINISHED;
    }

    public void startNextRound() {

        if (status != GameStatus.ROUND_FINISHED) {
            return;
        }

        currentRound++;

        currentCardIndex = 0;

        Collections.shuffle(cards);

        status = GameStatus.WAITING_FOR_TURN;
    }

    private void switchTeam() {

        if (currentTeam.equals("A")) {
            currentTeam = "B";
        } else {
            currentTeam = "A";
        }
    }
}
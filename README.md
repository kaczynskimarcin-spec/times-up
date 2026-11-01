# Time's Up

**A browser-based team party game built with Java and Spring Boot.**

Time's Up turns a classic guessing game into a simple web experience. Create teams, draw a set of 40 cards and compete across three rounds with different rules. The backend manages game state, scoring and turn timers; the frontend keeps the game easy to play on a phone or laptop.

## Highlights

- **Three rounds, one deck** — describe, use one word, then act out the same cards.
- **Team-based gameplay** — turn rotation, scores and a final leaderboard.
- **Server-side game logic** — timers, card handling and round transitions in Java.
- **Categorized card bank** — phrases loaded from JSON resources.
- **Mobile-friendly interface** — responsive frontend with a web app manifest and service worker.

## Tech stack

| Layer | Technology |
| --- | --- |
| Backend | Java 25, Spring Boot 4, REST API |
| Frontend | HTML, CSS, vanilla JavaScript |
| Data | JSON card collections |
| Build | Maven Wrapper |

## Game rules

| Round | Time per turn | Rule | Skip |
| --- | --- | --- | --- |
| 1 | 45 seconds | Describe the word or phrase | No |
| 2 | 60 seconds | Give a one-word clue | Yes |
| 3 | 60 seconds | Use gestures (charades) | Yes |

Each new game draws **40 cards**. Teams compete for points over all three rounds.

## Run locally

**Requirements:** JDK 25 and an internet connection for the initial Maven dependency download.

```bash
git clone https://github.com/kaczynskimarcin-spec/times-up.git
cd times-up
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run` instead.

Open **http://localhost:8080** in your browser.

## API overview

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/game/start` | Start a game with team names |
| `GET` | `/api/game` | Read the current game state |
| `POST` | `/api/game/start-turn` | Start a timed turn |
| `POST` | `/api/game/guess` | Mark the current card as guessed |
| `POST` | `/api/game/skip` | Skip a card when allowed |
| `POST` | `/api/game/next-round` | Advance to the next round |
| `GET` | `/api/cards/stats` | View card counts by category |

**Example — start a game:**

```bash
curl -X POST http://localhost:8080/api/game/start \
  -H "Content-Type: application/json" \
  -d '{"teamNames":["Team A","Team B"]}'
```

## Project structure

```text
src/main/
├── java/com/marcin/timesup/     # Game model, services and REST controllers
└── resources/
    ├── data/cards/              # Card categories stored as JSON
    └── static/                  # Frontend, manifest and service worker
```

## Current scope

This is a personal project developed to practice Spring Boot, API design and game-state modeling. The current backend stores **one active game in memory per application instance**; persistent multiplayer sessions are not implemented in this version.

---

Built by [Marcin Kaczyński](https://github.com/kaczynskimarcin-spec).

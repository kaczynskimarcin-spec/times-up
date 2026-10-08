let currentGame = null;


const screens = [
    "startScreen",
    "waitingScreen",
    "gameScreen",
    "roundFinishedScreen",
    "finishedScreen"
];


// ======================================================
// EKRANY
// ======================================================

function showScreen(screenId) {

    screens.forEach(id => {

        const screen =
            document.getElementById(id);

        if (screen) {
            screen.classList.add("hidden");
        }
    });


    const selectedScreen =
        document.getElementById(screenId);

    if (selectedScreen) {
        selectedScreen.classList.remove("hidden");
    }
}


// ======================================================
// DRUŻYNY - FRONTEND
// ======================================================

function addTeam() {

    const container =
        document.getElementById("teamInputs");

    const number =
        container.children.length + 1;


    const row =
        document.createElement("div");

    row.className =
        "team-input-row";


    row.innerHTML = `
        <input
            class="team-name-input"
            type="text"
            maxlength="20"
            placeholder="Drużyna ${number}">

        <button
            class="remove-team-button"
            type="button"
            onclick="removeTeam(this)">
            ×
        </button>
    `;


    container.appendChild(row);


    const input =
        row.querySelector("input");

    input.focus();
}


function removeTeam(button) {

    const rows =
        document.querySelectorAll(
            ".team-input-row"
        );


    if (rows.length <= 2) {

        alert(
            "Muszą być przynajmniej 2 drużyny."
        );

        return;
    }


    button
        .closest(".team-input-row")
        .remove();
}


// ======================================================
// START GRY
// ======================================================

async function startGame() {

    const inputs =
        document.querySelectorAll(
            ".team-name-input"
        );


    const teamNames =
        Array.from(inputs)
            .map(input =>
                input.value.trim()
            )
            .filter(name =>
                name.length > 0
            );


    if (teamNames.length < 2) {

        alert(
            "Dodaj przynajmniej 2 drużyny."
        );

        return;
    }


    try {

        const response =
            await fetch(
                "/api/game/start",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        teamNames: teamNames
                    })
                }
            );


        if (!response.ok) {

            throw new Error(
                `Start game error: ${response.status}`
            );
        }


        currentGame =
            await response.json();


        renderGame();

    } catch (error) {

        console.error(error);

        alert(
            "Nie udało się rozpocząć gry."
        );
    }
}


// ======================================================
// START TURY
// ======================================================

async function startTurn() {

    await runCountdown();


    try {

        const response =
            await fetch(
                "/api/game/start-turn",
                {
                    method: "POST"
                }
            );


        if (!response.ok) {

            throw new Error(
                `Start turn error: ${response.status}`
            );
        }


        currentGame =
            await response.json();


        renderGame();

    } catch (error) {

        console.error(error);

        alert(
            "Nie udało się rozpocząć tury."
        );
    }
}


// ======================================================
// ZGADNIĘCIE
// ======================================================

async function guessCard() {

    if (
        !currentGame ||
        currentGame.status !== "TURN_ACTIVE"
    ) {
        return;
    }


    animateCard("right");


    try {

        const response =
            await fetch(
                "/api/game/guess",
                {
                    method: "POST"
                }
            );


        if (!response.ok) {

            throw new Error(
                `Guess error: ${response.status}`
            );
        }


        currentGame =
            await response.json();


        if ("vibrate" in navigator) {

            navigator.vibrate(30);
        }


        setTimeout(() => {

            renderGame();

        }, 150);

    } catch (error) {

        console.error(error);
    }
}


// ======================================================
// SKIP
// ======================================================

async function skipCard() {

    if (
        !currentGame ||
        currentGame.status !== "TURN_ACTIVE"
    ) {
        return;
    }


    const round =
        getCurrentRound();


    if (!round.skipAllowed) {
        return;
    }


    animateCard("left");


    try {

        const response =
            await fetch(
                "/api/game/skip",
                {
                    method: "POST"
                }
            );


        if (!response.ok) {

            throw new Error(
                `Skip error: ${response.status}`
            );
        }


        currentGame =
            await response.json();


        setTimeout(() => {

            renderGame();

        }, 150);

    } catch (error) {

        console.error(error);
    }
}


// ======================================================
// NASTĘPNA RUNDA
// ======================================================

async function nextRound() {

    try {

        const response =
            await fetch(
                "/api/game/next-round",
                {
                    method: "POST"
                }
            );


        if (!response.ok) {

            throw new Error(
                `Next round error: ${response.status}`
            );
        }


        currentGame =
            await response.json();


        renderGame();

    } catch (error) {

        console.error(error);
    }
}


// ======================================================
// POBIERANIE AKTUALNEGO STANU GRY
// ======================================================

async function refreshGame() {

    if (!currentGame) {
        return;
    }


    try {

        const response =
            await fetch(
                "/api/game",
                {
                    cache: "no-store"
                }
            );


        if (!response.ok) {
            return;
        }


        const text =
            await response.text();


        if (!text) {
            return;
        }


        currentGame =
            JSON.parse(text);


        renderGame();

    } catch (error) {

        console.error(error);
    }
}


// ======================================================
// GŁÓWNY RENDER
// ======================================================

function renderGame() {

    if (!currentGame) {

        showScreen("startScreen");

        return;
    }


    switch (currentGame.status) {

        case "WAITING_FOR_TURN":

            renderWaiting();

            break;


        case "TURN_ACTIVE":

            renderTurn();

            break;


        case "ROUND_FINISHED":

            renderRoundFinished();

            break;


        case "GAME_FINISHED":

            renderFinished();

            break;


        default:

            console.error(
                "Nieznany status gry:",
                currentGame.status
            );
    }
}


// ======================================================
// EKRAN OCZEKIWANIA
// ======================================================

function renderWaiting() {

    showScreen(
        "waitingScreen"
    );


    document
        .getElementById("waitingRound")
        .innerText =
        `RUNDA ${currentGame.currentRound}`;


    document
        .getElementById("waitingTeam")
        .innerText =
        getTeamName().toUpperCase();


    document
        .getElementById("roundDescription")
        .innerText =
        getRoundDescription();


    const round =
        getCurrentRound();


    document
        .querySelector(".round-time")
        .innerText =
        `⏱ ${round.durationSeconds} SEKUND`;
}


// ======================================================
// EKRAN GRY
// ======================================================

function renderTurn() {

    showScreen(
        "gameScreen"
    );


    const round =
        getCurrentRound();


    document
        .getElementById("roundNumber")
        .innerText =
        `RUNDA ${currentGame.currentRound}`;


    document
        .getElementById("roundRule")
        .innerText =
        round.rule.toUpperCase();


    document
        .getElementById("currentTeam")
        .innerText =
        getTeamName().toUpperCase();


    if (currentGame.currentCard) {

        document
            .getElementById("cardText")
            .innerText =
            currentGame.currentCard.text;
    }


    updateTimer();

    updateScores();


    const skipButton =
        document.getElementById(
            "skipButton"
        );


    const actionButtons =
        document.querySelector(
            ".action-buttons"
        );


    if (round.skipAllowed) {

        skipButton.style.display =
            "block";

        actionButtons.style
            .gridTemplateColumns =
            "1fr 2fr";

    } else {

        skipButton.style.display =
            "none";

        actionButtons.style
            .gridTemplateColumns =
            "1fr";
    }
}


// ======================================================
// SCOREBOARD
// ======================================================

function updateScores() {

    const scoreboard =
        document.getElementById(
            "scoreboard"
        );


    if (
        !scoreboard ||
        !currentGame ||
        !currentGame.teams
    ) {
        return;
    }


    scoreboard.innerHTML = "";


    currentGame.teams.forEach(
        (team, index) => {

            const element =
                document.createElement("div");


            element.className =
                "score-team";


            if (
                index ===
                currentGame.currentTeamIndex
            ) {

                element.classList.add(
                    "active"
                );
            }


            const name =
                document.createElement("span");

            name.textContent =
                team.name.toUpperCase();


            const score =
                document.createElement("strong");

            score.textContent =
                team.score;


            element.appendChild(name);

            element.appendChild(score);

            scoreboard.appendChild(
                element
            );
        }
    );
}


// ======================================================
// KONIEC RUNDY
// ======================================================

function renderRoundFinished() {

    showScreen(
        "roundFinishedScreen"
    );


    document
        .getElementById("finishedRound")
        .innerText =
        `RUNDA ${currentGame.currentRound}`;


    renderTeamResults(
        "roundScores"
    );


    document
        .getElementById("nextRoundText")
        .innerText =
        getNextRoundText();
}


// ======================================================
// KONIEC GRY
// ======================================================

function renderFinished() {

    showScreen(
        "finishedScreen"
    );


    renderTeamResults(
        "finalScores"
    );


    const maxScore =
        Math.max(
            ...currentGame.teams.map(
                team => team.score
            )
        );


    const winners =
        currentGame.teams.filter(
            team =>
                team.score === maxScore
        );


    let winnerText;


    if (winners.length === 1) {

        winnerText =
            `${winners[0].name.toUpperCase()} WYGRYWA!`;

    } else {

        winnerText =
            "REMIS!";
    }


    document
        .getElementById("winner")
        .innerText =
        winnerText;
}


// ======================================================
// WYNIKI DRUŻYN
// ======================================================

function renderTeamResults(
    containerId
) {

    const container =
        document.getElementById(
            containerId
        );


    if (!container) {
        return;
    }


    container.innerHTML = "";


    const sortedTeams =
        [...currentGame.teams]
            .sort(
                (a, b) =>
                    b.score - a.score
            );


    sortedTeams.forEach(
        (team, index) => {

            const row =
                document.createElement("div");


            row.className =
                "result-row";


            const name =
                document.createElement("span");


            name.textContent =
                `${index + 1}. ${team.name}`;


            const score =
                document.createElement("strong");


            score.textContent =
                team.score;


            row.appendChild(name);

            row.appendChild(score);

            container.appendChild(row);
        }
    );
}


// ======================================================
// TIMER
// ======================================================

function updateTimer() {

    const seconds =
        currentGame.secondsRemaining ?? 0;


    const timer =
        document.getElementById(
            "timer"
        );


    timer.innerText =
        formatTime(seconds);


    const duration =
        currentGame
            .currentRoundDurationSeconds;


    const percentage =
        Math.max(
            0,
            Math.min(
                100,
                (seconds / duration) * 100
            )
        );


    document
        .getElementById(
            "timerProgress"
        )
        .style.width =
        `${percentage}%`;


    if (seconds <= 5) {

        timer.classList.add(
            "danger"
        );

    } else {

        timer.classList.remove(
            "danger"
        );
    }
}


function formatTime(seconds) {

    const minutes =
        Math.floor(seconds / 60);


    const remainingSeconds =
        seconds % 60;


    return (
        String(minutes)
            .padStart(2, "0")
        +
        ":"
        +
        String(remainingSeconds)
            .padStart(2, "0")
    );
}


// ======================================================
// HELPERY
// ======================================================

function getCurrentRound() {

    return currentGame.rounds[
    currentGame.currentRound - 1
        ];
}


function getCurrentTeam() {

    if (!currentGame) {
        return null;
    }


    if (
        currentGame.currentTeam &&
        typeof currentGame.currentTeam === "object"
    ) {

        return currentGame.currentTeam;
    }


    if (
        currentGame.teams &&
        currentGame.teams.length > 0
    ) {

        return currentGame.teams[
            currentGame.currentTeamIndex
            ];
    }


    return null;
}


function getTeamName() {

    const team =
        getCurrentTeam();


    if (!team) {

        return "Drużyna";
    }


    return team.name;
}


function getRoundDescription() {

    switch (
        currentGame.currentRound
        ) {

        case 1:

            return (
                "Opisuj hasło dowolnie. " +
                "Nie można pomijać."
            );


        case 2:

            return (
                "Możesz powiedzieć tylko jedno słowo. " +
                "Można pomijać."
            );


        case 3:

            return (
                "Kalambury. Bez mówienia. " +
                "Można pomijać."
            );


        default:

            return "";
    }
}


function getNextRoundText() {

    if (
        currentGame.currentRound === 1
    ) {

        return (
            "Następna runda: JEDNO SŁOWO"
        );
    }


    if (
        currentGame.currentRound === 2
    ) {

        return (
            "Następna runda: KALAMBURY"
        );
    }


    return "";
}


// ======================================================
// ANIMACJA KARTY
// ======================================================

function animateCard(direction) {

    const card =
        document.getElementById(
            "gameCard"
        );


    if (!card) {
        return;
    }


    if (direction === "right") {

        card.style.transform =
            "translateX(120%) rotate(15deg)";

    } else {

        card.style.transform =
            "translateX(-120%) rotate(-15deg)";
    }


    card.style.opacity =
        "0";


    setTimeout(() => {

        card.style.transition =
            "none";


        card.style.transform =
            "translateX(0) rotate(-1deg)";


        card.style.opacity =
            "1";


        setTimeout(() => {

            card.style.transition =
                "transform 0.25s ease, opacity 0.25s ease";

        }, 20);

    }, 150);
}


// ======================================================
// COUNTDOWN 3 2 1 GO
// ======================================================

function wait(milliseconds) {

    return new Promise(
        resolve => {

            setTimeout(
                resolve,
                milliseconds
            );
        }
    );
}


async function runCountdown() {

    const overlay =
        document.getElementById(
            "countdownOverlay"
        );


    const number =
        document.getElementById(
            "countdownNumber"
        );


    overlay.classList.remove(
        "hidden"
    );


    const values =
        [
            "3",
            "2",
            "1",
            "GO!"
        ];


    for (const value of values) {

        number.classList.remove(
            "go"
        );


        number.innerText =
            value;


        if (value === "GO!") {

            number.classList.add(
                "go"
            );


            if ("vibrate" in navigator) {

                navigator.vibrate(
                    100
                );
            }
        }


        number.style.animation =
            "none";


        number.offsetHeight;


        number.style.animation =
            "";


        if (value === "GO!") {

            await wait(600);

        } else {

            await wait(800);
        }
    }


    overlay.classList.add(
        "hidden"
    );
}


// ======================================================
// POLLING TIMERA
// ======================================================

setInterval(() => {

    if (
        currentGame &&
        currentGame.status ===
        "TURN_ACTIVE"
    ) {

        refreshGame();
    }

}, 500);


// ======================================================
// BLOKOWANIE ZOOMU MOBILNEGO
// ======================================================

document.addEventListener(
    "gesturestart",
    event => {

        event.preventDefault();
    }
);


document.addEventListener(
    "gesturechange",
    event => {

        event.preventDefault();
    }
);


document.addEventListener(
    "gestureend",
    event => {

        event.preventDefault();
    }
);


document.addEventListener(
    "touchmove",
    event => {

        if (
            event.touches.length > 1
        ) {

            event.preventDefault();
        }

    },
    {
        passive: false
    }
);


// blokowanie double-tap zoom

let lastTouchEnd = 0;


document.addEventListener(
    "touchend",
    event => {

        const now =
            Date.now();


        if (
            now - lastTouchEnd <= 300
        ) {

            event.preventDefault();
        }


        lastTouchEnd =
            now;

    },
    {
        passive: false
    }
);


// ======================================================
// PWA WYŁĄCZONE NA CZAS DEVELOPMENTU
// ======================================================

// Usuwamy starego Service Workera,
// żeby nie trzymał starych wersji JS/CSS.

if (
    "serviceWorker" in navigator
) {

    navigator
        .serviceWorker
        .getRegistrations()
        .then(registrations => {

            registrations.forEach(
                registration => {

                    registration.unregister();
                }
            );
        });
}
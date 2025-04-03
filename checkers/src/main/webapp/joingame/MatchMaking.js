
class Matchmaker {
    constructor() {
        this.queue = []; // Queue for players waiting for a match
    }

    addToQueue(playerID) {
        // TODO: Send event to Page Manager that player has joined queue

        // Adds a player to the matchmaking queue.
        // TODO: Implement logic to send event to Page Manager when a player joins the queue.
        // Use {string} for playerID - unique ID of the player joining the queue.
    }

    requestPlayerMatch(playerID) {
        // TODO: Send event to Page Manager requesting a player match

        // Handles when a player clicks the "Challenge Player" button.
        // TODO: Notify Page Manager that the player wants a human opponent.
        // Use {string} playerID - unique ID of the player that wants to challenge.
    }

    requestBotMatch(playerID) {
        // TODO: Send event to Page Manager requesting a bot match

        // Handles when a player clicks the "Challenge Bot" button.
        // TODO: Notify Page Manager that the player wants to play against a bot.
        // Use {string} playerID - unique ID of the player that wants a bot match.
    }

    removeFromQueue(playerID) {
        // TODO: Remove player from queue and notify Page Manager

        // TODO: Implement logic to remove player and notify Page Manager.
        // Use {string} playerID - unique ID of the player to be removed.

    }
}

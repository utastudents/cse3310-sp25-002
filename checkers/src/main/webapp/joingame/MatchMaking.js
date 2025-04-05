
class Matchmaker {
    constructor() {
        this.queue = []; // Queue for players waiting for a match
    }

    addToQueue(playerID) {
        // TODO: Send event to Join Game - Communication.js that player has joined queue

        // Adds a player to the matchmaking queue.
        // TODO: Implement logic to send event to Join Game - Communication.js when a player joins the queue.
        // Use {string} for playerID - unique ID of the player joining the queue.
    }

    requestPlayerMatch(playerID) {
        // TODO: Send event to Join Game - Communication.js requesting a player match

        // Handles when a player clicks the "Challenge Player" button.
        // TODO: Notify Join Game - Communication.js that the player wants a human opponent.
        // Use {string} playerID - unique ID of the player that wants to challenge.
        // Should be a button
    }

    requestBotMatch(playerID) {
        // TODO: Send event to Join Game - Communication.js requesting a bot match

        // Handles when a player clicks the "Challenge Bot" button.
        // TODO: Notify Join Game - Communication.js that the player wants to play against a bot.
        // Use {string} playerID - unique ID of the player that wants a bot match.
        // Should be a button
    }


    requestSpectateBotVsBot(playerID) {
        // TODO: Send signal to Join Game - Communication.js that this player wants to watch two bots play.
        // Called when a player wants to spectate a match between two bots.
        // Use {string} playerID - ID of the player requesting to watch bots.
        // Should be a button
    }

    removeFromQueue(playerID) {
        // TODO: Remove player from queue and notify Join Game - Communication.js

        // TODO: Implement logic to remove player and notify Join Game - Communication.js.
        // Use {string} playerID - unique ID of the player to be removed.

    }
}

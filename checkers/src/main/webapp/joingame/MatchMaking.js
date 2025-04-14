
class Matchmaker {
    constructor() {
        this.queue = []; // Queue for players waiting for a match
        this.communication = new Communication(); // Created an instance of the communication class
    }

    addToQueue(playerID) {
        // TODO: Send event to Join Game - Communication.js that player has joined queue

        // Adds a player to the matchmaking queue.
        // TODO: Implement logic to send event to Join Game - Communication.js when a player joins the queue.
        // Use {string} for playerID - unique ID of the player joining the queue.
        
        this.queue.push(playerID); // Add the player to the queue

        // I'm not sure we need to let communication know when a player joins?
    }

    requestPlayerMatch(playerID) {
        // TODO: Send event to Join Game - Communication.js requesting a player match

        // Handles when a player clicks the "Challenge Player" button.
        // TODO: Notify Join Game - Communication.js that the player wants a human opponent.
        // Use {string} playerID - unique ID of the player that wants to challenge.
        // Should be a button

        this.communication.sendJoinGameRequest(playerID, "Human");
        console.log("Player" + playerID + "requested a match against another player");
        this.removeFromQueue(playerID);
    }

    requestBotMatch(playerID) {
        // TODO: Send event to Join Game - Communication.js requesting a bot match

        // Handles when a player clicks the "Challenge Bot" button.
        // TODO: Notify Join Game - Communication.js that the player wants to play against a bot.
        // Use {string} playerID - unique ID of the player that wants a bot match.
        // Should be a button

        this.communication.sendJoinGameRequest(playerID, "Bot");
        console.log("Player" + playerID + "requested a match against a Bot");
        this.removeFromQueue(playerID);
    }


    requestSpectateBotVsBot(playerID) {
        // TODO: Send signal to Join Game - Communication.js that this player wants to watch two bots play.
        // Called when a player wants to spectate a match between two bots.
        // Use {string} playerID - ID of the player requesting to watch bots.
        // Should be a button

        this.communication.sendJoinGameRequest(playerID, "Spectate"); // Ensure Page Manager is aware that "Spectate" is an option
        console.log("Setting up a bot vs bot match to spectate");
        this.removeFromQueue(playerID);
    }

    removeFromQueue(playerID) {
        // TODO: Remove player from queue and notify Join Game - Communication.js

        // TODO: Implement logic to remove player and notify Join Game - Communication.js.
        // Use {string} playerID - unique ID of the player to be removed.

        this.queue = this.queue.filter(id => id !== playerID);
        console.log("Player" + playerID + "removed from queue");
        
    }
}

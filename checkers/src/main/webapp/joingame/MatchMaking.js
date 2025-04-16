class Matchmaker {
    constructor() {
        this.queue = []; // Queue for players waiting for a match
        this.dataManager = new DataManager(); // Created an instance of the data manager class
        this.communication = new Communication(this.dataManager); // Created an instance of the communication class
    }

    addToQueue(playerID) {

        this.queue.push(playerID); // Add the player to the queue
    }

    requestPlayerMatch(playerID) {

        // Handles when a player clicks the "Challenge Player" button.
        // Should be a button

        this.communication.sendJoinGameRequest(playerID, "Human"); //Send the request to Join Game - Communication.js
        console.log("Player" + playerID + "requested a match against another player");
        this.removeFromQueue(playerID);
    }

    requestBotMatch(playerID) {

        // Handles when a player clicks the "Challenge Bot" button.
        // Should be a button

        this.communication.sendJoinGameRequest(playerID, "Bot"); //Send the request to Join Game - Communication.js
        console.log("Player" + playerID + "requested a match against a Bot");
        this.removeFromQueue(playerID);
    }


    requestSpectateBotVsBot(playerID) {
        // Called when a player wants to spectate a match between two bots.
        // Should be a button

        this.communication.sendJoinGameRequest(playerID, "Spectate"); // Ensure Page Manager is aware that "Spectate" is an option
        console.log("Setting up a bot vs bot match to spectate");
        this.removeFromQueue(playerID);
    }

    removeFromQueue(playerID) {
        // Handles when a player leaves the queue.

        this.queue = this.queue.filter(id => id !== playerID); //Remove the player from the queue
        console.log("Player" + playerID + "removed from queue");
        
    }
}

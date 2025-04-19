class Matchmaking {
    constructor(communication, data, notification, playerWaitlist, botWaitlist) {
        this.queue = []; // Queue for players waiting for a match
        this.data = new Data(); // Created an instance of the data manager class
        this.communication = new Communication(this.data); // Created an instance of the communication class
        this.notification = notification;
        this.playerWaitlist = playerWaitlist;
        this.botWaitlist = botWaitlist;
    }

    addToQueue(playerID) {

        this.queue.push(playerID); // Add the player to the queue
    }

    requestPlayerMatch(playerID) {

        // Handles when a player clicks the "Challenge Player" button.
        // Should be a button

        const player = this.data.getPlayer();
        if (!player) {
            this.notification.displayNotification("No player data available");
            return;
        }

        this.playerWaitlist.remove(player.getID());
        this.data.setGameMode("Human");
        this.communication.sendPlayerAttributes(this.data.getGameMode());
        this.notification.displayNotification("Finding player match...");
    }

    requestBotMatch(playerID) {

        // Handles when a player clicks the "Challenge Bot" button.
        // Should be a button

        const player = this.data.getPlayer();
        if (!player) {
            this.notification.displayNotification("No player data available");
            return;
        }

        this.botWaitlist.remove(player.getID());
        this.data.setGameMode("Bot");
        this.communication.sendPlayerAttributes(this.data.getGameMode());
        this.notification.displayNotification("Starting bot match...");
    }
    


    requestSpectateBotVsBot(playerID) {
        // Called when a player wants to spectate a match between two bots.
        // Should be a button
        this.data.setGameMode("Spectate");
        this.communication.sendPlayerAttributes(this.data.getGameMode());
        this.notification.displayNotification("Loading bot vs bot match...");
    }

    removeFromQueue(playerID) {
        // Handles when a player leaves the queue.

        this.queue = this.queue.filter(id => id !== playerID); //Remove the player from the queue
        console.log("Player" + playerID + "removed from queue");
        
    }
}

class Matchmaking {
    constructor(communication, dataManager, notification, playerWaitlist, botWaitlist) {
        this.queue = []; // Queue for players waiting for a match
        this.dataManager = dataManager; // Use the passed data manager
        this.communication = communication; // Use the passed communication instance
        this.notification = notification;
        this.playerWaitlist = playerWaitlist;
        this.botWaitlist = botWaitlist;
    }

    addToQueue(playerID) {
        // Check if player is already in queue
        if (this.queue.includes(playerID)) {
            console.log(`Player ${playerID} is already in queue`);
            return;
        }

        this.queue.push(playerID); // Add the player to the queue
        console.log(`Player ${playerID} added to queue`);
    }

    requestPlayerMatch(playerID) {
        // Handles when a player clicks the "Challenge Player" button

        const player = this.dataManager.getPlayer();
        if (!player) {
            this.notification.displayNotification("No player data available", "error");
            return;
        }

        // Remove from waitlist
        this.playerWaitlist.remove(playerID);

        // Set game mode
        this.dataManager.setGameMode("Human");

        // Send player attributes to Page Manager
        this.communication.sendPlayerAttributes(this.dataManager.getGameMode());

        // Display notification
        this.notification.displayNotification("Finding player match...");

        // Add to internal queue
        this.addToQueue(playerID);
    }

    requestBotMatch(playerID) {
        // Handles when a player clicks the "Challenge Bot" button

        const player = this.dataManager.getPlayer();
        if (!player) {
            this.notification.displayNotification("No player data available", "error");
            return;
        }

        // Remove from waitlist
        this.botWaitlist.remove(playerID);

        // Set game mode
        this.dataManager.setGameMode("Bot");

        // Send player attributes to Page Manager
        this.communication.sendPlayerAttributes(this.dataManager.getGameMode());

        // Display notification
        this.notification.displayNotification("Starting bot match...");
    }

    requestSpectateBotVsBot(playerID) {
        // Called when a player wants to spectate a match between two bots

        // Set game mode
        this.dataManager.setGameMode("Spectate");

        // Send player attributes to Page Manager
        this.communication.sendPlayerAttributes(this.dataManager.getGameMode());

        // Display notification
        this.notification.displayNotification("Loading bot vs bot match...");
    }

    removeFromQueue(playerID) {
        // Handles when a player leaves the queue
        this.queue = this.queue.filter(id => id !== playerID); // Remove the player from the queue
        console.log(`Player ${playerID} removed from queue`);
    }
}

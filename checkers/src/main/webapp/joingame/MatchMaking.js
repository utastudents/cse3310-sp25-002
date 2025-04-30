class Matchmaking {
    constructor(communication, dataManager, notification, playerWaitlist, botWaitlist) {
        this.queue = []; 
        this.dataManager = dataManager; 
        this.communication = communication; 
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

    removeFromQueue(playerID) {
        // Handles when a player leaves the queue
        this.queue = this.queue.filter(id => id !== playerID); // Remove the player from the queue
        console.log(`Player ${playerID} removed from queue`);
    }

    requestPlayerMatch(playerID) {
        // Handles when a player clicks the "Challenge Player" button

        const player = this.dataManager.getPlayer();
        if (!player) {
            this.notification.displayNotification("No player data available", "error");
            return;
        }

        this.playerWaitlist.remove(playerID);
        this.dataManager.setGameMode("Human");
        this.communication.sendPlayerAttributes(this.dataManager.getGameMode());
        this.notification.displayNotification("Finding player match...");
        this.addToQueue(playerID);
    }

    requestBotMatch(playerID) {
        // Handles when a player clicks the "Challenge Bot" button

        const player = this.dataManager.getPlayer();
        if (!player) {
            this.notification.displayNotification("No player data available", "error");
            return;
        }

        this.botWaitlist.remove(playerID);
        this.dataManager.setGameMode("Bot");
        this.communication.sendPlayerAttributes(this.dataManager.getGameMode());
        this.notification.displayNotification("Starting bot match...");
    }

    requestSpectateBotVsBot(playerID) {
        this.dataManager.setGameMode("Spectate");
        this.communication.sendPlayerAttributes(this.dataManager.getGameMode());
        this.notification.displayNotification("Loading bot vs bot match...");
    }


}

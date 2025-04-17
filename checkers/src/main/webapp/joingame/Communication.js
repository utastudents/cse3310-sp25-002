class Communication {
  constructor(dataManager) {
    this.dataManager = dataManager;
    this.setupMessageHandlers();
  }

  setupMessageHandlers() {
    // Listen for messages from Page Manager
    window.addEventListener("message", (event) => {
      // Security check - verify message origin if possible
      // if (event.origin !== "expected-origin") return;
      
      const message = event.data;
      
      // Handle different message types from Page Manager
      if (message.type === "playerData") {
        console.log("Received player data:", message.payload);
        this.handlePlayerData(message.payload);
      } else if (message.type === "waitlistUpdate") {
        console.log("Received waitlist update:", message.payload);
        this.handleWaitlistUpdate(message.payload);
      }
    });
  }

  handlePlayerData(playerData) {
    // Process player data from Page Manager
    this.dataManager.setPlayer(playerData);
    
    // Notify other components if needed
    window.dispatchEvent(new CustomEvent("playerDataUpdated", {
      detail: playerData
    }));
  }

  handleWaitlistUpdate(waitlistData) {
    // Process waitlist update from Page Manager
    window.dispatchEvent(new CustomEvent("waitlistUpdated", {
      detail: waitlistData
    }));
  }

  sendPlayerAttributes(mode) {
    const player = this.dataManager.getPlayer();
    if (!player) {
      console.error("No player data available to send");
      return;
    }

    const message = {
      type: "matchRequest",
      payload: {
        playerID: player.id,
        username: player.username,
        mode: mode
      }
    };

    // Send message to Page Manager
    window.parent.postMessage(message, "*"); // Use specific origin in production
    console.log("Sent match request:", message);
  }
}

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
      } else if (message.type === "join_response") {
        // Handle join_response message type from Page Manager's JoinGameHandler
        console.log("Received join response:", message);
        this.handleJoinResponse(message);
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

  handleJoinResponse(response) {
    // Process join response from Page Manager

    // Display notification with the response message
    window.dispatchEvent(new CustomEvent("joinResponseReceived", {
      detail: response
    }));
  }

  sendPlayerAttributes(mode) {
    const player = this.dataManager.getPlayer();
    if (!player) {
      console.error("No player data available to send");
      return;
    }

    // Format the message to match what the Page Manager expects
    // Based on the PageManager.java code:
    // case "join_game": {
    //    Map<String, String> joinData = new HashMap<>();
    //    joinData.put("ClientID", String.valueOf(U.id));
    //    joinData.put("gameMode", U.msg);
    const message = {
      type: "join_game",  // Must match the case in the switch statement
      id: player.getID(),    // This becomes U.id in the backend
      msg: mode           // This becomes U.msg which is used as gameMode
    };

    console.log("Sending join game request:", message);

    // Send message to Page Manager via WebSocket
    if (window.connection && typeof window.connection.send === 'function') {
      window.connection.send(JSON.stringify(message));
      console.log("Sent join game request via WebSocket:", message);
    } else {
      window.parent.postMessage(message, "*"); // Use specific origin in production
      console.log("Sent join game request via postMessage:", message);
    }
  }

  // Method to cancel join request
  cancelJoinRequest() {
    const player = this.dataManager.getPlayer();
    if (!player) {
      console.error("No player data available to send");
      return;
    }

    // Format the message for cancel request
    // Based on the PageManager.java code:
    // case "cancel": {
    //    System.out.println("Received cancel request from ClientID: " + U.id);
    //    handlePlayerRemoval(U.id);
    const message = {
      type: "cancel",
      id: player.getID()  // This becomes U.id in the backend
    };

    console.log("Sending cancel request:", message);

    // Send message to Page Manager
    if (window.connection && typeof window.connection.send === 'function') {
      window.connection.send(JSON.stringify(message));
      console.log("Sent cancel request via WebSocket:", message);
    } else {
      window.parent.postMessage(message, "*"); // Use specific origin in production
      console.log("Sent cancel request via postMessage:", message);
    }
  }
}

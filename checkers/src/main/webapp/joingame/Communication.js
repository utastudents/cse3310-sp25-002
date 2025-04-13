class Communication {
  /**
   * Sends player attributes to Page Manager
   * @param {Object} playerData - Player information
   * @param {string} playerData.id - Unique player identifier
   * @param {string} playerData.username - Display name (max 20 chars)
   * @param {string} [playerData.status='pending'] - Initial player status
   */

constructor() {
    // https://developer.mozilla.org/en-US/docs/Web/API/WebSocket
    this.socket = new WebSocket("url"); // TODO: Change URL here

    this.socket.addEventListener("open", () => {
      console.log('WebSocket connection established.');
    });

    this.socket.addEventListener("close", () => {
      console.log('WebSocket connection closed.');
    });

    this.socket.addEventListener("message", (event) => {
      console.log('Received message:', event.data);
    });
  }

sendJoinGameRequest(clientID, gameMode) {
    // Create data object matching what Page Manager expects
    const joinData = {
      ClientID: clientID,
      gameMode: gameMode
    };
    
    if (this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(joinData)); // Send as JSON string
      console.log("Join game request sent: " + gameMode + " mode for player " + clientID);
    }
    else {
      console.error("WebSocket connection is not open. Cannot send join game request.")
    }
}
  

  sendPlayerAttributes(playerData) {
    // Validate and forward to Page Manager
  }

  /**
   * Handles matchmaking requests from UI buttons
   * @param {string} playerID - Requesting player's unique ID
   * @param {'PLAYER'|'BOT'|'SPECTATE'} matchType - Match type
   */
  handleMatchRequest(playerID, matchType) {
    // Convert to Page Manager event format
  }

  /**
   * Manages player queue status
   * @param {string} playerID - Player's unique ID
   * @param {'add'|'remove'} operation - Queue action
   */
  handleQueueOperation(playerID, operation) {
    // Track queue state changes
  }

  /**
   * Receives messages from Page Manager
   * @param {Object} message - Message payload
   * @param {string} message.type - Event type (MATCH_FOUND/QUEUE_UPDATE/ERROR)
   * @param {Object} message.data - Event-specific data
   */
  receiveFromPageManager(message) {
    // Route to appropriate handlers
  }

  /**
   * Registers update callback
   * @param {function(Object)} callback - Receives MATCH_READY/QUEUE_STATUS updates
   */
  onUpdate(callback) {
    // Store reference for later notifications
  }

  /**
   * Registers error callback
   * @param {function(Error)} callback - Receives error notifications
   */
  onError(callback) {
    // Store reference for error reporting
  }
}

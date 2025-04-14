class Communication {
  constructor() {
    this.updateCallback = null;
    this.errorCallback = null;
  }

  /**
   * Sends player attributes to Page Manager
   * @param {Object} playerData - Player information
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
    // Validate input
    if (
      !playerData ||
      typeof playerData.id !== 'string' ||
      typeof playerData.username !== 'string' ||
      playerData.username.length > 20
    ) {
      this._reportError(new Error('Invalid player data'));
      return;
    }
    // Simulate sending to Page Manager
    this._simulatePageManagerReceive({
      type: 'PLAYER_JOINED',
      data: { ...playerData }
    });
  }

  /**
   * Receives messages from Page Manager
   * @param {Object} message - Message payload
   */
  receiveFromPageManager(message) {
    if (!message || typeof message.type !== 'string') {
      this._reportError(new Error('Malformed message from Page Manager'));
      return;
    }
    if (message.type === 'PLAYER_JOINED' || message.type === 'UPDATE') {
      if (this.updateCallback) this.updateCallback(message);
    } else if (message.type === 'ERROR') {
      this._reportError(new Error(message.data?.error || 'Unknown error'));
    } else {
      this._reportError(new Error('Unknown message type: ' + message.type));
    }
  }

  /**
   * Registers update callback
   * @param {function(Object)} callback
   */
  onUpdate(callback) {
    this.updateCallback = callback;
  }

  /**
   * Registers error callback
   * @param {function(Error)} callback
   */
  onError(callback) {
    this.errorCallback = callback;
  }

  // Private helpers

  _reportError(err) {
    if (this.errorCallback) this.errorCallback(err);
    else console.error(err);
  }

  // Simulate Page Manager integration for demo/testing
  _simulatePageManagerReceive(message) {
    setTimeout(() => this.receiveFromPageManager(message), 10);
  }
}

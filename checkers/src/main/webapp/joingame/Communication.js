class Communication {
  constructor() {
    this.updateCallback = null;
    this.errorCallback = null;
  }

  /**
   * Sends player attributes to Page Manager
   * @param {Object} playerData - Player information
   */
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

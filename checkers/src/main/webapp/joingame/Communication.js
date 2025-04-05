class Communication {
  constructor() {
    this.pageManagerCallback = null;
    this.updateCallback = null;
  }
  // Send player attributes to Page Manager
  sendPlayerAttributes(playerData) {
    const validated = this.validatePlayerData(playerData);
    
    // Direct method call simulation
    if (this.pageManagerCallback) {
      this.pageManagerCallback({
        type: 'PLAYER_ATTRIBUTES',
        data: validated
      });
    }
  }

  // Receive updates from Page Manager
  receiveFromPageManager(message) {
    switch(message.type) {
      case 'STATUS_UPDATE':
      case 'MATCH_READY':
        this.handleUpdate(message.data);
        break;
      default:
        this.handleError(new Error('Unknown message type'));
    }
  }

  // Set Page Manager communication endpoint
  setPageManagerEndpoint(callback) {
    this.pageManagerCallback = callback;
  }

  // Set update handler
  setUpdateHandler(callback) {
    this.updateCallback = callback;
  }

  handleUpdate(data) {
    if (this.updateCallback) {
      this.updateCallback(data);
    }
  }

  handleError(error) {
    console.error('Communication Error:', error.message);
    // Could add error callback registration similarly
  }
  validatePlayerData(data) {
    if (!data.id || !data.username) {
      throw new Error('Invalid player data - missing required fields');
    }
    return {
      id: data.id,
      username: data.username.substring(0, 20),
      status: data.status || 'pending'
    };
  }
}
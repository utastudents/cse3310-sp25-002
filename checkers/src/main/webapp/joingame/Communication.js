const EventEmitter = require('events');

class Communication {
  constructor() {
    this.channel = new EventEmitter();
    this.setupCoreListeners();
  }

  setupCoreListeners() {
    // Receive updates from Page Manager
    this.channel.on('from_page', (message) => {
      this.handleIncoming(message);
    });
  }

  // Send player attributes to Page Manager
  sendPlayerAttributes(playerData) {
    const validated = this.validatePlayerData(playerData);
    this.channel.emit('to_page', {
      type: 'PLAYER_ATTRIBUTES',
      data: validated
    });
  }

  // Receive updates from Page Manager
  onUpdateFromPageManager(callback) {
    this.channel.on('update', (update) => {
      callback(update);
    });
  }

  // Handle incoming messages
  handleIncoming(message) {
    switch(message.type) {
      case 'STATUS_UPDATE':
        this.channel.emit('update', message.data);
        break;
      case 'MATCH_READY':
        this.channel.emit('update', message.data);
        break;
      default:
        this.channel.emit('error', new Error('Unknown message type'));
    }
  }

  // Validation for player attributes
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
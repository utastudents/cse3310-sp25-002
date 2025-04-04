class CheckersCommunication {
    constructor() {
      this.roomId = null;
   
      // Communication from PageManager
      document.addEventListener('join-game', (e) => {
        this.joinGame(e.detail.roomId);
      });
   
      document.addEventListener('make-move', (e) => {
        this.sendMove(e.detail.from, e.detail.to);
      });
    }
   
    // Method to join a game
    joinGame(roomId) {
      this.roomId = roomId;
   
      // Simulate server response for joining a game
      console.log(`Joining game room: ${roomId}`);
      this.sendToManager('connected', { roomId: this.roomId });
    }
   
    // Send a move
    sendMove(from, to) {
      if (!this.roomId) {
        console.error('Cannot send move. No room joined.');
        return;
      }
   
      const moveData = {
        roomId: this.roomId,
        from,
        to,
      };
   
      // Simulate server response for sending a move
      console.log(`Sending move:`, moveData);
      this.sendToManager('move-sent', { success: true, move: moveData });
    }
   
    // Send messages to the PageManager
    sendToManager(eventType, data) {
      document.dispatchEvent(new CustomEvent(eventType, { detail: data }));
    }
  }
   
  const comm = new CheckersCommunication();
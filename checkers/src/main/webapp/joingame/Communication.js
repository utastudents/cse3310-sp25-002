class CheckersCommunication {
    constructor() {
      //Communication for join the game events from PageManager
      document.addEventListener('join-game', (e) => {
        this.handlePlayerJoin(e.detail.playerInfo);
      });
    }
  
    // Handle player joining the game
    handlePlayerJoin(playerInfo) {
      console.log('Player joining the game:', playerInfo);
  
      // Simulate server response or processing logic
      const joinResponse = {
        success: true,
        message: `Player ${playerInfo.UserName} has joined the game.`,
        playerInfo,
      };
  
      // Notify PageManager that the player has successfully joined
      this.sendToPageManager('player-joined', joinResponse);
    }
  
    // Send messages to PageManager
    sendToPageManager(eventType, data) {
      document.dispatchEvent(new CustomEvent(eventType, { detail: data }));
    }
  }

  const comm = new CheckersCommunication();
  
class JoinGameManager 
{
    constructor() 
    {
      console.log("Initializing Join Game component");
  
      // Initialize components
      this.dataManager = new Data();
      this.notification = new DisplayNotification();
      this.communication = new Communication(this.dataManager);
      this.playerWaitlist = new Waitlist("player-waitlist");
      this.botWaitlist = new Waitlist("bot-waitlist");
      this.matchmaker = new Matchmaking(
        this.communication,
        this.dataManager,
        this.notification,
        this.playerWaitlist,
        this.botWaitlist
      );
  
      // Setup event listeners
      this.setupEventListeners();
  
      // Setup message handlers for page manager communication
      this.setupMessageHandlers();
  
      // Create default bot entries for the bot waitlist
      this.addDefaultBotsToWaitlist();
    }
  
    initialize() 
    {
      console.log("Join Game Manager initialized");
  
      this.createWaitlistElements();
  
      // Display initial notification
      this.notification.displayNotification("Welcome to the Game Lobby!");
  
      // Add self as player to waitlist when clientId is available
      if (window.clientId) 
      {
        this.addSelfToWaitlist();
      } 
      else 
      {
        window.addEventListener('clientIdReceived', () => 
        {
          this.addSelfToWaitlist();
        });
      }
    }
  
    createWaitlistElements() 
    {
      const waitlistContainer = document.querySelector('.waitlist-container');
      if (!waitlistContainer) {
        console.error("Waitlist container not found");
        return;
      }
  
      if (!document.getElementById('player-waitlist')) 
      {
        const playerDiv = document.createElement('div');
        playerDiv.className = 'waitlist';
        playerDiv.innerHTML = `
        <h3>Players Waiting</h3>
        <ul id="player-waitlist"></ul>
        `;
        waitlistContainer.appendChild(playerDiv);
      }
  
      if (!document.getElementById('bot-waitlist')) 
      {
        const botDiv = document.createElement('div');
        botDiv.className = 'waitlist';
        botDiv.innerHTML = `
        <h3>Bots Available</h3>
        <ul id="bot-waitlist"></ul>
        `;
        waitlistContainer.appendChild(botDiv);
      }
    }

    addSelfToWaitlist() 
    {
      if (window.clientId) 
      {
        // Use username if available from login, otherwise use default name
        const username = window.playerUsername || `Player ${window.clientId}`;
  
        const defaultPlayer = 
        {
          id: window.clientId,
          username: username
        };
  
        // Set the player in data manager
        this.dataManager.setPlayer(defaultPlayer);
  
        // Add to waitlist
        this.playerWaitlist.add(defaultPlayer);
  
        console.log("Added current player to waitlist:", defaultPlayer);
      } 
      else 
      {
        console.log("No client ID available, can't add player to waitlist");
      }
    }
  
    addDefaultBotsToWaitlist() 
    {
      const bots = 
      [
        { id: 'bot1', username: 'Easy Bot' },
        { id: 'bot2', username: 'Difficult Bot' },
      ];
  
      bots.forEach(bot => 
      {
        this.botWaitlist.add(bot);
      });
  
      console.log("Added default bots to waitlist");
    }
  
    setupEventListeners() 
    {
      // Add button click handlers
      const playVsPlayerBtn = document.getElementById('play-vs-player');
      const playVsBotBtn = document.getElementById('play-vs-bot');
      const spectateBotsBtn = document.getElementById('spectate-bots');
  
      if (playVsPlayerBtn) 
      {
        playVsPlayerBtn.addEventListener('click', () => this.handlePlayVsPlayer());
      }
  
      if (playVsBotBtn) 
      {
        playVsBotBtn.addEventListener('click', () => this.handlePlayVsBot());
      }
  
      if (spectateBotsBtn) 
    {
        spectateBotsBtn.addEventListener('click', () => this.handleSpectateBots());
      }
    }
  
    setupMessageHandlers() 
    {
      // Listen for custom events from Communication class
      window.addEventListener('playerDataUpdated', (event) => 
      {
        const playerData = event.detail;
        console.log('Player data updated:', playerData);
  
        // Add player to waitlist when data is received
        if (playerData && playerData.id && playerData.username) 
        {
          this.playerWaitlist.add(playerData);
        }
      });
  
      window.addEventListener('waitlistUpdated', (event) => 
      {
        const waitlistData = event.detail;
        console.log('Waitlist updated:', waitlistData);
  
        // Update waitlists based on data received
        if (waitlistData && waitlistData.players) 
        {
          // Clear existing waitlist to avoid duplicates
          this.playerWaitlist.waitlist = [];
          waitlistData.players.forEach(player => 
          {
            this.playerWaitlist.add(player);
          });
          this.playerWaitlist.displayWaitlist();
        }
  
        if (waitlistData && waitlistData.bots) 
        {
          // Clear existing bot waitlist to avoid duplicates
          this.botWaitlist.waitlist = [];
          waitlistData.bots.forEach(bot => 
          {
            this.botWaitlist.add(bot);
          });
          this.botWaitlist.displayWaitlist();
        }
      });
  
      // Listen for join response events
      window.addEventListener('joinResponseReceived', (event) => 
      {
        const response = event.detail;
        console.log('Join response received:', response);
  
        // Handle the join response
        if (response.type === 'join_response') 
        {
          // Display the message from the Page Manager
          this.notification.displayNotification(response.msg);
  
          // Update the game state based on the response
          if (response.isBot) 
          {
            // Bot match confirmed
            console.log('Bot match confirmed');
            // Remove player from waitlist
            const player = this.dataManager.getPlayer();
            if (player) 
            {
              this.playerWaitlist.remove(player.getID());
            }
          } 
          else 
          {
            // Waiting for another player or error
            console.log('Received response:', response.msg);
  
            // If there was an error, show notification
            if (response.msg && response.msg.includes("error")) {
              this.notification.displayNotification(response.msg, "error");
            }
          }
        }
      });
    }
  
    // Button event handlers
    handlePlayVsPlayer() 
    {
      console.log('User clicked: Play vs Player');
  
      // Ensure player data exists before proceeding
      if (!this.ensurePlayerData()) 
      {
        return;
      }
  
      this.dataManager.setGameMode("Human");
      const playerId = this.dataManager.getPlayer().getID();
      this.matchmaker.requestPlayerMatch(playerId);
      this.notification.displayNotification("Finding player match...");
    }
  
    handlePlayVsBot() 
    {
      console.log('User clicked: Play vs Bot');
  
      // Ensure player data exists before proceeding
      if (!this.ensurePlayerData()) 
      {
        return;
      }
  
      this.dataManager.setGameMode("Bot");
      const playerId = this.dataManager.getPlayer().getID();
      this.matchmaker.requestBotMatch(playerId);
      this.notification.displayNotification("Starting bot match...");
    }
  
    handleSpectateBots() 
    {
      console.log('User clicked: Spectate Bot vs Bot');
  
      // Ensure player data exists before proceeding
      if (!this.ensurePlayerData()) 
      {
        return;
      }
  
      this.dataManager.setGameMode("Spectate");
      const playerId = this.dataManager.getPlayer().getID();
      this.matchmaker.requestSpectateBotVsBot(playerId);
      this.notification.displayNotification("Loading bot vs bot match...");
    }
  
    // Helper method to ensure player data exists before proceeding
    ensurePlayerData() 
    {
      const player = this.dataManager.getPlayer();
  
      if (!player) 
     {
        if (window.clientId) 
        {
          const username = window.playerUsername || `Player ${window.clientId}`;
  
          this.dataManager.setPlayer({
            id: window.clientId,
            username: username
          });

          return true;
        } 
        else 
        {
          this.notification.displayNotification("No player data available. Please log in first.", "error");
          return false;
        }
      }
  
      return true;
    }
  }
  
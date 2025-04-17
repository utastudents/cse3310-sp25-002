// Data handling for Join Game component

class Player
{
    constructor(username, id) 
    {
        this.username = username;
        this.id = id;
        this.waitlistStatus = false; 
    }
    // Accessing the player's ID
    getID() // No parameters needed
    {
        return this.id;
    }

    // Accessing the player's username
    getUsername() // No parameters needed
    {
        return this.username;
    }
}

// Game mode selection handling
class GameOptions
{
    // Initially set both game (bot and standard) options to null
    constructor()
    {
        this.gameMode = null; // Using gameMode to match Page Manager's convention
    }

    // Set the game mode to either "Bot","Human", "Spectate"
    // Parameter is a string: "Bot", "Human", "Spectate"
    setMode(type)
    {
        this.gameMode = type; 
    }

    // Get the current game mode
    // Returns "Bot", "Human", "Spectate", or null if not selected
    getMode()
    {
        return this.gameMode;
    }
} 

class Data {
    constructor() {
        this.player = null;
        this.gameOptions = new GameOptions();
    }

    setPlayer(playerInfo) {
        this.player = new Player(playerInfo.username, playerInfo.id);
    }

    getPlayer() {
        return this.player;
    }

    setGameMode(mode) {
        this.gameOptions.setMode(mode);
    }

    getGameMode() {
        return this.gameOptions.getMode();
    }
}
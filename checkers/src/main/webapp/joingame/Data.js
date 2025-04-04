// Data handling for Join Game component

// Players will have the following data:
// ID, Username, flag to indicate waitlist status, timer indicating time spent on waitlist
// Has methods to obtain player information
class Player
{
    // Create a player object using (at least) the username (string) and ID
    // A timer will be initialized starting with the time the player was added
    // The waitlist status will be initially set to false as the user is not in the waitlist
    // Additional parameters may be used to build a player as listed below:
    // This was discussed with Page Mgr and will be confirmed in the implementation stage.
    // Username and ID are required; the additional parameter will not affect any functionality in this component

    /*
    {
    "status": {
        "turn": 1,
        "score": 100
    },
    "recipients": [4],
    "playerInfo": {
        "id": 4,
        "UserName": "Alice",
        "win": 5,
        "lose": 2,
        "TotalGames": 7,
        "rank": "Gold"
    }
    }
    */
    constructor(username, id) 
    {
        this.username = username;
        this.id = id;
        this.waitlistStatus = false; 
        this.timer = Date.now(); 
    }

    // Needs methods to access player information
    // Accessing the player's ID
    getID() // No parameters needed
    {
        return id;
    }

    // Accessing the player's username
    getUsername() // No parameters needed
    {
        return username;
    }
}

// This class includes the possible game options: against a bot or a player
// Has methods to set the game mode and then obtain the mode for communication
class GameOptions
{
    // Initially set both game (bot and standard) options to null
    constructor()
    {
        this.opponentType = null;
    }

    // Method sets the game mode to either bot or standard mode
    // Parameter is a boolean representing the type
    // If true, bot mode is set
    // If false, standard mode is set 
    setMode(type)
    {
        this.opponentType = type; 
    }

    // Get the current opponent type selection
    // No parameters
    // Returns a string such as "human", "bot1", "bot2", or null if not selected
    getOpponentType()
    {
        return this.opponentType;
    }
}


// Data structures for Join Game component

// Players will have the following data:
// ID, Username, flag to indicate waitlist status, timer indicating time spent on waitlist
class Player
{
    constructor(username, id) // Users will have an assigned ID and chosen username
    {
        this.username = username;
        this.id = id;
        this.waitlistStatus = false; 
        this.timer = timer;
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


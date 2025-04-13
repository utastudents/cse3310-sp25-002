class Waitlist{
	constructor(){
        this.waitlist = []; // Array to store player objects
    }

	
	add(playerInfo){	//Add a player's info onto the waitlist
		player = new Player(playerInfo.username, playerInfo.id); // Create a new Player object with given data
		this.waitlist.push(player); // Add the Player object to the waitlist
		this.displayWaitlist(); // Update/display the waitlist again with the new Player
		return player; 
	}
	
	// Changed parameter here to playerID
	remove(playerID){	//Remove a player's info from the waitlist when leaving the game
		// Filter the waitlist with the players that do not have this ID
		this.waitlist = this.waitlist.filter(player => player.getID() !== playerId);
		this.displayWaitlist(); // Update/display the waitlist again with the new Player
	}
	
	updateWaitlist(playerInfo){	//Update waitlist and placement's of players each time another player is added/removed
		this.playerInfo = new playerInfo;
	}

	displayWaitlist(){	//Displays the updated waitlist

	}
}
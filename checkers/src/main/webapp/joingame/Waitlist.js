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
	
	remove(playerInfo){	//Remove a player's info from the waitlist when leaving the game
		
	}
	
	updateWaitlist(playerInfo){	//Update waitlist and placement's of players each time another player is added/removed
		this.playerInfo = new playerInfo;
	}

	displayWaitlist(){	//Displays the updated waitlist

	}
}
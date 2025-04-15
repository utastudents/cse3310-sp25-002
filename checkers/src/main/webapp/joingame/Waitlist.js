class Waitlist{
	constructor(){
        this.waitlist = []; // Array to store player objects
		this.data = new Data;
    }
	
	add(playerInfo){	//Add a player's info onto the waitlist
		player = new Player(playerInfo.username, playerInfo.id); // Create a new Player object with given data
		player.waitlistStatus = true; // Update the flag to true to indicate player is on the waitlist
		this.waitlist.push(player); // Add the Player object to the waitlist
		this.displayWaitlist(); // Update/display the waitlist again with the new Player
		return player; 
	}
	
	// Changed parameter here to playerID
	remove(playerID){	//Remove a player's info from the waitlist when leaving the game
		  // Find the player before removing them
		  const player = this.waitlist.find(p => p.getID() === playerID);
    
		  // Check if the player is valid first, then update the waitlist status flag
		  if (player){
			  player.waitlistStatus = false; // Set waitlist status to false
		  }
		  
		  this.waitlist = this.waitlist.filter(p => p.getID() !== playerID); // Filter out the player with the matching ID
		  this.displayWaitlist();  // Update the display
	}

	displayWaitlist(){	//Displays the updated waitlist
		const list = document.getElementById(waitlist);

		HTMLDataListElement.innerHTML = "";

		this.waitlist.forEach(player => {
			const li = document.createElement("li");
			li.textContent = '${player.getUsername()}	${player.getID()}';
			HTMLDataListElement.appendChild(li);
		});
	}
}
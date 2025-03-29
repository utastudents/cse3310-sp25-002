# Project description: 

	The Login Page is the initial user interface that allows players 	
	to enter the game system by providing a valid username.  

 
## Enter Username/Join Game:

	At the center of the interface is a single input field labeled “Enter Username”, 
	accompanied by a “Join Game” button. When the user clicks the button without entering 
	a username, an alert is triggered, displaying the message: “Please enter a username.” 
	This ensures basic input validation on the client side. 

 
### Interfacing to Page Manager:

	After entering a username and clicking “Join Game,” the system packages the input into a 
	JSON object and sends it to the Page Manager (Project 29). The Page Manager is responsible 
	for generating a unique client ID associated with the username. This ID is then used to 
	track the user session throughout the game. 

 
#### Cross-Project Discussions:

	Based on team and cross-project discussions, we decided to: 

      •     Exclude password and email fields to reduce complexity and backend load 

      •     Rely solely on the username for login and identity tracking 

      •     Coordinate with the Database team (Project 30) to ensure the username and 	
			client ID are stored for game session tracking 

 

 
##### Conclusion:

	This interface plays a crucial role in initiating the user experience and interfacing 	
	with backend components without introducing unnecessary dependencies or burdens on 
	other subsystems. 

###### Mockup

![interface1](https://github.com/utastudents/cse3310-sp25-002/blob/fd4c29f25d2bed41bc68c1e8d1465dbfe98b7217/INTERFACES/LoginPage_Mockup/interface1.png)

![interface2](https://github.com/utastudents/cse3310-sp25-002/blob/fd4c29f25d2bed41bc68c1e8d1465dbfe98b7217/INTERFACES/LoginPage_Mockup/interface2.png)
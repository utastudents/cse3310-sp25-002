 # Page Manager Interface Description

 ## Overview 
The Page Manager will receive JSON inputs where it will convert them into Java objects
for backend processing, and direct the data to the relevant subsystems. This 
ensures seamless communication between the frontend and backend.

## Data Exchange
### New Account/Login  
  - NewAccount/Login will send Page Manager input of the usernames as a JSON string. 
  Page Manage will verify if a username already exists before adding it to the Database. 
  - Page Manager will send back a message if the username already exists. 
  This will ensure no duplicate usernames while providing immediate feedback to users during registration.
### Join Game  
  - Join Game will send Page Manager the event when clicking the join game button. 
  This includes what type of game the user wants to join and game preferences such as playing against a human or bot(multiplayer mode or bot mode). 
  - When multiplayer is selected page manager will send an availability flag database and join game 
### Game Display 
  - Page Manager will send Game Display updates of the board in real time based on player or bot movement, Usernames for both players, ClientID, GameID, Game manager’s gamestate object . 
  Which involve game start(board display),game over, and commands for navigation. 
  - Game Display will send Page Manager Requested move from the current player, resignation, and draw offer signal.
### Summary
  - Page manager will send Summary the users username, client ID and Score. 
### DataBase  
  Database will Store Username, client ID that we send. We will get score, game ID, and client ID back.
### Pair up 
  - Page manager will be sending Pair up information on what mode the user selects, either bot or multiplayer(from game display). 
  - Pair up will send us the list of the available clients to display to the player and if a valid selection was made.
### Game Manager  
  - The Game Manager will interact with bots.
  - The Page Manager will send the Game Manager the user's client ID, the events of when players move, and where they move to. 
  - If errors occur, the Game Manager will validate moves, update the game state, and send back to the Page Manager the current board status and game ID, which the Page Manager will then send to the Game Display. 
  -  If players move illegally or a user cannot connect, the Game Manager will generate error messages that the Page Manager will send to the Game Display.

# BOT I : Interface 

## BOT I : Interface Description
The BOT I will communicate with the Game Manager in primarily two ways 
1. Receive the Game State : Game State includes the objects like BOARD, PLAYERS, and MOVES
2. Send the moves of the BOT I back to Game Manager for verification or continue to the game.

### Methods used by the BOT I to interact with Game Manager 
1. requestMove( Game game ) 
    * The Game Manager calls this method to send the current game state to BOT I.
    * Parameter( Game game ) : An object representing the current state of the game, including the board, players, and move history.
    * BOT I processes the received game state and decides its next moves based on the given information.

2. sendMove ()
    * This method is used by BOT I to send its decided moves back to the Game Manager.
    * Since the Game Manager group is using a Linked List for storing moves, including chained captures if applicable.
    * This method returns a Linked List of Moves containing one or more move objects representing the actions BOT I wants to take.

### Description of data structures
* Game game : Game State includes the objects like BOARD, PLAYERS, and MOVES
* Move move : the move objects contain the decided moves of BOT I based on the strategy and Game state recieved.
* The Move of BOT I will be stored in the Linked List used by Game Manager to store the Moves

### Notes
* BOT I interacts with the Game Manager. It gets the game state, processes it, and sends back valid moves. 
* BOT I does not maintain its own game state; it relies on the Game Manager for up-to-date information.







    



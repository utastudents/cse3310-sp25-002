# GameDisplay Interface

The Game Display webpackage will use HTML components to communicate mainly with the Page Manager. The game display will produce a game board that shows the current state of the player moves and their inputs/outputs.

# Data Received:
The Game Display Module receives the following information from the page manager:

- Game Termination Status: Provided by the Game Termination Module, indicating whether the game has ended.
- Player Information: Retrieved from the Pair-Up module, including details of the players in the match; their unique ID etc.
- Game ID: Obtained from the Game Manager module to identify the current game session.
- Current Game State:
    - The positions of all the pieces on the board
    - The Active Player's turn
    - List of valid moves (based on the selected pieces and its position)
- Movement:
    - Confirms if the move is valid and accepted by the backend
    - Triggers a pop-up/UI message if the move is invalid

# Data Sent to Page Manager

The Game Display Module communicates the following actions to the Page Manager:

- Player Move Request: The selected move from the active player
- Resignation Signal: A notification that the current player has chosen to resign.
- Draw Offer Signal: A request from the player to propose a draw. 

# Side information

- Game Display only handles the rendering and user input captures of the player movements.
- The final game display will be revised during the design stages.
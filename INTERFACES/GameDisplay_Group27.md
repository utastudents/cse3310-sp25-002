# GameDisplay Interface

The Game Display webpackage will use HTML components to communicate mainly with the Page Manager. The game display will produce a game board that shows the current state of the player moves and their inputs/outputs.

# Data Received:
The Game Display Module receives the following information from the page manager:

- Game Termination Status: Provided by the Game Termination Module, indicating whether the game has ended.
- Player Information: Retrieved from the Pair-Up module, including details of the players in the match.
- Game ID: Obtained from the Game Manager module to identify the current game session.
- Current Game State:
    - The positions of all piece on the board
    - The Active Player's turn

# Data Sent to Page Manager

The Game Display Module communicates the following actions to the Page Manager:

- Player Move Request: The selected move from the active player
- Resignation Signal: A notification that the current player has chosen to resign.
- Draw Offer Signal: A request from the player to propose a draw. 
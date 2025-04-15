# GameDisplay Interface

The Game Display webpackage will use websocket components to communicate mainly with the Page Manager. The game display will produce a game board that shows the current state of the player moves and their inputs/outputs.

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
- Player Actions:
    - Notifies if a player resigns
    - Notifies is a player offers or accepts a draw
    - Update to indicate whose turn is next
- Basic Notifications:
    - Any end-of-game messsages, errors, or system alerts that are meant for players
        - E.g., Game over, Draw, Conneection Lost, etc.

# Data Sent to Page Manager

The Game Display Module communicates the following actions to the Page Manager:

- Player Move Request: The selected move from the active player
- Resignation Signal: A notification that the current player has chosen to resign.
- Draw Offer Signal: A request from the player to propose a draw. 
- Draw Signal: If a player accepts a draw, this is sent to back end to finalize the result
- Valid Move Request: Request asking for all legal moves that can be made from that position

# Updated Functionality

- Blocking moves during oponenet's turn // Prevents interaction unless it's the player's turn
- The board is roated 180 degrees for black pieces to provide correct perspective
- The board visually updates when a move is made by the opponent or a bot

# Side information

- Game Display only handles the rendering and user input captures of the player movements.
- All communication will be done through WebSocket and formatted as JSON.
- Game Display will wait for specific messages and wil respond back according by updating the UI or sending information (show_game_display, resign, draw_offer, etc.).
- The final game display will be revised during the design stages.
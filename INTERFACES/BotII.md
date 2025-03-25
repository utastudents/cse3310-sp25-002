# BotII Interface

## BotII Interface Description

BotII will primarily interface with the game manager through these 2 function calls:

// 1. void getBotIIMove(GameState gameState)
    - This function will be called by the game manager to send the current game state to BotII.
    - The game state will include information about the game board, and any other relevant information.

// 2. void send_move(GameState gameState, Move move)
    - This function will be returned by BotII within the getBotIIMove function to send its move to the game manager along with updated game board.
    - The move will include the action that BotII wants to take in the current game state.
    - The game manager will then update the game state is updated accordingly.

Notes: 
- This interface is designed to be simple and efficient, allowing BotII to receive the game state and send its move in a single function call. 
- The game manager will handle the details of updating the game state and notifying the other players.
- Additional functions may be added in the future to support more complex interactions between BotII and the game manager, but this basic interface should be sufficient for the initial implementation.
- This is our (group 34's) idea for how BotII will interface with the game manager. We are open to suggestions from game manager and changes to this interface as needed.
- May have to talk to Group 33 about how they will interface with the game manager, to ensure it is uniform between both bots.
- Class names are not definite, just idea, will change as we talk to game manager and finalize our interface design
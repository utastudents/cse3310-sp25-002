# BotII Interface

## BotII Interface Description

BotII will primarily interface with the game manager through these 2 function calls:

1. void requestMove(Game game)
    - This function will be called by the game manager to send the current game state to BotII.
    - The game state will include information about the game board, and any other relevant information.

2. void sendMove()
    - This function will be returned by BotII within the requestMove function to send its move(s) to the game manager.
    - It will return a LinkedList of moves, which will be processed by the game manager.
    - The game manager will then ensure the move(s) are valid with Game Play Rules and update the game state accordingly.
    - The LinkedList will contain the moves that BotII has decided to make based on the game state it received.
    - The moves will be represented as a list of objects, each containing the necessary information to execute the move in the game.

Notes: 
- This interface is designed to be simple and efficient, allowing BotII to receive the game state and send its move(s) in a single function call. 
- The game manager will handle the details of updating the game state (checking with game play) and notifying the other players.
- Additional functions may be added in the future to support more complex interactions between BotII and the game manager, but this basic interface should be sufficient for the initial implementation.
- After talking with the game manager, they suggested that the BotII should be able to send multiple moves at once meaning a LinkedList of moves.
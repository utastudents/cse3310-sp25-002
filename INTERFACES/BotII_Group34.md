# BotII -> Game Manager Interface

## Overview
BotII (as of right now) only interfaces with the game manager.

## Interface Description

BotII will primarily interface with the game manager through these 2 function calls:

1. ```LinkedList<Move> requestMove(Game game)```
    - Public function that will be called by the game manager to send the current game state to BotII.
    - The game state will include information about the game board, and any other relevant information.

2. ``` LinkedList<Move> sendMoves()```
    - Private function that will be returned by BotII within the ```requestMove``` function to send its move(s) to the game manager.
    - It will return a ```LinkedList``` of moves, which will be processed by the game manager.
    - The ```LinkedList``` will contain the moves that BotII has decided to make based on the game state it received.
    - The moves will be represented as a list of ```Move``` Objects, each containing the necessary information to execute the move in the game.

3. Description of objects:

    - Game: This object will represent the current state of the gameand any other relevant data.
    - Move: This object will represent a single move that BotII wants to make.
    - ```LinkedList``` (JCF): This object will be used to store the list of moves that BotII wants to make. It will allow for easy addition and removal of moves, as well as iteration through the list of moves.

Notes: 
- This interface is designed to be simple and efficient, allowing BotII to receive the game state and send its move(s) in a single function call. 
- The game manager will handle the details of updating the game state (checking with game play) and notifying the other players.
- Additional functions may be added in the future to support more complex interactions between BotII and the game manager, but this basic interface should be sufficient for the initial implementation.
- After talking with the game manager, they suggested that the BotII should be able to send multiple moves at once meaning a LinkedList of moves and Group34 concurs.
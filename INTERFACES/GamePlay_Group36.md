# Game Play Interface

## 1.) Gameplay <-> Game Manager

**Purpose:** 
Receive the board and moves to determine whether the move was legal. Also serve as the go between to Termination to check if the game is over which we send back to Game Manager.
**Data Received:**
- Original game board.
- Updated game board.
- Move steps such that if there was more than one jump, all of them are listed in the order they were taken.

**Data sent:**
- Send updated board.
- Send a Boolean for if the move was legal or not.
- Send a Boolean for if the game is over or not.

**Method:**
- Send and receive all data through a JSON file.

## 2.) Gameplay <-> Termination

**Purpose:**
After checking if a move is legal, ask Termination if the resulting board is a draw, a win for red or a win for black.
**Data received:**
- Receive a Boolean true if the game is over or false if the game is not over.

**Data sent:**
- Send the updated board to determine if the game is over.

**Method:**
- Send and receive all data through a JSON file.

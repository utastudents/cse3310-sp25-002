# Game Termination Interface

## Subsystem Interactions

### 1. Gameplay
**Interaction Flow:**
1. After every valid move made by the user (managed by Gameplay), the valid move is sent to Game Termination for evaluation
2. When a valid move results in piece capture, Game Termination counts pieces to determine if game should end
3. When no legal moves remain, Gameplay sends "0 valid moves" signal to trigger termination

**Key Functions:**
- `void processValidMove()`
- `void updatePieceCount()`
- `void checkForLegalMoves()`

### 2. Page Manager (Bidirectional)
**Interaction Flow:**
1. Game Termination notifies Page Manager when termination is detected (win/lose/draw page display)
2. Sends winner name/information to Page Manager for display
3. Receives player offline duration from Page Manager for timeout determination
4. Processes restart requests from Page Manager
5. Sends continuation signal when game is not in terminal state

**Key Functions:**
- `void notifyGameEnd()`
- `void handlePlayerTimeout()`
- `void processRestartRequest()`
- `void sendGameContinuationSignal()`

### 3. SQLite Database
**Interaction Flow:**
1. Game Termination retrieves updated board configuration after each valid move
2. Uses board state to check for termination conditions

## Diagram Changes Required
1. Add bidirectional connection between Game Termination and Page Manager

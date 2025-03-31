## Pair Up Interface Description

### Functionality
The Pair Up subsystem is responsible for matching players who are waiting to start a game. It ensures that players are grouped based on predefined criteria, including whether they want to play against a human opponent or a bot.

### Data Received
#### From "Page Manager" Subsystem (JSON format)
- **List of players waiting for a game**
- Each player entry includes:
  - `playerId` (String) – Unique identifier for the player
  - `playerName` (String) – Unique value
  - `playAgainstBot` (Boolean) – Indicates if the player prefers to play against a bot
  - `timestamp` (DateTime) – Time when the player joined the queue

#### From "DB" Subsystem (JSON format)
- `listOfPlayerActive` (list) – A list of Active players

### Pairing Logic
- If a player prefers a human match, the system searches for another available player.
- If no human is available or the player selected a bot match, the system assigns **Bot I** or **Bot II**.
- **Match Finalization**: Once a pair (human-human or human-bot) is formed, they are removed from the queue and prepared for game initiation.

### Data Transferred/Sent
#### To "Page Manager" Subsystem (JSON format)
- **List of Active players**

#### To "Game Manager" Subsystem (JSON format)
- **List of paired players or player-bot matches ready to start**
- Each match includes:
  - `gameId` (String) – Unique identifier for the match
  - `player1Id` (String) – First player's ID
  - `player2Id` (String) or `botId` – ID of opponent (human or bot)
  - `player1Name` (String) – Player username
  - `player2Name` (String) – or `botName` (Bot I or Bot II)
  - `isBotGame` (Boolean) – Indicates if the match involves a bot
  - `timestamp` (DateTime) – Time when the match was created

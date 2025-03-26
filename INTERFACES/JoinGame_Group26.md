# <u>**Join Game Interface Description**</u>

Primary Interface: The primary interface is between Join Game and Page Mgr. The Join Game html component will display all players that are waiting to play a game and possibly other options including available games and options to play against a bot. It will also accept user input to select or join games.

Recommended Change to Block Diagram: The current block diagram shows a unidirectional connection from Join Game to Page Mgr. This should be a bidirectional connection as Page Mgr must send the Join Game component updated information about available players/games to play. 

Data Exchange:
-	All communication uses JSON for data exchange. Page Mgr manages conversion between JSON from Join Game and the other Java classes.
-	Data to be sent to Page Mgr from Join Game include:
    
    - User game selection (which game/player user wants to join)

    - Game preferences (such as playing against a human or a bot)
-	User data inputs on the Join Game component could be in the form of click events (such as pressing on a listed option), string inputs (typing in an input field), etc. which will all be captured as JSON objects.
-	Join Game receives data from Page Mgr including:

    - List of players waiting for games

    - Available game types/options

    - Status updates on player and game availability

Direct Interface Functions:
-   Send data from Join Game to Page Mgr
-   Received data from Page Mgr to Join Game.

Communication Mechanisms: The system is event-driven and will use web sockets.

Notes:
-	Further details regarding specific elements, form structure, and exact data fields will be determined during the design phase.
-   Function types and parameters will also be specified during the design phase.
-	Additional interfaces may be implemented such as an intermediary interface between four html components to facilitate real-time communication between these html components. However, this can be handled through Page Mgr and is likely not needed. Any other possible interfaces are also not needed and would add unnecessary complexity.


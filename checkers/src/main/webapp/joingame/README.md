# Join Game Design

This document outlines the design of the Join Game component, including class structures, methods, and data flow. It specifies core functionality without implementation details, covering methods, arguments, and return values as needed. Full implementation will occur in the next phase.

## Design Overview
The Join Game component consists of five classes, each responsible for handling a specific aspect of the game joining process. This modular structure improves code organization and maintainability while enabling parallel development among team members. While the structure may be refined during the implementation phase, the core components will remain similar.


Five Classes:
1) Data: Manages core data within this component, including player data and game options. Other classes interact with this class to access relevant data.
    
    Player Information: ID, username, waitlist status, waitlst timer.
    - Constructor: builds a player instance
    - Methods: gets for each player attribute to provide to Page Manager if needed

    Note: Additional parameters to the Player constructor will likely be added during the implementation phase based on the implementation of other components in the system.

    Game Options: Bot or Standard mode
    - Methods: Set and get methods for the game mode.
        

2) Matchmaking: Provides methods to facilitate match requests between players or against bots. Also responsible for communicating these details to the Page Manager.


3) Communication: Handles data transmission between the Join Game component and the Page Manager, including sending player attributes and receiving updates.


4) Waitlist: Manages the live waitlist of players waiting to join a game. Includes methods for adding and removing players from the list..


5) Display Notifications: Handles notifications related to the Join Game component, including displaying alerts or messages to the player during the matchmaking process.


## User Interface
The user interface for the Join Game component will be provided through an HTML file. This file will include elements for displaying player information, game options, and interactive buttons to facilitate joining and matchmaking.

## Notes

Requirements were revised and refined during the design phase as the component's structure became clearer.

Further adjustments may be made during implementation as new requirements or improvements are identified.
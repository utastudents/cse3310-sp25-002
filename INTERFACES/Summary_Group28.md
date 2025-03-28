
Data retrieving: Communicate with Page Manager to retrieve the data from Database. The data will include:
- Player names
- Wins
- Loses
- Time played

Data conversion: The data retrieved by Page Manager will be converted using JSON to be used for Summary Page.

Display Leaderboard: Using the infomation retrieved from the database, display it on the screen:

    The Summary Page will have:
    - Title: Leaderboard
    - Search bar: User can look up a player by searching their name
    - Current stat: Display the user's current status:
        - Name
        - Rank
        - Wins
        - Loses
        - Time played
    - Learderboard: Display player based on their ranking, best player will be in the top, the 2nd best will be second and so on.

Data sending: There will be no data being sent to Page Manager or any other subsection of the game.
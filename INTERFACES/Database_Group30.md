# Database Interface Description


## Database -> Page Manager -> New Account/Login
    Database sends account information back to New Account/Login interface if the account exists. 
    Otherwise, if account does not exist we create 
    an entry in the database to store the new account.

## Database -> Page Manager -> Summary
    Database sends the updated leaderboard information once a game ends to the Page Manager as a JSON file.

## Database -> Pair Up
    Database will return player information back to Pair Up if the 2 players are available.

## Database -> Game Termination
    Database will be updated when Game Termination signals that a game has ended.
    Items updated will be: Rank, Win Percentage, and Games Played


Further details regarding specific variables stored in database will be determined during the design phase.

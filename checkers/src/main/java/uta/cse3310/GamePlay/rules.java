<<<<<<< HEAD
<!DOCTYPE html>
<html lang="en">
<head>
<style>
.hidden {
display: none;
=======
package uta.cse3310.GamePlay;

import java.util.ArrayList;
import java.util.LinkedList;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;

//class rules checks if the move is legal
public class rules
{   
    //checks if the move is within the bounds of the board
    //8x8 board
    //assumption: program starts counting at 0
    //assumption: gameManager waits until current move is processed
    //and then processes the next move
    static protected boolean inBounds(Moves moves)
    {
        int index = moves.size() - 1;
        Square square = moves.getDest(index);

        //assuming program starts counting at 0
        if (square.getCol() > 7 || square.getCol() < 0 ||
            square.getRow() > 7 || square.getRow() < 0)
            return false;
        return true;
    }

    //checks if the piece moves diagonally up-right and up-left
    //for king: moves diagonally down-right and down-left in addition to above
    static protected boolean isDiagonal(LinkedList<Moves> moves, Board board)
    {
        /*if(isKing = true)
        {
            allow the king to move forward/backwards along the diagonal
        }
        else{
            the piece should not be allowed to move down-left or down-right
        }*/
        return false; //Default
    }


    //checks if the square being moved to is occupied by a piece
    static protected boolean occupied(LinkedList<Moves> moves, Board board) {
        // Safety check: make sure the moves list isn't empty
        if (moves == null || moves.isEmpty()) {
            return false;
        }
    
        // Get the latest Moves object (a group of individual Move steps)
        Moves lastMoves = moves.getLast();
    
        // Get the index of the last move within that Moves group
        int lastIdx = lastMoves.size() - 1;
    
        // Extra safety: make sure there's at least one move in the group
        if (lastIdx < 0) {
            return false;
        }
    
        // Get the destination square of the last move
        Square dest = lastMoves.getDest(lastIdx);
    
        // Get the row and column of that square
        int row = dest.getRow();
        int col = dest.getCol();
    
        // Use the board to check if something exists at that location
        Square boardSquare = board.getSquare(row, col);
    
        // If the board square is not null, it's occupied
        return boardSquare != null;
    }

    //checks how many spots moved up to compared to number of pieces
    //0 pieces = 1, 1 piece = 2, 2 pieces = 4 etc...
    static protected boolean pieceToMoves(Moves moves, Board board)
    {
        int index = moves.size() - 1;
        Square squareDest = moves.getDest(index);
        Square squareStart = moves.getStart(index);

        int dist = Math.abs(squareDest.getRow() - squareStart.getRow());

        //if negative, piece moved left, if positive, piece moved right
        int xDirection = squareDest.getCol() - squareStart.getCol();
        //if negative, piece moved down, if positive, piece moved up
        int yDirection = squareDest.getRow() - squareStart.getRow();
        
        if (dist == 1)
            return true;

        int x = 0, y = 0;
        int numPieces = 0;
        //find pieces on the way to the destination
        for (int i = dist; i > 0; --i) 
        {
            if (xDirection > 0)
                ++x;
            else if (xDirection < 0)
                --x;
            /*
            //for exception handling
            else
                throw new RuntimeException("piece did not move\n");
            */

            if (yDirection > 0)
                ++y;
            else if (yDirection < 0)
                --y;
            /*
            //for exception handling
            else
                throw new RuntimeException("piece did not move\n");
            */

            Square currSquare = board.getSquare(squareStart.getCol() + x, squareStart.getRow() + y);
            if (currSquare.hasPiece())
                ++numPieces;

        }
        
        //if 0 pieces != 1 dist, 1 piece != 2 dist, 2 pieces != 4 dist etc...
        //return false
        if (dist / numPieces != 2)
            return false;

        return true;
    }

    //will call occupied to check if a square is occupied by another piece, then check 
    //if the user can check that piece
    static protected boolean canCapture(LinkedList<Moves> moves, Board board)
    {
     

        //call occupied, check if the square is occupied
        //if the space is occupied and the following square is free you can capture that piece and move to the next free space
        //return a map showing were the player can move

        return false;//default
    }

    //Check to see if current player can move selected piece
    //Does the color of the player match the color of the piece
    static protected boolean canMovePiece(Game game)
    {
        return true; //Default value
    }

    public static ArrayList<Square> getAllPieces(Board board)
    {
        ArrayList<Square> pieces = new ArrayList<>();

        for (int i = 0; i < 8; i++) 
        {
            for (int j = 0; j < 8; j++)
            {
                Square boardSquare = board.getSquare(i, j); 
                if (boardSquare != null) 
                {
                    pieces.add(boardSquare);
                }
            }
        }

        return pieces;
    }

    // removes captured pieces from the board 
    // find out if this needs to report a piece as 'being captured'
    static protected boolean removeCaptured(LinkedList<Moves> moves, Board board)
    {
        return false;
    }
>>>>>>> parent of e5a79c2 (Revert "Merge branch 'main' of https://github.com/utastudents/cse3310-sp25-002")
}
.visible {
display: block;
}
</style>
</head>
<body>
<div id="new_account" class="hidden"
style =
"position: absolute;
       top: 0;
       left: 0;
       width: 100%;
       height: 100%;
       display: flex;
       align-items: center;
       justify-content: center;
       background: rgba(255,255,255,0.9);
       z-index: 100;">
New Account/Login 
<div id="login_container" 
style="width: 100%;
       padding: 12px;
       font-size: 16px;
       border: 1px solid #ccc;
       border-radius: 8px;
       box-sizing: border-box;
       outline: none;
       transition: border-color 0.3s ease;">
<h2>Login</h2>
<div class="form-group">
<input type="text" placeholder="Enter your Username" id="username"  
style =
"width: 100%;
           padding: 12px;
           font-size: 16px;
           border: 1px solid #ccc;
           border-radius: 8px;
           box-sizing: border-box;
           outline: none;
           transition: border-color 0.3s ease;">
</div>
<button onclick="sendUsername()" style="
         padding: 12px 24px;
         font-size: 16px;
         background-color: #333;
         color: #fff;
         border: none;
         border-radius: 8px;
         cursor: pointer;">Join Game</button>
</div>

<script>
function sendUsername()
{
const username = document.getElementById("username").value.trim();
const errorDiv = document.getElementById("username_error");
if(!username)
{
errorDiv.innerText = "Please enter a username."
errorDiv.style.display = "block"
return;
}
}
const userData = 
{
type: "join",
username: username
}
// Sending data through websocket
if(connection === WebSocket.OPEN)
{
connection.send(JSON.stringify(userData));
}
else
{
errorDiv.innerText = "Connection to server not available.";
errorDiv.style.display = "block";
}

</script>
</div>
<div id="join_game">

join game
</div>
<div id="game_display">
game display
<div style="width: 336px; max-width:auto; margin: 10px auto;" class="hidden" id="game_display_container">
<div style="text-align: center; font-size: 16px; margin-top: 10px; font-weight: bold; color: #333;">
<p id="current-player">Current Player: Player 1</p>
</div>
<br/>
<div class="game" style="display: inline-block; border: 4px solid #333; background: #333; margin: 0 auto; box-shadow: 0 4px 12px rgba(0,0,0,0.3); width: 328px; height: 328px; padding: 0; box-sizing: content-box;"></div>
<div style="margin-top: 15px; text-align: center;">
<button id="resign_buttom" class="button" style="background-color: #ff4f4f; color: white; padding: 8px 16px; font-size: 14px; border: none; cursor: pointer; margin: 5px; border-radius: 20px; font-weight: bold; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); transition: all 0.2s ease;">Resign</button>
<button id="offer_draw_button" class="button" style="background-color: #ffcb45; color: #333; padding: 8px 16px; font-size: 14px; border: none; cursor: pointer; margin: 5px; border-radius: 20px; font-weight: bold; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2); transition: all 0.2s ease;">Offer Draw</button>
</div>
</div>
</div>
<div id="summary">
summary
</div>
<div id="stuff">
<input type="button" value="Send Something" onclick="msg()">
<label for="tbox">Received</label>
<textarea id="tbox" name="tbox" rows="4" cols="50">
</textarea>
</div>
<!-- This javascript code contains logic for the game display. Imports the CheckersBoard class which contains methods to create and add functionality to the checkers board. -->
<script src="js/game_display_checkers.js"></script>
</body>
</html>
<script>
// each of the 'divs' in the html page should have attributes to allow them to be hidden.
// the page manager should send a small json message, that tells which of these are hidden
// and which are visible when there is a change
// something like "{"display_new_account":t,"display_join_game":f,"display_game_display:f,"display_summary":f}"


// a reminder.  we want to create one websocket connection, and use if, basically forever.
// or until the user decides to go to another page.
// this requires everything to load from this page.

    // this element is used to display the game board
    let gameContainer = document.getElementById("game_display_container");
    // this variable is used to check if the game board has been initialized
    let game_display_checkers_board_initialized = false;
    let checkerBoard = null;

var connection = null;

var serverUrl;
serverUrl = "ws://" + window.location.hostname + ":" + (parseInt(location.port) + 100);
connection = new WebSocket(serverUrl);

connection.onopen = function (evt) {
console.log("open");
}

connection.onclose = function (evt) {
console.log("close");
}

connection.onmessage = function (evt) {
var msg;
msg = evt.data;


console.log("Message received: " + msg);
document.getElementById("tbox").innerHTML = msg + '\n' + document.getElementById("tbox").innerHTML;
const obj = JSON.parse(msg);

let data = JSON.parse(msg);

// this function handles/receives all the data that is sent to the game dispaly through websockets. Please check `webapp\js\game_display_checkers.js` for more details
        if (game_display_checkers_board_initialized){
            game_display_handle_websocket_received_data(checkerBoard, data);
        };
        game_display_handle_websocket_received_data(connection, data);

}


    // uncomment the line below to test rendering the game board
    // setTimeout(showGameDisplay("134242424", "Player 1"),3000);
    // Note from Game Display: uncomment the line below to test rendering the game board
    setTimeout(show_game_display(connection, "134242424", "Player 1", "this is player2", "B"),3000);

class UserEvent {
msg;
}

function msg() {
console.log("button clicked");
U = new UserEvent();
U.msg="i pushed a button";
connection.send(JSON.stringify(U));
console.log(JSON.stringify(U))
}

</script>
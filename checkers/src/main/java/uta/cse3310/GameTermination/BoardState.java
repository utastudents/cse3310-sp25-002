package uta.cse3310.GameTermination;
import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GameManager.Player;
import uta.cse3310.GameManager.Board;

import java.util.HashMap;

public class BoardState {
	//the board variable is currently private inside Board.java we will need it to be public.
	Board boardState = new Board();

    //represents the 8x8 board
    int[][] board;

    //keeps track of remaining pieces
    HashMap<String, Integer> remainingPieces;

	public boolean hasLegalMoves(Board boardState, int playerId) //METHOD: checks if player has legal moves left
	{
	    return true;
	}

	//will evaluate board state after each move to see which player is winning
    public int determineWinningPlayer(Board boardState, Player player1, Player player2){
    	    return 0;
	}

    //will decide if the game has winner
    public boolean hasAWinner(Board boardState){
        //this method will return either true or false depending if there is a winner or not.

        //this will depend on having no legal moves or capturing all of the opponet pieces.
	    return remainingPieces.get("Player1") == 0 || remainingPieces.get("Player2") == 0;
    }


    public boolean isGameDraw(BoardState boardState){
        //this method will return if game has no winner and is draw
	    return true;
    }
}

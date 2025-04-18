package cse3310.uta.GamePlayTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GamePlay.rules;

public class MoveList extends rules{

    @Test
    public void startingBoard()
    {
        Boolean notFound = false;
        Board board = new Board();
        board.initializeBoard();

        Map<Square, Moves> correctOutput = new HashMap<>();
        ArrayList<Square> whitePieces = new ArrayList<>();

        //getting correct moveList of the starting board for white
        //j represents column
        for (int j = 0; j < 8; j += 2) 
        {
            /*using 5 for row since only the first row of white pieces can move*/
            Square square = board.getSquare(5, j);
            whitePieces.add(square); //adds white pieces from row 5
            Moves moves = new Moves();

            //using 4 for row since white can only move up
            if (j == 0)
                moves.addNext(new Move(square, new Square(4, 1)));
            else {
                moves.addNext(new Move(square, new Square(4,j-1)));
                moves.addNext(new Move(square, new Square(4,j+1)));
            }
            correctOutput.put(square, moves);
        }

        Map<Square, Moves> output = moveList(board, true);

        for (Square square : whitePieces)
        {
            Boolean found = false;
            Moves outputMoves = output.get(square);
            Moves correctOutputMoves = correctOutput.get(square);
            ArrayList<Move> outputMovesCopy = new ArrayList<>();
            ArrayList<Move> correctOutputMovesCopy = new ArrayList<>();

            //checks number of moves for correctOutput and output are the same
            if (outputMoves.size() != correctOutputMoves.size())
            {
                notFound = true;
                break;
            }

            //put the Moves in arraylists to iterate over them
            for (int i = outputMoves.size(); i > 0; --i) 
            {
                outputMovesCopy.add(outputMoves.get(i));
                correctOutputMovesCopy.add(correctOutputMoves.get(i));

            }

            for (Move move : correctOutputMovesCopy)
            {
                Square start = move.getStart();
                Square dest = move.getDest();

                for (Move move2 : outputMovesCopy)
                {
                    Square start2 = move2.getStart();
                    Square dest2 = move2.getDest();
                    
                    //check if a move from correctOutput is the same as a move from output
                    if (start.getColor().equals(start2.getColor()) && start.hasPiece() == start2.hasPiece() &&
                        start.getCol() == start2.getCol() && start.getRow() == start2.getRow() &&
                        start.isKing() == start2.isKing() && dest.getColor().equals(dest2.getColor()) && 
                        dest.hasPiece() == dest2.hasPiece() && dest.getCol() == dest2.getCol() && 
                        dest.getRow() == dest2.getRow() && dest.isKing() == dest2.isKing())
                    {
                        found = true;
                    }
                }
                
            }

            if (!found) {
                notFound = true;
                break;
            }
            
            
        }

        assertTrue("output is not correct\n", notFound == false);
    }

}
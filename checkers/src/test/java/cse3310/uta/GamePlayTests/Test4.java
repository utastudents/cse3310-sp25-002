package cse3310.uta.GamePlayTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GamePlay.rules;

public class Test4 extends rules{

    @Test
    public void exampleEndGame() //black will be moving
    {
        Map<Square, Moves> correctOutput = new HashMap<>();
        ArrayList<Square> blackPieces = new ArrayList<>();
        Boolean notFound = false;

        Board board = new Board();
        board.getSquare(3, 1).placeWhite();
        board.getSquare(5, 1).placeWhite();
        board.getSquare(7, 3).placeWhite();
        board.getSquare(7, 7).placeWhite();
        board.getSquare(6, 6).placeWhite();

        board.getSquare(0, 2).placeBlack();
        board.getSquare(2, 2).placeBlack();
        board.getSquare(1, 3).placeBlack();
        board.getSquare(2, 4).placeBlack();
        board.getSquare(0, 6).placeBlack();
        board.getSquare(5, 7).placeBlack();

        blackPieces.add(board.getSquare(0, 2));
        blackPieces.add(board.getSquare(2, 2));
        blackPieces.add(board.getSquare(1, 3));
        blackPieces.add(board.getSquare(2, 4));
        blackPieces.add(board.getSquare(0, 6));
        blackPieces.add(board.getSquare(5, 7));
        
        //looping 5 times since there are only 5 black pieces that can move
        for (int i = 0; i < 5; ++i) 
        {
            Moves moves = new Moves();
            if (i == 0)
            {
                moves.addNext(board.getSquare(0, 2), board.getSquare(1, 1));
                correctOutput.put(board.getSquare(0,2), moves);
            }
            else if (i == 1)
            {
                moves.addNext(board.getSquare(2, 2), board.getSquare(4, 0));
                moves.addNext(board.getSquare(2, 2), board.getSquare(3, 3));
                moves.addNext(board.getSquare(4, 0), board.getSquare(6, 2));
                correctOutput.put(board.getSquare(2,2), moves);
            }
            else if (i == 2)
            {
                moves.addNext(board.getSquare(2, 4), board.getSquare(3, 5));
                moves.addNext(board.getSquare(2, 4), board.getSquare(3, 3));
                correctOutput.put(board.getSquare(2,4), moves);
            }
            else if (i == 3)
            {
                moves.addNext(board.getSquare(0, 6), board.getSquare(1, 5));
                moves.addNext(board.getSquare(0, 6), board.getSquare(1, 7));
                correctOutput.put(board.getSquare(0,6), moves);
            }
            else if (i == 4)
            {
                moves.addNext(board.getSquare(5, 7), board.getSquare(7, 5));
                correctOutput.put(board.getSquare(5,7), moves);
            }
        }

        Map<Square, Moves> output = moveList(board, false);

        for (Square square : blackPieces)
        {
            
            Moves outputMoves = output.get(square);
            Moves correctOutputMoves = correctOutput.get(square);
            ArrayList<Move> outputMovesCopy = new ArrayList<>();
            ArrayList<Move> correctOutputMovesCopy = new ArrayList<>();

            //if that piece on that square has no moves and output correctly reflects that, continue
            //else, found error
            if (correctOutputMoves == null && outputMoves == null)
                continue;
            else if (correctOutputMoves == null && outputMoves != null)
            {
                notFound = true;
                assertTrue("correctOutputMoves is empty but outputMoves is not\n" + 
                           "square row: " + square.getRow() + " square col: " + square.getCol() + '\n', 
                           notFound == false);
                break;
            }

            //checks number of moves for correctOutput and output are the same
            if (outputMoves.size() != correctOutputMoves.size())
            {
                notFound = true;

                System.err.println("Output Moves: ");
                for (int i = 0; i < outputMoves.size(); ++i)
                {
                    System.err.println("Size: " + outputMoves.size());
                    System.err.println(i + ". start row: " + outputMoves.get(i).getStart().getRow() + 
                                       " start col: " + outputMoves.get(i).getStart().getCol());
                    System.err.println(i + ". dest row: " + outputMoves.get(i).getDest().getRow() + 
                                       " dest col: " + outputMoves.get(i).getDest().getCol() + '\n');                   
                }

                System.err.println("Correct Output Moves: ");
                for (int i = 0; i < correctOutputMoves.size(); ++i)
                {
                    System.err.println("Size: " + correctOutputMoves.size());
                    System.err.println(i + ". start row: " + correctOutputMoves.get(i).getStart().getRow() + 
                                       " start col: " + correctOutputMoves.get(i).getStart().getCol());
                    System.err.println(i + ". dest row: " + correctOutputMoves.get(i).getDest().getRow() + 
                                       " dest col: " + correctOutputMoves.get(i).getDest().getCol() + '\n');                   
                }

                assertTrue("outputMoves size is not the same as correctOutputMoves size\n" +
                           "square row: " + square.getRow() + " square col: " + square.getCol() + '\n', 
                           notFound == false);
                break;
            }

            //put the Moves in arraylists to iterate over them
            // Bug fix: indexed 1 too far
            for (int i = outputMoves.size() - 1; i >= 0; --i) 
            {
                outputMovesCopy.add(outputMoves.get(i));
                correctOutputMovesCopy.add(correctOutputMoves.get(i));

            }

            for (Move move : correctOutputMovesCopy)
            {
                // Bug fix: initialize found inside of this loop instead of before
                Boolean found = false;
                Square start = move.getStart();
                Square dest = move.getDest();

                for (Move move2 : outputMovesCopy)
                {
                    Square start2 = move2.getStart();
                    Square dest2 = move2.getDest();
                    
                    //check if a move from correctOutput is the same as a move from output
                    // Bug fix: Original allowed a NULL.equals() call. That is not legal java.
                    if (Objects.equals(start.getColor(), start2.getColor()) && start.hasPiece() == start2.hasPiece() &&
                    start.getCol() == start2.getCol() && start.getRow() == start2.getRow() &&
                    start.isKing() == start2.isKing() && Objects.equals(dest.getColor(), dest2.getColor()) && 
                    dest.hasPiece() == dest2.hasPiece() && dest.getCol() == dest2.getCol() && 
                    dest.getRow() == dest2.getRow() && dest.isKing() == dest2.isKing())
                    {
                        found = true;
                        assertTrue("move from correctOutput is not the same as move from output\n" +
                                   "square row: " + square.getRow() + " square col: " + square.getCol() + '\n', 
                                   notFound == false);
                        break;
                    }
                }
                //Bug fix: move this if into the for loop
                if (!found) {
                    notFound = true;
                    break;
                }
            }

            
            
            
        }

        assertTrue("output is not correct\n", notFound == false);
    }

}
package uta.cse3310.Test;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Game;

public class Test{
     public static Board board = new Board();
     public static void main(String[] args){
          // board.initializeBoard();
          // System.out.print(board.toString());
          // Moves p1moves = new Moves();
          // Moves p2moves = new Moves();

          // p1moves.addNext(p1moves.makeMove(board, board.getSquare(2, 1), false, false, false));
          // board.execute(p1moves.getFirst(), false);
          // System.out.print(board.toString());

          // p2moves.addNext(p2moves.makeMove(board, board.getSquare(5, 2), true, true, false));
          // board.execute(p2moves.getFirst(), false);
          // System.out.print(board.toString());

          Game game = new Game(0, 1, false, true, 1);
          System.out.println(game.getBoard().toString());
          // moves.getStart(4)
          // moves.getDest(4)
     }
}
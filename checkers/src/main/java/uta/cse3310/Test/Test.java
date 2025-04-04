package uta.cse3310.Test;

import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;

public class Test{
     public static Board board = new Board();
     public static void main(String[] args){
          Square start = new Square(2, 1);
          Square dest = new Square(3, 2);
          board.initializeBoard();
          System.out.print(board.toString());
          Moves moves = new Moves();
          moves.addNext(moves.makeMove(board, start, false, false, false));
          board.execute(moves.getFirst(), false);
          System.out.print(board.toString());
     }
}
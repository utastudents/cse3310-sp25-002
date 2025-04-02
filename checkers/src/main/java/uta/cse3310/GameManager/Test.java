package uta.cse3310.GameManager;

public class Test{
     public static Board board = new Board();
     public static void main(String[] args){
          Square start = new Square(2, 1);
          Square dest = new Square(3, 2);
          board.initializeBoard();
          System.out.print(board.toString());
          Move move = new Move(start, dest);
          board.execute(move);
          System.out.print(board.toString());
     }
}
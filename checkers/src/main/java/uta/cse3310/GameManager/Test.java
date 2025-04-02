package uta.cse3310.GameManager;

public class Test{
     public static Board board = new Board();
     public static void main(String[] args){
          board.initializeBoard();
          System.out.print(board.toString());
     }
}
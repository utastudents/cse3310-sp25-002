package uta.cse3310.GameManager;

public class Board {
     private Square[][] board;

     public Board() {
          board = new Square[8][8];
          for (int i = 0; i < 8; i++) {
               for (int j = 0; j < 8; j++) {
                    board[i][j] = new Square(i, j);
               }
          }
     }

     public Square getSquare(int row, int col) {
          return board[row][col];
     }

     public void initializeBoard() {
          for (int i = 0; i < 3; i++) {
               for (int j = (i % 2 == 0) ? 1 : 0; j < 8; j += 2) {
                    board[i][j].placeBlack();
                    ; // Black pieces
               }
          }
          for (int i = 5; i < 8; i++) {
               for (int j = (i % 2 == 0) ? 1 : 0; j < 8; j += 2) {
                    board[i][j].placeWhite();
                    ; // White pieces
               }
          }
     }

     public void setSquare(Square square) {
          board[square.getRow()][square.getCol()] = square;
     }

}

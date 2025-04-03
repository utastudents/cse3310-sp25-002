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
     
     public void initializeBoard() {
          for (int i = 0; i < 3; i++) {
               for (int j = (i % 2 == 0) ? 1 : 0; j < 8; j += 2) {
                    board[i][j].placeBlack(); // Black pieces
               }
          }
          for (int i = 5; i < 8; i++) {
               for (int j = (i % 2 == 0) ? 1 : 0; j < 8; j += 2) {
                    board[i][j].placeWhite(); // White pieces
               }
          }
     }

     public Square getSquare(int row, int col){return board[row][col];}
     public void setSquare(Square square){board[square.getRow()][square.getCol()] = square;}

     public void execute(Move move, boolean promote){
          Square start = board[move.getStart().getRow()][move.getStart().getCol()];
          boolean pieceColor = start.getColor();
          boolean isKing = start.isKing();

          Square end = board[move.getDest().getRow()][move.getDest().getCol()];
          end.place(pieceColor, isKing);
          if(promote){end.makeKing();}
          start.remove();
     }

     public String toString(){
          StringBuilder builder = new StringBuilder();
          builder.append("\n------------------\n");
          for(int i = 0; i<8; i++){
               builder.append("|");
               for(int j=0;j<8;j++){
                    String piece = getSquare(i, j).hasPiece() ? "██" : "  ";
                    builder.append(piece);
               }
               builder.append("|\n");
          }
          builder.append("------------------");
          return builder.toString();
     }

}

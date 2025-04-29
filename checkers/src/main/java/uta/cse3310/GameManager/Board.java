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

          System.out.println("[DEBUG Board.execute] Executing move: ("+start.getRow()+","+start.getCol()+") -> ("+end.getRow()+","+end.getCol()+")");
          System.out.println("[DEBUG Board.execute] Start square state before remove: hasPiece="+start.hasPiece()+", color="+(start.getColor() == null ? "null" : (start.getColor() ? "W" : "B")));
          System.out.println("[DEBUG Board.execute] End square state before place: hasPiece="+end.hasPiece()+", color="+(end.getColor() == null ? "null" : (end.getColor() ? "W" : "B")));

          end.place(pieceColor, isKing);
          if(promote){end.makeKing();}
          start.remove();

          System.out.println("[DEBUG Board.execute] Start square state AFTER remove: hasPiece="+start.hasPiece()+", color="+(start.getColor() == null ? "null" : "null"));
          System.out.println("[DEBUG Board.execute] End square state AFTER place: hasPiece="+end.hasPiece()+", color="+(end.getColor() ? "W" : "B")); 
     }

     public String toString(){
          StringBuilder builder = new StringBuilder();

          builder.append("\n------------------\n");
          for(int i = 0; i<8; i++){
               builder.append("|");
               for(int j=0;j<8;j++){
                    Boolean color = getSquare(i, j).getColor();
                    String piece = (color == null) ? "  " : (color ? "██" : "▒▒");
                    builder.append(piece);
               }
               builder.append("|\n");
          }
          builder.append("------------------");
          return builder.toString();
     }

}

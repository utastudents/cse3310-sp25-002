package uta.cse3310.GameManager;
import uta.cse3310.GamePlay.rules;

public class Board {
     public Square[][] board;

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

     public Square getSquare(int row, int col){
          if (rules.inBounds(row, col)) {
               return board[row][col];
          }
          return null;
     }


     public void setSquare(Square square){
          if (square != null && rules.inBounds(square.getRow(), square.getCol())) {
               board[square.getRow()][square.getCol()] = square;
          } else if (square != null) {
               System.err.println("[ERROR Board.setSquare] Attempted to set square out of bounds: (" + square.getRow() + "," + square.getCol() + ")");
          }
     }


     public void execute(Move move, boolean isCapture) {

          Square start = getSquare(move.getStart().getRow(), move.getStart().getCol());
          Square end = getSquare(move.getDest().getRow(), move.getDest().getCol());

          if (start == null || end == null || !start.hasPiece()) {
               System.err.println("[ERROR Board.execute] Invalid move coordinates or no piece at start.");
               System.err.println("Move Details: Start(" + (move.getStart()!=null?move.getStart().getRow()+","+move.getStart().getCol():"null") + ") -> End(" + (move.getDest()!=null?move.getDest().getRow()+","+move.getDest().getCol():"null") + ")");
               System.err.println("Board State: Start=" + start + ", End=" + end + ", Start hasPiece=" + (start != null && start.hasPiece()));
               return;
          }

          boolean pieceColor = start.getColor();
          boolean isKing = start.isKing();
          int destRow = end.getRow();

          boolean promote = (!isKing && ((pieceColor && destRow == 0) || (!pieceColor && destRow == 7)));

          System.out.println("[DEBUG Board.execute] Executing move: ("+start.getRow()+","+start.getCol()+") -> ("+end.getRow()+","+end.getCol()+") Capture: " + isCapture + " Promote: " + promote);
          System.out.println("[DEBUG Board.execute] Start square state before: hasPiece="+start.hasPiece()+", color="+(start.getColor() ? "W" : "B")+", isKing="+start.isKing());
          System.out.println("[DEBUG Board.execute] End square state before: hasPiece="+end.hasPiece()+", color="+(end.getColor() == null ? "null" : (end.getColor() ? "W" : "B")));

          end.place(pieceColor, isKing || promote);
          if (promote) {
               System.out.println("[DEBUG Board.execute] Piece promoted to King at ("+end.getRow()+","+end.getCol()+")");
          }

          start.remove();

          if (isCapture) {
               System.out.println("[DEBUG Board.execute] Processing capture(s)...");
               int rowDiff = end.getRow() - start.getRow();
               int colDiff = end.getCol() - start.getCol();
               int steps = Math.abs(rowDiff) / 2;

                    System.out.println("[DEBUG Board.execute]   Detected jump distance: " + Math.abs(rowDiff) + " rows. Number of jumps: " + steps);

               for (int i = 1; i <= steps; i++) {
                    int midRow = start.getRow() + (rowDiff / steps) * i - (rowDiff / steps / 2);
                    int midCol = start.getCol() + (colDiff / steps) * i - (colDiff / steps / 2);

                    System.out.println("[DEBUG Board.execute]   Checking middle square for jump " + i + ": (" + midRow + "," + midCol + ")");

                    if (rules.inBounds(midRow, midCol)) {
                         Square middleSquare = getSquare(midRow, midCol);
                         if (middleSquare != null && middleSquare.hasPiece() && middleSquare.getColor() != pieceColor) {
                              System.out.println("[DEBUG Board.execute]     Removing opponent piece at (" + midRow + "," + midCol + ")");
                              middleSquare.remove();
                         } else {
                              System.err.println("[WARN Board.execute]     Expected opponent piece at ("+midRow+","+midCol+") for capture sequence, but found none or same color.");
                              System.err.println("[WARN Board.execute]       Middle Square State: " + (middleSquare != null ? "hasPiece="+middleSquare.hasPiece()+", color="+(middleSquare.getColor()==null?"null":middleSquare.getColor()?"W":"B") : "null"));
                         }
                    } else {
                              System.err.println("[ERROR Board.execute] Calculated middle square ("+midRow+","+midCol+") is out of bounds for capture sequence.");
                    }
               }
          }

          System.out.println("[DEBUG Board.execute] Start square state AFTER remove: hasPiece="+start.hasPiece()+", color=null");
          System.out.println("[DEBUG Board.execute] End square state AFTER place: hasPiece="+end.hasPiece()+", color="+(end.getColor() ? "W" : "B") + ", isKing="+end.isKing());
     }

     public String toString(){
          StringBuilder builder = new StringBuilder();
          builder.append("\n------------------\n");
          for(int i = 0; i<8; i++){
               builder.append("|");
               for(int j=0;j<8;j++){
                    Square sq = getSquare(i, j);
                    String pieceRep = "  ";
                    if (sq != null && sq.hasPiece()) {
                         boolean color = sq.getColor();
                         boolean king = sq.isKing();
                         if (color) { // White
                              pieceRep = king ? "WW" : "ww";
                         } else { // Black
                              pieceRep = king ? "BB" : "bb";
                         }
                         builder.append(pieceRep);
                    } else {
                         builder.append("..");
                    }
               }
               builder.append("|\n");
          }
          builder.append("------------------");
          return builder.toString();
     }



     public Board copy() {
          Board newBoard = new Board();
          for (int i = 0; i < 8; i++) {
               for (int j = 0; j < 8; j++) {
                    Square original = this.getSquare(i, j);
                    Square copySquare = newBoard.getSquare(i, j);
                    if (original != null && copySquare != null) {
                         if (original.hasPiece()) {
                              copySquare.place(original.getColor(), original.isKing());
                         } else {
                              copySquare.remove();
                         }
                    }
               }
          }
          return newBoard;
     }

}

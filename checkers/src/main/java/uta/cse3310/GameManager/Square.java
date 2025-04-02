public class Square{
     private int row, col;
     private Boolean piece;
     private Boolean color;
     private Boolean king;

     public Square(int row, int col){
          this.row = row;
          this.col = col;
          this.piece = false;
          this.color = null;
          this.king = false;
     }

     public void place(boolean color, boolean isKing){
          this.piece = true;
          this.color = color;
          this.king = isKing;
     }
     public void placeBlack(){this.place(false, false);}
     public void placeBlackKing(){this.place(false, true);}
     public void placeWhite(){this.place(true, false);}
     public void placeWhiteKing(){this.place(true, true);}
     public void makeKing(Square square){square.king = true;}
     public void remove(boolean color, boolean isKing){
          this.piece = false;
          this.color = null;
          this.king = false;
     }

     public boolean hasPiece(){return piece;}
     public Boolean getColor(){return color;}
     public boolean isKing(){return king;}
     public int getRow(){return row;}
     public int getCol(){return col;}
}

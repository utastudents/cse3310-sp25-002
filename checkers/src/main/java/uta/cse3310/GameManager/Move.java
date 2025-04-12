package uta.cse3310.GameManager;

public class Move{
    private Square start;
    private Square dest;

    public Move(Square start, Square dest){
         this.start = start;
         this.dest = dest;
    }

    public Move(int startRow, int startCol, int destRow, int destCol){
         this.start = new Square(startRow, startCol);
         this.dest = new Square(destRow, destCol);
    }

    public Square getStart(){return start;}
    public Square getDest(){return dest;}
}

package uta.cse3310.GameManager;
import java.util.LinkedList;

class Move{
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

public class Moves{
     private LinkedList<Move> moves;
     public Moves(LinkedList<Move> moves){this.moves = moves;}
     public Moves(){this.moves = new LinkedList<>();}

     public void addNext(Square start, Square end){this.moves.add(new Move(start, end));}
     public void addNext(int startRow, int startCol, int destRow, int destCol){this.moves.add(new Move(startRow, startCol, destRow, destCol));}

     public LinkedList<Move> getMoves(){return moves;}
     public Move getFirst(){return moves.getFirst();}
     public Move getNext(int n){return moves.get(n + 1);}
     public Move getNext(Move move){return moves.get(moves.indexOf(move) + 1);}
}

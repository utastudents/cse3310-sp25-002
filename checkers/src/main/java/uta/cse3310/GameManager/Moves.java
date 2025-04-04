package uta.cse3310.GameManager;
import java.util.LinkedList;

public class Moves{
     private LinkedList<Move> moves;
     public Moves(LinkedList<Move> moves){this.moves = moves;}
     public Moves(){this.moves = new LinkedList<>();}

     public Move makeMove(Board board, Square start, boolean color, boolean direction, boolean take){
          int colSign = direction ? -1 : 1;
          int rowSign = color ? -1 : 1;
          int step = take ? 2 : 1;

          Square dest = board.getSquare(start.getRow() + (rowSign * step), start.getCol() + (colSign * step * rowSign));
          Move move = new Move(start, dest);
          return move;
     }

     public void addNext(Move move){this.moves.add(move);}
     public void addNext(Square start, Square end){this.moves.add(new Move(start, end));}
     public void addNext(int startRow, int startCol, int destRow, int destCol){this.moves.add(new Move(startRow, startCol, destRow, destCol));}

     public LinkedList<Move> getMoves(){return moves;}
     public Move getFirst(){return moves.getFirst();}
     public Move get(int idx){return moves.get(idx);}
     public Square getStart(int idx){return moves.get(idx).getStart();}
     public Square getDest(int idx){return moves.get(idx).getDest();}
     public int size(){return moves.size();}
     public Move getNext(int n){return moves.get(n + 1);}
     public Move getNext(Move move){return moves.get(moves.indexOf(move) + 1);}
}

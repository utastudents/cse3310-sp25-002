
package uta.cse3310.GamePlay;
import java.util.LinkedList;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Square;
import java.util.ArrayList;


class Checkpieces 
{
    // class for checkpieces
}
class Board 
{
   private Checkpieces[][] grid = new Checkpieces[8][8];

    public Board()
    {
       //class to simulate the actual Board position 
    }

    public Checkpieces getCheckerAt(int row, int col) 
    {
        return grid[row][col];
    }
}
public class MethodScanner {

    // Method to scan the board and get a list of the pieces that were found
    public static ArrayList<Checkpieces> getAllPieces(Board board) {
        ArrayList<Checkpieces> pieces = new ArrayList<Checkpieces>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Checkpieces item = board.getCheckerAt(i, j);
                if (item != null) {
                    pieces.add(item);
                }
            }
        }

        return pieces;
    }
}

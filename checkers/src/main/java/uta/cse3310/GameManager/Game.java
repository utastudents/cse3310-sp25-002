package uta.cse3310.GameManager;

public class Game
{

    private Board board;
    private Player player1;
    private Player player2;

    public Game(int player1id, int player2id, boolean player1color, boolean player2color)
    {
        board = new Board();
        player1 = new Player(player1id, player1color);
        player2 = new Player(player2id, player2color);
    }
    
}

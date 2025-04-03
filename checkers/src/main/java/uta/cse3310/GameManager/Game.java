package uta.cse3310.GameManager;

public class Game
{

    private Board board;
    private Player player1;
    private Player player2;
    private int gameNumber;

    public Game(int player1id, int player2id, boolean player1color, boolean player2color, int gameNumber)
    {
        board = new Board();
        board.initializeBoard();
        player1 = new Player(player1id, player1color);
        player2 = new Player(player2id, player2color);
        this.gameNumber = gameNumber;
    }
    public void setBoard(Board board)
    {   
        //board.
    }
    public int getPlayer1ID()
    {
        return player1.getPlayerId();
    }
    public int getPlayer2ID()
    {
        return player2.getPlayerId();
    }
    public boolean getPlayer1Color()
    {
        return player1.getColor();
    }
    public boolean getPlayer2Color()
    {
        return player2.getColor();
    }
    public Board getBoard()
    {
        return board;
    }
    public int getGameNumber()
    {
        return gameNumber;
    }

}

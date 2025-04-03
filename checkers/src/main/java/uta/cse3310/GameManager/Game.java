package uta.cse3310.GameManager;

public class Game
{

    private Board board;
    private Player player1;
    private Player player2;
    private int gameNumber;
    private boolean player1Turn = true;

    public Game(int player1id, int player2id, boolean player1color, boolean player2color, int gameNumber){
        board = new Board();
        board.initializeBoard();
        player1 = new Player(player1id, player1color);
        player2 = new Player(player2id, player2color);
        this.gameNumber = gameNumber;
    }
    public Player getWinner(){
        int playerOnePieces = player1.getPieces();
        int playerTwoPieces = player2.getPieces();
        if(playerOnePieces == 0){return player2;}
        if(playerTwoPieces == 0){return player1;}
        return null;
    }
    public Player getCurrentTurn(){return player1Turn ? player1 : player2;}
    public void switchTurn(){player1Turn = !player1Turn;}
    public void updateBoard(Board board){this.board = board;}
    public int getPlayer1ID(){return player1.getPlayerId();}
    public int getPlayer2ID(){return player2.getPlayerId();}
    public boolean getPlayer1Color(){return player1.getColor();}
    public boolean getPlayer2Color(){return player2.getColor();}
    public Board getBoard(){return board;}
    public int gameNumber(){return gameNumber;}
}

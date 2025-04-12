package uta.cse3310.GameManager;

import uta.cse3310.Bot.BotI.BotI;
import uta.cse3310.Bot.BotII.BotII;

public class Game
{
    private Board board;
    private Player player1;
    private Player player2;
    private int gameNumber;
    private BotI bot1;
    private BotII bot2;
    private boolean player1Turn = true;
    private boolean gameIsActive = true;
    private boolean draw = false;
    private boolean player1quit = false;
    private boolean player2quit = false;

    public Game(int player1id, int player2id, boolean player1color, boolean player2color, int gameNumber){
        board = new Board();
        board.initializeBoard();
        bot1 = new BotI(player1color);
        bot2 = new BotII(player2color);
        player1 = new Player(player1id, player1color);
        player2 = new Player(player2id, player2color);
        this.gameNumber = gameNumber;
    }
    public Player getWinner(){
        int playerOnePieces = player1.getPieces();
        int playerTwoPieces = player2.getPieces();
        if(draw){
            gameIsActive = false;
            return null;
        }
        else if(playerOnePieces == 0 || player1quit){
            gameIsActive = false;
            return player2;
        }
        else if(playerTwoPieces == 0 || player2quit){
            gameIsActive = false;
            return player1;
        }
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
    public boolean gameActive(){return gameIsActive;}
    public void GameDeclareDraw(){draw = true;}
    public int getPlayer1Score(){return player1.getScore();}
    public int getPlayer2Score(){return player2.getScore();}
    public void Player1Quit(){player1quit = true;}
    public void Player2Quit(){player2quit = true;}
}

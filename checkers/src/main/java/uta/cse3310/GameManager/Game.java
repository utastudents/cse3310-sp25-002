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

    /* Game constructor which will initialize the board, will create two new bots which return their assigned color
     * two new players will be created with their designated ID and color 
     * each game will have a unique id 
     */
    public Game(int player1id, int player2id, boolean player1color, boolean player2color, int gameNumber, String player1name, String player2name){
        board = new Board();
        board.initializeBoard();
        bot1 = new BotI(player1color);
        bot2 = new BotII(player2color);
        player1 = new Player(player1id, player1color, player1name);
        player2 = new Player(player2id, player2color, player2name);
        this.gameNumber = gameNumber;
    }
    /* this method returns the winner of the match, if there's no winner it will return null
     * if the game is declared a draw, then it will set game is active to false and return null
     * so if getWinner is null and gameIsActive that means it's a draw
     */
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
    
    /* methods to recieve game information */
    //returns who's turn it is
    public Player getCurrentTurn(){return player1Turn ? player1 : player2;}
    //switch player turns
    public void switchTurn(){player1Turn = !player1Turn;}
    //update the board
    public void updateBoard(Board board){this.board = board;}
    //return player1's ID
    public int getPlayer1ID(){return player1.getPlayerId();}
    //return player2's ID
    public int getPlayer2ID(){return player2.getPlayerId();}
    //return player1's color
    public boolean getPlayer1Color(){return player1.getColor();}
    //return player2's color
    public boolean getPlayer2Color(){return player2.getColor();}
    //return player1's name
    public String getPlayer1Name(){return player1.getName();}
    //return player2's name
    public String getPlayer2Name(){return player2.getName();}
    //return the board
    public Board getBoard(){return board;}
    //return the number of the game
    public int gameNumber(){return gameNumber;}
    //return game activity status
    public boolean gameActive(){return gameIsActive;}
    //declare draw and set game to inactive
    public void GameDeclareDraw(){
        draw = true;
        gameIsActive = false;
    }
    //return player scores
    public int getPlayer1Score(){return player1.getScore();}
    public int getPlayer2Score(){return player2.getScore();}
    // if quitting is implemented these set the player to quit
    public void Player1Quit(){
        player1quit = true;
        gameIsActive = false;
    }
    public void Player2Quit(){
        player2quit = true;
        gameIsActive = false;
    }
}

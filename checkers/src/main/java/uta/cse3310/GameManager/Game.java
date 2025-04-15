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
     * each game will have a unique id we have conditions to see if the player will be a bot or not, to initialize it
     */
    public Game(int player1id, int player2id, boolean player1color, boolean player2color, int gameNumber){
        board = new Board();
        board.initializeBoard();
        if(player1id == 0){
            bot1 = new BotI(player1color);
            player1 = new Player(player1id, player1color);
        }
        else if(player1id == 1){
            bot2 = new BotII(player1color);
            player1 = new Player(player1id, player1color);
        }
        else{
            player1 = new Player(player1id, player1color);
        }
        if(player2id == 0){
            bot1 = new BotI(player2color);
            player2 = new Player(player2id, player2color);
        }
        else if(player2id == 1){
            bot2 = new BotII(player2color);
            player2 = new Player(player2id, player2color);
        }
        else{
            player2 = new Player(player2id, player2color);
        }
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
    // returning loser as requested by page controller
    public Player getLoser(){
        int playerOnePieces = player1.getPieces();
        int playerTwoPieces = player2.getPieces();
        if(draw){
            gameIsActive = false;
            return null;
        }
        else if(playerOnePieces == 0 || player1quit){
            gameIsActive = false;
            return player1;
        }
        else if(playerTwoPieces == 0 || player2quit){
            gameIsActive = false;
            return player2;
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
    //method in case others want to know if the player1 is a bot or not
    public boolean isPlayer1Bot(){
        return (player1.getPlayerId() == 0 || player1.getPlayerId() == 1);
    }
    //method in case others want to know if the player2 is a bot or not
    public boolean isPlayer2Bot(){
        return (player2.getPlayerId() == 0 || player2.getPlayerId() == 1);
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

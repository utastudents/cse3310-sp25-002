package uta.cse3310.GameManager;

import uta.cse3310.Bot.BotI.BotI;
import uta.cse3310.Bot.BotII.BotII;
import uta.cse3310.GamePlay.GamePlay;
import uta.cse3310.GamePlay.rules;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;





public class Game
{
    private Board board;
    private Player player1;
    private Player player2;
    private String player1Name;
    private String player2Name;
    private int gameNumber;
    private BotI bot1;
    private BotII bot2;
    private boolean turn = true;
    private boolean active = true;
    private boolean draw = false;
    private boolean player1quit = false;
    private boolean player2quit = false;
    private int movesSinceLastCapture;

    /* Game constructor which will initialize the board, will create two new bots which return their assigned color
     * two new players will be created with their designated ID and color 
     * each game will have a unique id we have conditions to see if the player will be a bot or not, to initialize it
     */
    public Game(int player1id, int player2id, boolean player1color, boolean player2color, int gameNumber, String p1Name, String p2Name){
        this.gameNumber = gameNumber;
        this.board = new Board();
        board.initializeBoard();
        this.movesSinceLastCapture = 0;


        this.player1 = new Player(player1id, player1color);
        this.player1Name = p1Name;
        if(player1id == 0){
            this.bot1 = new BotI();
            this.bot1.setColor(player1color);
            this.player1Name = "Bot I";
        } else if(player1id == 1){
            this.bot2 = new BotII();
            this.bot2.setColor(player1color);
            this.player1Name = "Bot II";
        }

        this.player2 = new Player(player2id, player2color);
        this.player2Name = p2Name;
        if(player2id == 0){ // BotI ID
            if (this.bot1 == null || player1id != 0) {
                    this.bot1 = new BotI();
            } else {
                System.out.println("[WARN Game Constructor] Potential shared BotI instance needed?");
            }
            if (this.bot1 != null) this.bot1.setColor(player2color);
            this.player2Name = "Bot I";
        } else if(player2id == 1){
            if (this.bot2 == null || player1id != 1) {
                this.bot2 = new BotII();
            }
            if (this.bot2 != null) this.bot2.setColor(player2color);
            this.player2Name = "Bot II";
        }

        this.turn = !player1color;
        this.active = true;
        this.draw = false;
        this.player1quit = false;
        this.player2quit = false;

    }

    public Game(int player1id, int player2id, boolean player1color, boolean player2color, int gameNumber){
        this(player1id, player2id, player1color, player2color, gameNumber, (player1id == 0 ? "Bot I" : (player1id == 1 ? "Bot II" : "Player " + player1id)),(player2id == 0 ? "Bot I" : (player2id == 1 ? "Bot II" : "Player " + player2id)));
    }


    /* this method returns the winner of the match, if there's no winner it will return null
     * if the game is declared a draw, then it will set game is active to false and return null
     * so if getWinner is null and gameIsActive that means it's a draw
     */
    public Player getWinner(){
        if (player1quit) {
            if (active) endGame();
            return player2;
        }
        if (player2quit) {
            if (active) endGame();
            return player1;
        }


        int playerOnePieces = player1.getPieces();
        int playerTwoPieces = player2.getPieces();

        if(playerOnePieces == 0){
            if (active) endGame();
            return player2;
        }
        else if(playerTwoPieces == 0){
            if (active) endGame();
            return player1;
        }

        return null;
    }



    public boolean checkDrawCondition() {
        if (!active) return draw;

        if (movesSinceLastCapture >= 80) {
            System.out.println("[DEBUG Game.checkDrawCondition] Draw due to 80 ply rule (movesSinceLastCapture=" + movesSinceLastCapture + ")");
            GameDeclareDraw();
            return true;
        }

        Map<Square, Moves> availableMoves = getAllMovesForCurrentPlayer();

        if (availableMoves == null || availableMoves.isEmpty()) {
            System.out.println("[DEBUG Game.checkDrawCondition] Draw due to stalemate (Player " + getCurrentTurn().getPlayerId() + " has no moves).");
            GameDeclareDraw();
            return true;
        }

        return false;
    }



    private Map<Square, Moves> getAllMovesForCurrentPlayer() {
        Player currentPlayer = getCurrentTurn();
        Board currentBoard = getBoard();
        if (currentPlayer == null || currentBoard == null) return new HashMap<>();

        Map<Square, Moves> allPossibleMoves = new HashMap<>();
        ArrayList<Square> playerPieces = rules.getAllPiecesForColor(currentBoard, currentPlayer.getColor());

        for (Square piece : playerPieces) {
            if (piece == null) continue;
            int[] coords = {piece.getRow(), piece.getCol()};
            Moves pieceMoves = rules.getMovesForSquare(currentBoard, currentPlayer.getColor(), coords);
            if (pieceMoves != null && pieceMoves.size() > 0) {
                allPossibleMoves.put(piece, pieceMoves);
            }
        }
        return allPossibleMoves;
    }


    // returning loser as requested by page controller
    public Player getLoser(){
        Player winner = getWinner();
        if (winner != null) {
            return (winner == player1) ? player2 : player1;
        }
        if (player1quit) return player1;
        if (player2quit) return player2;

        return null;
    }

    /* methods to recieve game information */
    //returns who's turn it is
    public Player getCurrentTurn(){return turn ? player1 : player2;}
    //switch player turns
    public void switchTurn(){turn = !turn;}
    //update the board
    public void updateBoard(Board board){this.board = board;}
    //return player1's ID
    public int getPlayer1ID(){return player1.getPlayerId();}
    //return player2's ID
    public int getPlayer2ID(){return player2.getPlayerId();}
    //return player1's name
    public String getPlayer1Name() { return player1Name; }
    //return player2's name
    public String getPlayer2Name() { return player2Name; }
    //return player1's color
    public boolean getPlayer1Color(){return player1.getColor();}
    //return player2's color
    public boolean getPlayer2Color(){return player2.getColor();}
    //return the board
    public Board getBoard(){return board;}
    //return the number of the game
    public int gameNumber(){return gameNumber;}
    //return game activity status
    public boolean gameActive(){return active;}
    //return the number of moves since last capture
    public int getMovesSinceLastCapture(){return movesSinceLastCapture;}
    public void incrementMoveCounter(){movesSinceLastCapture++;}
    //sets lastCapture to 0
    public void newCapture(){movesSinceLastCapture = 0;}
    public boolean isDraw() { return draw; }

    //declare draw and set game to inactive
    public void GameDeclareDraw(){
        if(active) {
            draw = true;
            active = false;
        }
    }


    public void endGame() {
        if(active) {
            active = false;
        }
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
    public int getPlayer1Score(){return player1 != null ? player1.getScore() : 0;}
    public int getPlayer2Score(){return player2 != null ? player2.getScore() : 0;}

    // if quitting is implemented these set the player to quit
    public void Player1Quit(){
        if(active) {
            player1quit = true;
            active = false;
        }
    }

    public void Player2Quit(){
        if(active) {
            player2quit = true;
            active = false;
        }
    }



}

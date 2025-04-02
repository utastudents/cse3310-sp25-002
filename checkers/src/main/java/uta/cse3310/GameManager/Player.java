package uta.cse3310.GameManager;

public class Player {
     private int playerId;
     private int score;
     private int pieceCount;

     public Player(int playerId){
          this.playerId = playerId;
          this.score = 0;
          this.pieceCount = 12;
     }

     public int getPlayerId(){return playerId;}
     public int getScore(){return score;}
     public void addScore(int points){score += points;}
     public int getPieces(){return pieceCount;}
     public void takePieces(int numPieces){pieceCount -= numPieces;}
}

package uta.cse3310.GameManager;

public class Player {
     private int playerId;
     private int score;
     private int pieceCount;
     private boolean color;

     public Player(int playerId, boolean color){
          this.playerId = playerId;
          this.score = 0;
          this.pieceCount = 12;
          this.color = color;
     }

     public int getPlayerId(){return playerId;}
     public int getScore(){return score;}
     public void addScore(int points){score += points;}
     public int getPieces(){return pieceCount;}
     public void takePieces(int numPieces){pieceCount -= numPieces;}
     public void setPieces(int count) {
          if (count >= 0) {
               this.pieceCount = count;
          }
     }
     public boolean getColor(){return color;}
}

package cse3310.uta.GameManagerTests;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import uta.cse3310.GameManager.GameManager;
import uta.cse3310.GameManager.Game;
import uta.cse3310.GameManager.Square;
import uta.cse3310.GameManager.Board;
import uta.cse3310.GameManager.GamePageController;
import uta.cse3310.GameManager.GamePairController;
import uta.cse3310.GameManager.Move;
import uta.cse3310.GameManager.Moves;
import uta.cse3310.GameManager.Player;
import uta.cse3310.PairUp.Match;

public class Requirements_Tests {
  private Square square;
  private Board board;
  private Game game;
  private GameManager gameManager;
  private Player player1;
  private Player player2;
  private Move move;
  private Moves moves;
  @Before
  public void setUp() {
    square = new Square(1, 2);
    board = new Board();
    board.initializeBoard();
    player1 = new Player(1, true); // Player ID 1, color white
    player2 = new Player(2, false); // Player ID 2, color black
    game = new Game(1, 2, true, false, 0); // Game number 0
    gameManager = new GameManager();
    move = new Move(2, 1, 3, 2); // Valid initial move for white
    moves = new Moves();
  }
  @Test
  public void test171_GameStateProgression() {
    gameManager.progressGame(0); // Progress game number 0
    assertTrue(game.gameActive());
  }
  @Test
  public void test172_SwitchTurn() {
    boolean initial = game.getCurrentTurn().getColor();
    game.switchTurn();
    assertNotEquals(initial, game.getCurrentTurn().getColor());
  }
  @Test
  public void test173_MoveAppliedToBoard() {
    Square start = board.getSquare(5, 0);
    Square end = board.getSquare(4, 1);
    Move validMove = new Move(start, end);
    board.execute(validMove, false);
    assertFalse(start.hasPiece());
    assertTrue(end.hasPiece());
  }
  @Test
  public void test174_PlayerAssignedToMatch(){
    GamePairController gpc = new GamePairController();
    Match match = new Match(1, 2,"player1", "player2", false, 1, false, true);
    Game game = gpc.newMatch(match);
    assertNotNull(game);
    assertEquals(1, game.getPlayer1ID());
    assertEquals(2, game.getPlayer2ID());
  }
  @Test
  public void test176_PlayerColorAssignment() {
    assertNotEquals(game.getPlayer1Color(), game.getPlayer2Color());
  }
  @Test
  public void test177_PlayerScoreUpdate() {
    int initialScore = player1.getScore();
    player1.addScore(1);
    assertEquals(initialScore + 1, player1.getScore());
  }
  @Test
  public void test191_IllegalMovesDisapproval(){
    Move illegalMove = new Move(0, 0, 7, 7);
    Game game = gameManager.findGameById(0);
    if (game != null){
      boolean turnBefore=game.getCurrentTurn().getColor();
      gameManager.processMove(1, illegalMove);
      boolean turnAfter = game.getCurrentTurn().getColor();
      assertEquals(turnBefore, turnAfter);
    }
  }
}

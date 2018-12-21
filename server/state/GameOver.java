package server.state;

import static server.network.YahtzeeServer.SYSTEM;

import protocol.FrameExceptions.FrameMsgIdException;
import server.controller.GameController;

/**
 * The type Game over that represents the game over state.
 */
public class GameOver extends ServerState {

  /**
   * Instantiates a new Game over state object.
   *
   * @param controller the controller
   */
  public GameOver(GameController controller) {
    super(controller);
  }

  @Override
  public void playerQuit(int playerId) {
    if (controller.getCurPlayerNumber() == 1) {
      System.out.println(
          SYSTEM + " PLAYER-" + playerId + " quited game. Current player number is " + (
              controller.getCurPlayerNumber() - 1) + ". Game service restart.");
      controller.reset();
    } else {
      System.out.println(
          SYSTEM + " PLAYER-" + playerId + " quited game. Current player number is " + (
              controller.getCurPlayerNumber() - 1) + ".");
      controller.removePlayer(playerId);
    }
  }

  @Override
  public void ackMessage(int playerId, String[] strs) throws FrameMsgIdException {
    controller.matchMsg(playerId, strs);
  }
}
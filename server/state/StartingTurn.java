package server.state;

import protocol.FrameExceptions.FrameMsgIdException;
import server.controller.GameController;

import java.io.IOException;

/**
 * The type Starting turn that represents the starting turn state..
 */
public class StartingTurn extends ServerState {

  /**
   * Instantiates a new Starting turn state object.
   *
   * @param controller the controller
   */
  public StartingTurn(GameController controller) {
    super(controller);
  }

  @Override
  public void ackMessage(int playerId, String[] strs) throws FrameMsgIdException, IOException {
    controller.matchMsg(playerId, strs);
    controller.setState(controller.getRolling());
    controller.getPlayer(playerId).throwDices();
    handleTurnNumber(playerId);
  }
}

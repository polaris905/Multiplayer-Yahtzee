package server.state;

import protocol.FrameExceptions.FrameMsgIdException;
import server.controller.GameController;

import java.io.IOException;

/**
 * The type Rolling that represents the rolling state.
 */
public class Rolling extends ServerState {

  /**
   * Instantiates a new Rolling state object.
   *
   * @param controller the controller
   */
  public Rolling(GameController controller) {
    super(controller);
  }

  @Override
  public void keepDiceMessage(int playerId, String[] strs) throws FrameMsgIdException, IOException {
    controller.matchMsg(playerId, strs);
    int[] keeps = new int[5];
    for (int i = 0; i < 5; i++) {
      keeps[i] = Integer.parseInt(strs[i + 2]);
    }
    controller.getPlayer(playerId).throwDices(keeps);
    handleTurnNumber(playerId);
  }
}

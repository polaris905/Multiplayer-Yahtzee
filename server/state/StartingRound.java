package server.state;

import protocol.FrameExceptions.FrameMsgIdException;
import server.controller.GameController;

/**
 * The type Starting round that represents the starting round state..
 */
public class StartingRound extends ServerState {

  /**
   * Instantiates a new Starting round state object.
   *
   * @param controller the controller
   */
  public StartingRound(GameController controller) {
    super(controller);
  }

  @Override
  public void ackMessage(int playerId, String[] strs) throws FrameMsgIdException {
    controller.matchMsg(playerId, strs);
    if (controller.getUnhandledMsg().isEmpty()) {
      controller.setState(controller.getStartingTurn());
      controller.sendMessageAll("INFO", "PLAYER-" + controller.getCurrentPlayerId() + " turns now.",
          false);
      controller.sendMessage(controller.getCurrentPlayerId(), "START_TURN",
          String.valueOf(controller.getTurn()));
    }
  }
}

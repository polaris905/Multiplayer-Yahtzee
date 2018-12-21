package server.state;

import protocol.FrameExceptions.FrameMsgIdException;
import server.controller.GameController;

/**
 * The type Turn over that represents the turn over state..
 */
public class TurnOver extends ServerState {

  /**
   * Instantiates a new Turn over state object.
   *
   * @param controller the controller
   */
  public TurnOver(GameController controller) {
    super(controller);
  }

  @Override
  public void ackMessage(int playerId, String[] strs) throws FrameMsgIdException {
    controller.matchMsg(playerId, strs);
    if (controller.isRoundOver()) {
      controller.setState(controller.getRoundOver());
      controller.sendMessageAll("ROUND_OVER", String.valueOf(controller.getCurrentRound()));
    } else {
      controller.setState(controller.getStartingTurn());
      controller.switchPlayer();
      controller.sendMessageAll("INFO", "PLAYER-" + controller.getCurrentPlayerId() + " turns now.",
          false);
      controller.sendMessage(controller.getCurrentPlayerId(), "START_TURN",
          String.valueOf(controller.getTurn()));
    }
  }
}

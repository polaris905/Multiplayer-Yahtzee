package server.state;

import protocol.FrameExceptions.FrameMsgIdException;
import server.controller.GameController;

/**
 * The type Round over that represents the round over state..
 */
public class RoundOver extends ServerState {

  /**
   * Instantiates a new Round over state object.
   *
   * @param controller the controller
   */
  public RoundOver(GameController controller) {
    super(controller);
  }

  @Override
  public void ackMessage(int playerId, String[] strs) throws FrameMsgIdException {
    controller.matchMsg(playerId, strs);
    if (controller.getUnhandledMsg().isEmpty()) {
      if (controller.isGameOver()) {
        controller.setState(controller.getGameOver());
        controller.sendMessageAll("GAME_OVER", controller.getAllPlayersScore());
      } else {
        controller.setState(controller.getStartingRound());
        controller.nextRound();
        controller.sendMessageAll("INFO", controller.getAllPlayersScore(), false);
        controller.sendMessageAll("START_ROUND", String.valueOf(controller.getCurrentRound()));
      }
    }
  }
}


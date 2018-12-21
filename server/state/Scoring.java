package server.state;

import protocol.FrameExceptions.FrameMsgIdException;
import server.controller.GameController;

/**
 * The type Scoring that represents the scoring state..
 */
public class Scoring extends ServerState {

  /**
   * Instantiates a new Scoring state object.
   *
   * @param controller the controller
   */
  public Scoring(GameController controller) {
    super(controller);
  }

  @Override
  public void scoreChoiceMessage(int playerId, String[] strs) throws FrameMsgIdException {
    controller.matchMsg(playerId, strs);
    String scoreName = strs[2];
    try {
      controller.getPlayer(playerId).chooseScoreSlot(scoreName);
      controller.sendMessage(playerId, "SCORE_CHOICE_VALID",
          controller.getPlayer(playerId).getScoresString(), false);
      controller.sendMessageOther(playerId, "INFO",
          "PLAYER-" + playerId + " chooses score " + controller.getPlayer(playerId)
              .getScoresString(), false);
      controller.setState(controller.getTurnOver());
      controller.sendMessage(playerId, "TURN_OVER");
    } catch (IllegalArgumentException ex) {
      controller.sendMessage(playerId, "SCORE_CHOICE_INVALID",
          controller.getPlayer(playerId).getPossibleScoresString());
    }
  }
}

package client.state;

import client.controller.GameController;

/**
 * The type Rolling that represents the rolling state.
 */
public class Rolling extends ClientState {

  /**
   * Instantiates a new Rolling state object.
   *
   * @param controller the controller
   */
  public Rolling(GameController controller) {
    super(controller);
  }

  @Override
  public void keepDice(int msgId, String message) {
    controller.matchMsgId(msgId);
    controller.setState(controller.getStaying());
    controller.sendMessage(message);
  }
}

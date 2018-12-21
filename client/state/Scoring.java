package client.state;

import client.controller.GameController;

/**
 * The type Scoring that represents the scoring state.
 */
public class Scoring extends ClientState {

  /**
   * Instantiates a new Scoring state object.
   *
   * @param controller the controller
   */
  public Scoring(GameController controller) {
    super(controller);
  }

  @Override
  public void scoreChoice(int msgId, String message) {
    controller.matchMsgId(msgId);
    controller.setState(controller.getStaying());
    controller.sendMessage(message);
  }
}

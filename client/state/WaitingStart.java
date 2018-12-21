package client.state;

import client.controller.GameController;

/**
 * The type Waiting start that represents the waiting start state.
 */
public class WaitingStart extends ClientState {

  /**
   * Instantiates a new Waiting start state object.
   *
   * @param controller the controller
   */
  public WaitingStart(GameController controller) {
    super(controller);
  }

  @Override
  public void sendAck(int msgId, String message) {
    controller.matchMsgId(msgId);
    controller.setState(controller.getStaying());
    controller.sendMessage(String.join(" ", message));
    if (!controller.getUnhandledMsg().empty()) {
      controller.processServerMsg(controller.getUnhandledMsg().pop());
    }
  }

  @Override
  public void gameOver(String message) {
    controller.getUnhandledMsg().clear();
    controller.sendMessage(message);
  }

  @Override
  public void retrieveMsg() {
  }
}

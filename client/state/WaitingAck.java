package client.state;

import client.controller.GameController;

/**
 * The type Waiting ack that represents the wating ack state.
 */
public class WaitingAck extends ClientState {

  /**
   * Instantiates a new Waiting ack state object.
   *
   * @param controller the controller
   */
  public WaitingAck(GameController controller) {
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
}

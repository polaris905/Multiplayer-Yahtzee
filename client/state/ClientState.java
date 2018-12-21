package client.state;

import client.controller.GameController;
import protocol.FrameExceptions.FrameMsgIdException;

/**
 * The type Client state that represents different behaviours of the Client under different state.
 */
public abstract class ClientState {

  /**
   * The Controller object of the Client GameController.
   */
  protected GameController controller;

  /**
   * Instantiates a new Client state.
   *
   * @param controller the controller
   */
  public ClientState(GameController controller) {
    this.controller = controller;
  }

  /**
   * Send ack message.
   *
   * @param msgId the msg id
   * @param message the message
   * @throws FrameMsgIdException the frame msg id exception
   */
  public void sendAck(int msgId, String message) throws FrameMsgIdException {
    controller.showClientMessage("You can not send this request in current state.");
  }

  /**
   * Send print game state message.
   *
   * @param message the message
   */
  public void printGameState(String message) {
    controller.setState(controller.getStaying());
    controller.sendMessage(message);
  }

  /**
   * Send game over message.
   *
   * @param message the message
   */
  public void gameOver(String message) {
    controller.setState(controller.getStaying());
    controller.sendMessage(message);
  }

  /**
   * Send keep dice message.
   *
   * @param msgId the msg id
   * @param message the message
   * @throws FrameMsgIdException the frame msg id exception
   */
  public void keepDice(int msgId, String message) throws FrameMsgIdException {
    controller.showClientMessage("You can not send this request in current state.");
  }

  /**
   * send score choice message.
   *
   * @param msgId the msg id
   * @param message the message
   * @throws FrameMsgIdException the frame msg id exception
   */
  public void scoreChoice(int msgId, String message) throws FrameMsgIdException {
    controller.showClientMessage("You can not send this request in current state.");
  }

  /**
   * Send retrieve last msg message.
   */
  public void retrieveMsg() {
    controller.retrieveMsg();
  }
}

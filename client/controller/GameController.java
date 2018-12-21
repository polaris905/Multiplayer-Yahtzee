package client.controller;

import client.state.ClientState;
import client.state.Rolling;
import client.state.Scoring;
import client.state.Staying;
import client.state.WaitingAck;
import client.state.WaitingStart;
import client.ui.ClientUi;
import protocol.FrameExceptions;
import protocol.FrameExceptions.FrameMsgIdException;

import java.io.PrintWriter;
import java.util.Stack;

/**
 * The type Game controller that using "State Pattern" to control the whole logic operation of the
 * client application.
 */
public class GameController {

  private ClientUi clientUi;
  private PrintWriter output;
  private ClientState waitingStart = new WaitingStart(this);
  private ClientState staying = new Staying(this);
  private ClientState waitingAck = new WaitingAck(this);
  private ClientState rolling = new Rolling(this);
  private ClientState scoring = new Scoring(this);
  private ClientState state = waitingAck;
  private Stack<String> unHandledMsg = new Stack<>();

  /**
   * Instantiates a new Game controller.
   *
   * @param clientUi the client ui
   * @param output the output object
   */
  public GameController(ClientUi clientUi, PrintWriter output) {
    this.clientUi = clientUi;
    this.output = output;
  }

  /**
   * Process server message.
   *
   * @param message the message
   */
  public void processServerMsg(String message) {
    clientUi.showServerMessage(message);
    message.replace("(RETRIEVE)", "");
    String[] strs = message.split("\\s");
    String frame = strs[1];
    switch (frame) {
      case "START_GAME":
        unHandledMsg.clear();
        state = waitingStart;
        break;
      case "START_ROUND":
        state = waitingAck;
        break;
      case "START_TURN":
        state = waitingAck;
        break;
      case "CHOOSE_DICE":
        state = rolling;
        break;
      case "INVALID_DICE_CHOICE":
        state = rolling;
        break;
      case "CHOOSE_SCORE":
        state = scoring;
        break;
      case "SCORE_CHOICE_INVALID":
        state = scoring;
        break;
      case "SCORE_CHOICE_VALID":
        state = waitingAck;
        break;
      case "TURN_OVER":
        state = waitingAck;
        break;
      case "ROUND_OVER":
        state = waitingAck;
        break;
      case "GAME_OVER":
        state = waitingAck;
        break;
      case "INFO":
        state.retrieveMsg();
        break;
      case "GAME_STATE":
        state.retrieveMsg();
        break;
      case "ACK":
        state.retrieveMsg();
        break;
      default:
        break;
    }
    if (!frame.equals("ACK") && !frame.equals("INFO") && !frame.equals("SCORE_CHOICE_VALID")
        && !frame.equals("GAME_STATE")) {
      unHandledMsg.push(message);
    }
  }

  /**
   * Process client message.
   *
   * @param message the message
   * @throws FrameExceptions the frame exceptions
   */
  public void processClientMsg(String message) throws FrameExceptions {
    String[] strs = message.split("\\s");
    String frame = strs[0];
    int msgId = Integer.parseInt(strs[1]);
    switch (frame) {
      case "ACK":
        state.sendAck(msgId, message);
        break;
      case "KEEP_DICE":
        state.keepDice(msgId, message);
        break;
      case "SCORE_CHOICE":
        state.scoreChoice(msgId, message);
        break;
      case "PRINT_GAME_STATE":
        state.printGameState(message);
        break;
      case "QUIT_GAME":
        state.gameOver(message);
        break;
      default:
        break;
    }
  }

  /**
   * Retrieve last unhandled message.
   */
  public void retrieveMsg() {
    if (!unHandledMsg.empty()) {
      processServerMsg("(RETRIEVE)" + unHandledMsg.pop().replace("(RETRIEVE)", ""));
    }
  }

  /**
   * Gets current state.
   *
   * @return the state
   */
  public ClientState getState() {
    return state;
  }

  /**
   * Gets staying state.
   *
   * @return the staying
   */
  public ClientState getStaying() {
    return staying;
  }

  /**
   * Sets state.
   *
   * @param state the state
   */
  public void setState(ClientState state) {
    this.state = state;
  }

  /**
   * Send message to the server.
   *
   * @param message the message
   */
  public void sendMessage(String message) {
    output.println(message);
  }

  /**
   * Show client message by calling the ui.
   *
   * @param message the message
   */
  public void showClientMessage(String message) {
    clientUi.showClientMessage(message);
  }

  /**
   * Gets unhandled message stack.
   *
   * @return the unhandled msg
   */
  public Stack<String> getUnhandledMsg() {
    return unHandledMsg;
  }

  /**
   * Match message id.
   *
   * @param msgId the message id
   * @throws FrameMsgIdException the frame msg id exception
   */
  public void matchMsgId(int msgId) throws FrameMsgIdException {
    if (!unHandledMsg.isEmpty()) {
      int lastUnhandledMsgId = Integer.parseInt(unHandledMsg.peek().split("\\s")[2]);
      if (msgId == lastUnhandledMsgId) {
        unHandledMsg.pop();
      } else {
        throw new FrameMsgIdException("Message ID does not match.");
      }
    }
  }
}

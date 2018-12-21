package server.state;

import static server.network.YahtzeeServer.SYSTEM;

import protocol.FrameExceptions.FrameMsgIdException;
import server.controller.GameController;

import java.io.IOException;

/**
 * The type Server state that represents different behaviours of the Server under different state.
 */
public abstract class ServerState {

  /**
   * The Server game controller.
   */
  protected GameController controller;

  /**
   * Instantiates a new Server state.
   *
   * @param controller the controller
   */
  public ServerState(GameController controller) {
    this.controller = controller;
  }

  /**
   * Handle player quit behavior.
   *
   * @param playerId the player id
   */
  public void playerQuit(int playerId) {
    System.out.println(
        SYSTEM + " PLAYER-" + playerId + " quited game. Current player number is " + (
            controller.getCurPlayerNumber() - 1) + ".");
    controller.sendMessageAll("GAME_OVER", "PLAYER-" + playerId + " quited game.");
    if (controller.getLatch().getCount() <= 1) {
      System.out.println(SYSTEM + " PLAYER-" + playerId + " quited game. Game service restart.");
      controller.reset();
    } else {
      controller.setState(controller.getGameOver());
      controller.removePlayer(playerId);
    }
  }

  /**
   * Handle ack message.
   *
   * @param playerId the player id
   * @param strs the message
   * @throws FrameMsgIdException the frame msg id exception
   * @throws IOException the io exception
   */
  public void ackMessage(int playerId, String[] strs) throws FrameMsgIdException, IOException {
    controller
        .sendMessage(playerId, "INFO", "You can not send this request in current state.", false);
  }

  /**
   * Handle keep dice message.
   *
   * @param playerId the player id
   * @param strs the message
   * @throws FrameMsgIdException the frame msg id exception
   * @throws IOException the io exception
   */
  public void keepDiceMessage(int playerId, String[] strs) throws FrameMsgIdException, IOException {
    controller
        .sendMessage(playerId, "INFO", "You can not send this request in current state.", false);
  }

  /**
   * Handle score choice message.
   *
   * @param playerId the player id
   * @param strs the message
   * @throws FrameMsgIdException the frame msg id exception
   */
  public void scoreChoiceMessage(int playerId, String[] strs) throws FrameMsgIdException {
    controller
        .sendMessage(playerId, "INFO", "You can not send this request in current state.", false);
  }

  /**
   * Handle game state message.
   *
   * @param playerId the player id
   * @param strs the message
   */
  public void gameStateMessage(int playerId, String[] strs) {
    controller.sendMessage(playerId, "GAME_STATE", Integer.parseInt(strs[1]),
        controller.getState() + " " + controller.getAllPlayersScore());
  }

  /**
   * Handle quit game message.
   *
   * @param playerId the player id
   * @param strs the message
   */
  public void quitGameMessage(int playerId, String[] strs) {
    controller.setLastRound();
    controller
        .sendMessage(playerId, "ACK", Integer.parseInt(strs[1]), "OK! Changing to the last round.");
    controller.sendMessageOther(playerId, "INFO",
        "PLAYER-" + playerId + " sent quit game request. Changing to the last round.", false);
  }

  /**
   * Check and handle current turn number.
   *
   * @param playerId the player id
   * @throws IOException the io exception
   */
  public void handleTurnNumber(int playerId) throws IOException {
    if (!controller.isTurnOver()) {
      controller.addTurn();
      controller.sendMessage(playerId, "CHOOSE_DICE", controller.getPlayer(playerId).showDices());
      controller.sendMessageOther(playerId, "INFO",
          "PLAYER-" + playerId + " throws dice " + controller.getPlayer(playerId).showDices(),
          false);
    } else {
      controller.setState(controller.getScoring());
      controller
          .sendMessage(playerId, "INFO", "FINAL_DICE " + controller.getPlayer(playerId).showDices(),
              false);
      controller.sendMessageOther(playerId, "INFO",
          "PLAYER-" + playerId + " throws dice " + controller.getPlayer(playerId).showDices(),
          false);
      controller.sendMessage(playerId, "CHOOSE_SCORE",
          controller.getPlayer(playerId).getPossibleScoresString());
    }
  }
}

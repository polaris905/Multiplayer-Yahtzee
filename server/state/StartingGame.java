package server.state;

import static server.network.YahtzeeServer.SYSTEM;

import protocol.FrameExceptions.FrameMsgIdException;
import server.controller.GameController;

/**
 * The type Starting game that represents the starting game state..
 */
public class StartingGame extends ServerState {

  /**
   * Instantiates a new Starting game stat object.
   *
   * @param controller the controller
   */
  public StartingGame(GameController controller) {
    super(controller);
  }

  @Override
  public void playerQuit(int playerId) {
    controller.getPlayers().remove(playerId);
    controller.decreaseCurPlayerNumber();
    System.out.println(
        SYSTEM + " PLAYER-" + playerId + " quited game. Current player number is " + controller
            .getCurPlayerNumber() + ".");
    if (controller.getCurPlayerNumber() == 0) {
      controller.reset();
      return;
    }
    controller.sendMessageAll("INFO",
        "PLAYER-" + playerId + " quited game. Current player number is " + controller
            .getCurPlayerNumber() + ".", false);
    controller.clearMsg();
    controller.sendMessageAll("START_GAME", true);
    controller.deleteMsg(playerId);
    controller.clearLatch();
  }

  @Override
  public void ackMessage(int playerId, String[] strs) throws FrameMsgIdException {
    controller.matchMsg(playerId, strs);
    if (controller.getUnhandledMsg().isEmpty()) {
        controller.setUpLatch();
      controller.setState(controller.getStartingRound());
      controller.sendMessageAll("START_ROUND", String.valueOf(controller.getCurrentRound()));
    }
  }

  @Override
  public void quitGameMessage(int playerId, String[] strs) {
    controller.setState(controller.getGameOver());
    controller.setUpLatch();
    controller.sendMessage(playerId, "ACK", Integer.parseInt(strs[1]), "OK!");
    controller
        .sendMessageOther(playerId, "INFO", "PLAYER-" + playerId + " sent quit game request. ",
            false);
    controller.sendMessageAll("GAME_OVER", controller.getAllPlayersScore());
  }
}

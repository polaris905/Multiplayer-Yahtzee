package server.controller;

import static server.network.YahtzeeServer.SERVER;

import protocol.FrameExceptions.FrameMsgIdException;
import server.game.Player;
import server.state.GameOver;
import server.state.Rolling;
import server.state.RoundOver;
import server.state.Scoring;
import server.state.ServerState;
import server.state.StartingGame;
import server.state.StartingRound;
import server.state.StartingTurn;
import server.state.TurnOver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * The type Game controller that using "State Pattern" to control the whole logic operation of the
 * server application.
 */
public class GameController {

  private int maxPlayer;
  private int maxRound;
  private int maxTurn = 3;
  private int msgId = 1;
  private int currentRound = 1;
  private int currentPlayerId = 1;
  private int countInCurrentRound = 1;
  private int turn = 1;
  private int curPlayerNumber = 0;
  private int lastPlayerId = 1;
  private ServerState startingGame = new StartingGame(this);
  private ServerState startingRound = new StartingRound(this);
  private ServerState startingTurn = new StartingTurn(this);
  private ServerState rolling = new Rolling(this);
  private ServerState scoring = new Scoring(this);
  private ServerState turnOver = new TurnOver(this);
  private ServerState roundOver = new RoundOver(this);
  private ServerState gameOver = new GameOver(this);
  private ServerState state = startingGame;
  private Deque<String> unhandledMsg = new LinkedList<>();
  private Map<Integer, Player> players = new HashMap<>();
  private CountDownLatch latch;


  /**
   * Instantiates a new Game controller object.
   *
   * @param maxPlayer the max number of players in a game
   * @param maxRound the max round in a game
   */
  public GameController(int maxPlayer, int maxRound) {
    this.maxPlayer = maxPlayer;
    this.maxRound = maxRound;
  }

  /**
   * Process clients' request.
   *
   * @param playerId the player id
   * @param request the request
   * @throws IOException the io exception
   */
  public void processRequest(int playerId, String request) throws IOException {
    String[] strs = request.split("\\s");
    String frame = strs[0];
    try {
      switch (frame) {
        case "ACK":
          state.ackMessage(playerId, strs);
          break;
        case "KEEP_DICE":
          state.keepDiceMessage(playerId, strs);
          break;
        case "SCORE_CHOICE":
          state.scoreChoiceMessage(playerId, strs);
          break;
        case "PRINT_GAME_STATE":
          state.gameStateMessage(playerId, strs);
          break;
        case "QUIT_GAME":
          state.quitGameMessage(playerId, strs);
          break;
        default:
          break;
      }
    } catch (FrameMsgIdException ex) {
      sendMessageNoId(playerId, "Response message ID does not match.");
    }
  }

  /**
   * Gets starting game state.
   *
   * @return the starting game
   */
  public ServerState getStartingGame() {
    return startingGame;
  }

  /**
   * Gets rolling state.
   *
   * @return the rolling
   */
  public ServerState getRolling() {
    return rolling;
  }

  /**
   * Gets scoring state.
   *
   * @return the scoring
   */
  public ServerState getScoring() {
    return scoring;
  }

  /**
   * Gets turn over state.
   *
   * @return the turn over
   */
  public ServerState getTurnOver() {
    return turnOver;
  }

  /**
   * Gets starting round state.
   *
   * @return the starting round
   */
  public ServerState getStartingRound() {
    return startingRound;
  }

  /**
   * Gets starting turn state.
   *
   * @return the starting turn
   */
  public ServerState getStartingTurn() {
    return startingTurn;
  }

  /**
   * Gets round over state.
   *
   * @return the round over
   */
  public ServerState getRoundOver() {
    return roundOver;
  }

  /**
   * Gets game over state.
   *
   * @return the game over
   */
  public ServerState getGameOver() {
    return gameOver;
  }

  /**
   * Gets current state.
   *
   * @return the state
   */
  public ServerState getState() {
    return state;
  }

  /**
   * Sets state.
   *
   * @param state the state
   */
  public void setState(ServerState state) {
    this.state = state;
  }

  /**
   * Sets up CountDownLatch.
   */
  public void setUpLatch() {
    clearLatch();
    latch = new CountDownLatch(curPlayerNumber);
  }

  /**
   * Reset the controller to start new game.
   */
  public void reset() {
    msgId = 1;
    currentRound = 1;
    countInCurrentRound = 1;
    currentPlayerId = 1;
    lastPlayerId = 1;
    turn = 1;
    state = startingGame;
    unhandledMsg.clear();
    players.clear();
    curPlayerNumber = 0;
    clearLatch();
  }

  /**
   * Clear the CountDownLatch.
   */
  public void clearLatch() {
    if (latch != null) {
      while (latch.getCount() != 0) {
        latch.countDown();
      }
    }
  }

  /**
   * Gets max number of players.
   *
   * @return the max player number
   */
  public int getMaxPlayer() {
    return maxPlayer;
  }

  /**
   * Gets current number of players.
   *
   * @return the current player number
   */
  public int getCurPlayerNumber() {
    return curPlayerNumber;
  }

  /**
   * Add current player number.
   */
  public void addCurPlayerNumber() {
    curPlayerNumber++;
    lastPlayerId++;
  }

  /**
   * Decrease current player number.
   */
  public void decreaseCurPlayerNumber() {
    curPlayerNumber--;
  }

  /**
   * Gets last player id.
   *
   * @return the last player id
   */
  public int getLastPlayerId() {
    return lastPlayerId;
  }

  /**
   * Gets the CountDownLatch.
   *
   * @return the CountDownLatch
   */
  public CountDownLatch getLatch() {
    return latch;
  }

  /**
   * Add new player.
   *
   * @param playerId the player id
   * @param output the output object
   */
  public void addPlayer(int playerId, PrintWriter output) {
    players.put(playerId, new Player(playerId, output));
  }

  /**
   * Remove player by ID.
   *
   * @param playerId the player id
   */
  public void removePlayer(int playerId) {
    players.remove(playerId);
    deleteMsg(playerId);
    curPlayerNumber--;
    if (latch != null) {
      latch.countDown();
    }
  }

  /**
   * Gets current state string.
   *
   * @return the current state string
   */
  public String getCurrentStateString() {
    return "Round: " + currentRound + " Turn: PLAYER-" + currentPlayerId + " STATE: " + state;
  }

  /**
   * Gets unhandled messages queue.
   *
   * @return the unhandled messages queue
   */
  public Deque<String> getUnhandledMsg() {
    return unhandledMsg;
  }

  /**
   * Gets player object by ID.
   *
   * @param playerId the player id
   * @return the player object
   */
  public Player getPlayer(int playerId) {
    return players.get(playerId);
  }

  /**
   * Gets current player id.
   *
   * @return the current player id
   */
  public int getCurrentPlayerId() {
    while (!players.containsKey(currentPlayerId)) {
      currentPlayerId++;
    }
    return currentPlayerId;
  }

  /**
   * Gets players collections.
   *
   * @return the players map object
   */
  public Map<Integer, Player> getPlayers() {
    return players;
  }

  /**
   * Gets the turn number.
   *
   * @return the turn number
   */
  public int getTurn() {
    return turn;
  }

  /**
   * Check if the game should be over.
   *
   * @return true or false
   */
  public boolean isGameOver() {
    return currentRound >= maxRound && isRoundOver();
  }

  /**
   * Check if the round should be over.
   *
   * @return true or false
   */
  public boolean isRoundOver() {
    return countInCurrentRound >= players.size();
  }

  /**
   * Sets to the last round.
   */
  public void setLastRound() {
    currentRound = maxRound;
  }

  /**
   * Check if the turn should be over.
   *
   * @return true or false
   */
  public boolean isTurnOver() {
    return turn >= maxTurn;
  }

  /**
   * Switch to next player.
   */
  public void switchPlayer() {
    turn = 1;
    countInCurrentRound++;
    while (!players.containsKey(++currentPlayerId)) {
      ;
    }
  }

  /**
   * Switch to next round.
   */
  public void nextRound() {
    turn = 1;
    currentPlayerId = 1;
    countInCurrentRound = 1;
    currentRound++;
  }

  /**
   * Gets current round number.
   *
   * @return the current round
   */
  public int getCurrentRound() {
    return currentRound;
  }

  /**
   * Gets new message id.
   *
   * @return the message id
   */
  public int getMsgId() {
    return msgId++;
  }

  /**
   * Add turn.
   */
  public void addTurn() {
    turn++;
  }

  /**
   * Clear unhandled messages queue.
   */
  public void clearMsg() {
    unhandledMsg.clear();
  }

  /**
   * Gets all players score string.
   *
   * @return the all players score
   */
  public String getAllPlayersScore() {
    StringBuilder builder = new StringBuilder();
    for (Player player : players.values()) {
      builder.append("PLAYER-" + player.getPlayerId() + ": " + player.getScoresString());
    }
    if (builder.length() > 0) {
      builder.setLength(builder.length() - 1);
    }
    return builder.toString();
  }

  /**
   * Send message to a player.
   *
   * @param playerId the player id
   * @param frame the frame
   * @param content the content
   * @param responseRequired the response required
   */
  public void sendMessage(int playerId, String frame, String content, boolean responseRequired) {
    if (responseRequired) {
      String message = SERVER + " " + frame + " " + getMsgId() + " " + content + " <NEED RESPONSE>";
      getPlayer(playerId).getOutput().println(message);
      unhandledMsg.addLast(playerId + " " + message);
    } else {
      String message = SERVER + " " + frame + " " + getMsgId() + " " + content;
      getPlayer(playerId).getOutput().println(message);
    }
  }

  /**
   * Send message to a player.
   *
   * @param playerId the player id
   * @param frame the frame
   * @param msgId the msg id
   * @param content the content
   */
  public void sendMessage(int playerId, String frame, int msgId, String content) {
    String message = SERVER + " " + frame + " " + msgId + " " + content;
    getPlayer(playerId).getOutput().println(message);
  }

  /**
   * Send message to a player.
   *
   * @param playerId the player id
   * @param frame the frame
   */
  public void sendMessage(int playerId, String frame) {
    sendMessage(playerId, frame, true);
  }

  /**
   * Send message to a player.
   *
   * @param playerId the player id
   * @param frame the frame
   * @param content the content
   */
  public void sendMessage(int playerId, String frame, String content) {
    sendMessage(playerId, frame, content, true);
  }

  /**
   * Send message to a player.
   *
   * @param playerId the player id
   * @param frame the frame
   * @param responseRequired the response required
   */
  public void sendMessage(int playerId, String frame, boolean responseRequired) {
    if (responseRequired) {
      String message = SERVER + " " + frame + " " + getMsgId() + " <NEED RESPONSE>";
      System.out.println(message);
      getPlayer(playerId).getOutput().println(message);
      unhandledMsg.addLast(playerId + " " + message);
    } else {
      String message = SERVER + " " + frame + " " + getMsgId();
      System.out.println(message);
      getPlayer(playerId).getOutput().println(message);
    }
  }

  /**
   * Send message to all players.
   *
   * @param frame the frame
   * @param content the content
   * @param responseRequired the response required
   */
  public void sendMessageAll(String frame, String content, boolean responseRequired) {
    if (responseRequired) {
      for (Player player : players.values()) {
        String message =
            SERVER + " " + frame + " " + getMsgId() + " " + content + " <NEED RESPONSE>";
        System.out.println(message);
        player.getOutput().println(message);
        unhandledMsg.addLast(player.getPlayerId() + " " + message);
      }
    } else {
      for (Player player : players.values()) {
        String message = SERVER + " " + frame + " " + getMsgId() + " " + content;
        System.out.println(message);
        player.getOutput().println(message);
      }
    }
  }

  /**
   * Send message to all players.
   *
   * @param frame the frame
   * @param content the content
   */
  public void sendMessageAll(String frame, String content) {
    sendMessageAll(frame, content, true);
  }


  /**
   * Send message to all players.
   *
   * @param frame the frame
   * @param responseRequired the response required
   */
  public void sendMessageAll(String frame, boolean responseRequired) {
    if (responseRequired) {
      for (Player player : players.values()) {
        String message = SERVER + " " + frame + " " + getMsgId() + " <NEED RESPONSE>";
        System.out.println(message);
        player.getOutput().println(message);
        unhandledMsg.addLast(player.getPlayerId() + " " + message);
      }
    } else {
      for (Player player : players.values()) {
        String message = SERVER + " " + frame + " " + getMsgId();
        System.out.println(message);
        player.getOutput().println(message);
      }
    }
  }

  /**
   * Send message to all players except for the given player.
   *
   * @param playerId the player id
   * @param frame the frame
   * @param content the content
   * @param responseRequired the response required
   */
  public void sendMessageOther(int playerId, String frame, String content,
      boolean responseRequired) {
    if (responseRequired) {
      for (Player player : players.values()) {
        if (player.getPlayerId() != playerId) {
          String message =
              SERVER + " " + frame + " " + getMsgId() + " " + content + " <NEED RESPONSE>";
          System.out.println(message);
          player.getOutput().println(message);
          unhandledMsg.addLast(player.getPlayerId() + " " + message);
        }
      }
    } else {
      for (Player player : players.values()) {
        if (player.getPlayerId() != playerId) {
          String message = SERVER + " " + frame + " " + getMsgId() + " " + content;
          System.out.println(message);
          player.getOutput().println(message);
        }
      }
    }
  }

  /**
   * Send message to a player without using message ID.
   *
   * @param playerId the player id
   * @param content the content
   */
  public void sendMessageNoId(int playerId, String content) {
    getPlayer(playerId).getOutput().println(content);
  }

  /**
   * Match the player's message with unhandled messages queue.
   *
   * @param playerId the player id
   * @param message the message
   * @throws FrameMsgIdException the frame msg id exception
   */
  public synchronized void matchMsg(int playerId, String[] message) throws FrameMsgIdException {
    int msgId = Integer.parseInt(message[1]);
    for (int i = 0; i < unhandledMsg.size(); i++) {
      String[] historyMsg = unhandledMsg.getLast().split("\\s");
      int historyTo = Integer.parseInt(historyMsg[0]);
      int historyId = Integer.parseInt(historyMsg[3]);
      if (playerId == historyTo && msgId == historyId) {
        unhandledMsg.removeLast();
        return;
      }
      unhandledMsg.addFirst(unhandledMsg.removeLast());
    }
    throw new FrameMsgIdException("Message ID does not match.");
  }

  /**
   * Delete unhandled message of the given player.
   *
   * @param playerId the player id
   */
  public synchronized void deleteMsg(int playerId) {
    for (int i = 0; i < unhandledMsg.size(); i++) {
      String[] historyMsg = unhandledMsg.getLast().split("\\s");
      int historyTo = Integer.parseInt(historyMsg[0]);
      if (playerId == historyTo) {
        unhandledMsg.removeLast();
      } else {
        unhandledMsg.addFirst(unhandledMsg.removeLast());
      }
    }
  }
}

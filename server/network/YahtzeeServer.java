package server.network;

import static java.nio.charset.StandardCharsets.UTF_8;

import server.controller.GameController;
import server.state.StartingGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Yahtzee server that used to manage the network connection of the Server.
 */
public class YahtzeeServer {

  /**
   * The constant SERVER.
   */
  public static final String SERVER = "[SERVER]";
  /**
   * The constant SYSTEM.
   */
  public static final String SYSTEM = "[SYSTEM]";
  /**
   * The constant CLIENT.
   */
  public static final String CLIENT = "[CLIENT]";
  private int port;
  private ExecutorService service;
  private GameController controller;
  private BlockingQueue<Socket> queue = new ArrayBlockingQueue<>(50);

  /**
   * Instantiates a new Yahtzee server.
   *
   * @param port the port
   * @param maxRound the max round
   * @param maxPlayer the max player
   */
  public YahtzeeServer(int port, int maxRound, int maxPlayer) {
    this.port = port;
    service = Executors.newFixedThreadPool(maxPlayer);
    controller = new GameController(maxPlayer, maxRound);
  }

  /**
   * Sets up network. This thread always handle's new connect requests from clients and put the
   * request into a queue.
   */
  public void setUpNetwork() {
    try (ServerSocket serverSocket = new ServerSocket(port);) {
      System.out.println("Server setups successfully.");
      new Thread(new JoinRequestHandler()).start();
      while (true) {
        Socket clientSocket = serverSocket.accept();
        queue.put(clientSocket);
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }

  /**
   * The type Join request handler. This thread used to decide whether take a client request from
   * the queue to join the game. The basic rule is that if and only if the game is in the starting
   * status AND the current player number is less than the max players number, the client request
   * should be accepted and open a new thread for the client.
   */
  public class JoinRequestHandler implements Runnable {

    @Override
    public void run() {
      try {
        while (true) {
          if (controller.getCurPlayerNumber() < controller.getMaxPlayer()) {
            Socket socket = queue.take();
            if (!(controller.getState() instanceof StartingGame)) {
              controller.getLatch().await();
            }
            ClientHandler client = new ClientHandler(controller.getLastPlayerId(), socket,
                controller);
            controller.addCurPlayerNumber();
            service.execute(client);
            Thread.sleep(100);
          } else {
            controller.setUpLatch();
            controller.getLatch().await();
            controller.setState(controller.getStartingGame());
          }
        }
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * The type Client handler. This thread used to communicate with a special client.
   */
  public static class ClientHandler implements Runnable {

    private int playerId;
    private Socket socket;
    private GameController controller;

    /**
     * Instantiates a new Client handler.
     *
     * @param playerId the player id
     * @param socket the socket
     * @param controller the controller
     */
    public ClientHandler(int playerId, Socket socket, GameController controller) {
      this.playerId = playerId;
      this.socket = socket;
      this.controller = controller;
    }

    @Override
    public void run() {
      try (PrintWriter output = new PrintWriter(socket.getOutputStream(),
          true); BufferedReader input = new BufferedReader(
          new InputStreamReader(socket.getInputStream(), UTF_8))) {
        System.out.println(
            SYSTEM + " New client connected: " + socket.getInetAddress() + ": " + socket.getPort());
        controller.addPlayer(playerId, output);
        controller.sendMessage(playerId, "INFO",
            "You joined the game now. Your name is PLAYER-" + playerId + ". Please enjoy yourself.",
            false);
        controller.sendMessageAll("INFO",
            "PLAYER-" + playerId + " joined game. Current player number is " + controller
                .getCurPlayerNumber() + ".", false);
        controller.getUnhandledMsg().clear();
        controller.sendMessageAll("START_GAME", true);
        String message;
        while ((message = input.readLine()) != null) {
          System.out.println(SYSTEM + " Current state: " + controller.getCurrentStateString());
          System.out.println(CLIENT + " PLAYER-" + playerId + " " + message);
          controller.processRequest(playerId, message);
        }
        controller.getState().playerQuit(playerId);
      } catch (IOException ex) {
        controller.getState().playerQuit(playerId);
      } finally {
        try {
          socket.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
  }
}
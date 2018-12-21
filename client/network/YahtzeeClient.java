package client.network;

import static java.nio.charset.StandardCharsets.UTF_8;

import client.controller.GameController;
import client.ui.ClientUi;
import protocol.ClientProtocol;
import protocol.FrameExceptions;
import protocol.ProtocolParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Yahtzee client that used to manage the network connection of the Client.
 */
public class YahtzeeClient {

  private ClientUi clientUi;
  private String host;
  private int port;
  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private ProtocolParser parser = new ProtocolParser(ClientProtocol.FRAMES);
  private boolean readyStop;
  private GameController controller;

  /**
   * Instantiates a new Yahtzee client network.
   *
   * @param clientUi the client ui
   * @param host the host
   * @param port the port
   */
  public YahtzeeClient(ClientUi clientUi, String host, int port) {
    this.clientUi = clientUi;
    this.host = host;
    this.port = port;
  }

  /**
   * Connect server. This thread always receive the message from the server.
   */
  public void connectServer() {
    try (Socket socket = new Socket(host, port);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {
      clientUi.showNetworkStatus("Connecting Server and waiting for new game.");
      controller = new GameController(clientUi, output);
      executor.execute(new MessageSendingHandler());
      String message;
      while ((message = input.readLine()) != null) {
        controller.processServerMsg(message);
        if (message.split("\\s")[1].equals("GAME_OVER")) {
          readyStop = true;
          break;
        }
      }
      executor.shutdown();
      while (!executor.isTerminated()) {
        executor.awaitTermination(3, TimeUnit.SECONDS);
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      System.out.println("Connection break off.");
      System.exit(0);
    }
  }

  /**
   * The type Message sending handler that used to handling user's input.
   */
  public class MessageSendingHandler implements Runnable {

    @Override
    public void run() {
      clientUi.getInput(this);
    }

    /**
     * Handle input.
     *
     * @param message the message
     */
    public void handleInput(String message) {
      try {
        String parsedMessage = parser.parse(message);
        controller.processClientMsg(parsedMessage);
        if (readyStop) {
          System.exit(0);
        }
      } catch (FrameExceptions ex) {
        clientUi.showClientMessage(ex.getMessage());
      }
    }
  }
}

package client.ui;

import static java.nio.charset.StandardCharsets.UTF_8;

import client.network.YahtzeeClient.MessageSendingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The type Text client ui that just get users' input from the console and print messages on the
 * console.
 */
public class TextClientUi implements ClientUi {

  @Override
  public void showServerMessage(String message) {
    System.out.println(message);
  }

  @Override
  public void showClientMessage(String message) {
    System.out.println("Client message: " + message);
  }

  @Override
  public void showNetworkStatus(String message) {
    System.out.println("Network Status: " + message);
  }

  @Override
  public void getInput(MessageSendingHandler handler) {
    try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in, UTF_8));) {
      while (true) {
        String message = input.readLine();
        handler.handleInput(message);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}

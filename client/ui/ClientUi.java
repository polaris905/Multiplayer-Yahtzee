package client.ui;

import client.network.YahtzeeClient.MessageSendingHandler;

/**
 * The interface Client ui.
 */
public interface ClientUi {

  /**
   * Show server message.
   *
   * @param message the message
   */
  void showServerMessage(String message);

  /**
   * Show client message.
   *
   * @param message the message
   */
  void showClientMessage(String message);

  /**
   * Show network status.
   *
   * @param message the message
   */
  void showNetworkStatus(String message);

  /**
   * Gets user's input.
   *
   * @param handler the handler
   */
  void getInput(MessageSendingHandler handler);
}

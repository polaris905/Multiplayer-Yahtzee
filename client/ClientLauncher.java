package client;

import argparser.ArgParser;
import argparser.CmdLineExceptions;
import argparser.Option;
import argparser.Option.OptionBuilder;
import argparser.Options;
import client.network.YahtzeeClient;
import client.ui.ClientUi;
import client.ui.GraphClientUi;
import client.ui.TextClientUi;

import java.util.Map;

/**
 * The type Client launcher that represents the entrance of the Client application.
 */
public class ClientLauncher {

  private static final String HOST_REGEX = "^((localhost)|((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)"
      + "\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?))$";
  private static final String PORT_REGEX = "^([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]"
      + "\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";
  private static Options options;

  static {
    options = new Options();
    options.addOption(
        new OptionBuilder("--HOST").setRequired().hasSubOption().setSubOptionRegex(HOST_REGEX)
            .setDesc("").build());
    options.addOption(
        new OptionBuilder("--PORT").hasSubOption().setSubOptionRegex(PORT_REGEX).setDesc("")
            .build());
    options.addExample("--HOST localhost --PORT 1200");
    options.generateUsage();
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    ArgParser argParser = new ArgParser(args, options);
    try {
      Map<String, Option> validOptions = argParser.parse();
      ClientUi clientUi = new TextClientUi();
//      ClientUi clientUi = new GraphClientUi();
      String host = validOptions.get("--HOST").getSubOptions().get(0);
      int port = Integer.parseInt(validOptions.get("--PORT").getSubOptions().get(0));
      YahtzeeClient client = new YahtzeeClient(clientUi, host, port);
      client.connectServer();
    } catch (CmdLineExceptions ex) {
      System.out.println(ex.getMessage());
      System.out.println(options.getUsage());
    }
  }
}

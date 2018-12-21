package server;

import argparser.ArgParser;
import argparser.CmdLineExceptions;
import argparser.Option;
import argparser.Option.OptionBuilder;
import argparser.Options;
import server.network.YahtzeeServer;

import java.util.Map;

/**
 * The type Server launcher that represents the entrance of the server application.
 */
public class ServerLauncher {

  private static final int ROUND = 13;
  private static final int MAX_PLAYERS = 6;
  private static final String PORT_REGEX = "^[0-9]*$";
  private static final String DEV_REGEX = "^([1-9]|1[0-2])$";
  private static final String MAX_REGEX = "^[1-9]$";
  private static final Options OPTIONS = new Options();

  static {
    OPTIONS.addOption(
        new OptionBuilder("--PORT").setRequired().hasSubOption().setSubOptionRegex(PORT_REGEX)
            .setDesc("The PORT used to connect.").build());
    OPTIONS.addOption(new OptionBuilder("--DEV").hasSubOption().setSubOptionRegex(DEV_REGEX)
        .setDesc("The number of round in developing mode.").build());
    OPTIONS.addOption(new OptionBuilder("--MAX").hasSubOption().setSubOptionRegex(MAX_REGEX)
        .setDesc("The max number of players in a game.").build());
    OPTIONS.addExample("--PORT 1200 --DEV 3");
    OPTIONS.addExample("--PORT 1200 --MAX 4");
    OPTIONS.generateUsage();
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    ArgParser argParser = new ArgParser(args, OPTIONS);
    try {
      Map<String, Option> validOptions = argParser.parse();
      int port = Integer.parseInt(validOptions.get("--PORT").getSubOptions().get(0));
      int round = validOptions.containsKey("--DEV") ? Integer
          .parseInt(validOptions.get("--DEV").getSubOptions().get(0)) : ROUND;
      int maxPlayers = validOptions.containsKey("--MAX") ? Integer
          .parseInt(validOptions.get("--MAX").getSubOptions().get(0)) : MAX_PLAYERS;
      YahtzeeServer server = new YahtzeeServer(port, round, maxPlayers);
      server.setUpNetwork();
    } catch (CmdLineExceptions ex) {
      System.out.println(ex.getMessage());
      System.out.println(OPTIONS.getUsage());
    }
  }
}

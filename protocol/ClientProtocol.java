package protocol;

import protocol.Frame.FrameBuilder;

/**
 * The type Client protocol that defines the frame of message the client need to obey.
 */
public class ClientProtocol {

  /**
   * The constant FRAMES.
   */
  public static final Frames FRAMES = new Frames();
  /**
   * The constant MSGID_REGEX.
   */
  public static final String MSGID_REGEX = "^[1-9]\\d*$";
  /**
   * The constant KEEP_REGEX.
   */
  public static final String KEEP_REGEX = "^[0-1]$";
  /**
   * The constant SCORENAME_REGEX.
   */
  public static final String SCORENAME_REGEX =
      "^(Aces|Twos|Threes|Fours|Fives|Sixes|ThreeOfKing" + "|FourOfKind|FH|SS|LS|Yahtzee|Chance)$";
  /**
   * The constant TEXT_REGEX.
   */
  public static final String TEXT_REGEX = ".*";

  static {
    FRAMES.addFrame(
        new FrameBuilder("KEEP_DICE").addPayload(1, MSGID_REGEX).addPayload(5, KEEP_REGEX).build());
    FRAMES.addFrame(
        new FrameBuilder("SCORE_CHOICE").addPayload(1, MSGID_REGEX).addPayload(1, SCORENAME_REGEX)
            .build());
    FRAMES.addFrame(
        new FrameBuilder("ACK").addPayload(1, MSGID_REGEX).addPayload(1, TEXT_REGEX, true).build());
    FRAMES.addFrame(new FrameBuilder("PRINT_GAME_STATE").addPayload(1, MSGID_REGEX).build());
    FRAMES.addFrame(new FrameBuilder("QUIT_GAME").addPayload(1, MSGID_REGEX).build());
  }
}

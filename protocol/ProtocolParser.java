package protocol;

import argparser.CmdLineExceptions.IllegalValueException;
import protocol.Frame.Payload;
import protocol.FrameExceptions.FrameHeaderException;
import protocol.FrameExceptions.FramePayloadException;

import java.util.List;

/**
 * The type Protocol parser. It will compare the user's message to the rule of the given Frames, and
 * then check if the user's message is valid. If valid, send the message to the server. Otherwise,
 * throws a type of FrameExceptions.
 */
public class ProtocolParser {

  private Frames frames;

  /**
   * Instantiates a new Protocol parser.
   *
   * @param frames the frames
   */
  public ProtocolParser(Frames frames) {
    this.frames = frames;
  }

  /**
   * Parse user's message according to the Frames object. This method separates many check
   * algorithms into some helper private method, which makes it easy to modify some of the special
   * check algorithms.
   *
   * @param message the message
   * @return the parsed message
   * @throws FrameExceptions the FrameExceptions
   */
  public String parse(String message) throws FrameExceptions {
    String[] strs = message.split("\\s+");
    int index = 0;
    checkHeader(strs, index);
    Frame prototype = frames.getTotal().get(strs[index++]);
    checkPayloadCount(strs, prototype);
    handlePayloadEach(strs, index, prototype.getPayloadGroups());
    return String.join(" ", strs);
  }

  /**
   * Helper method that checks if the frame of the user's message exist in the Frames object.
   *
   * @param strs the user's message
   * @param index the index of the message
   * @throws IllegalValueException the exception
   */
  private void checkHeader(String[] strs, int index) throws FrameHeaderException {
    if (strs.length == 0 || !frames.getTotal().containsKey(strs[index])) {
      throw new FrameHeaderException("FORMAT CHECK ERROR. Invalid frame was given.");
    }
  }

  /**
   * Helper method that checks if the payload total count of the user's message is valid.
   *
   * @param strs the user's message
   * @param prototype the prototype of the frame
   * @throws IllegalValueException the exception
   */
  private void checkPayloadCount(String[] strs, Frame prototype) throws FramePayloadException {
    if (strs.length - 1 != prototype.getPayloadCount() && strs.length - 1 != prototype
        .getRequiredPayloadCount()) {
      throw new FramePayloadException("FORMAT CHECK ERROR. Wrong number of payload was given.");
    }
  }

  /**
   * Helper method that checks if eahc of the payload's format and count is valid.
   *
   * @param strs the user's message
   * @param protos the Payload list of the prototype frame
   * @throws IllegalValueException the exception
   */
  private void handlePayloadEach(String[] strs, int index, List<Payload> protos)
      throws FramePayloadException {
    for (int i = 0; i < protos.size() && index < strs.length; i++) {
      String regex = protos.get(i).getRegex();
      for (int j = 0; j < protos.get(i).getCount(); j++) {
        if (!strs[index++].matches(regex)) {
          throw new FramePayloadException("FORMAT CHECK ERROR. Wrong payload format was given.");
        }
      }
    }
  }
}
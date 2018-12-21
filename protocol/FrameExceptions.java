package protocol;

/**
 * Class to handle all frame related exceptions.
 */
public abstract class FrameExceptions extends RuntimeException {

  /**
   * The abstract class constructor..
   *
   * @param msg Error message
   */
  public FrameExceptions(String msg) {
    super(msg);
  }

  /**
   * This constructor prints the error message to handle errors related to FrameHeaderException.
   */
  public static class FrameHeaderException extends FrameExceptions {

    /**
     * This constructor passes the error message to its super class.
     *
     * @param msg Error message
     */
    public FrameHeaderException(String msg) {
      super(msg);
    }
  }

  /**
   * This constructor prints the error message to handle errors related to FramePayloadException.
   */
  public static class FramePayloadException extends FrameExceptions {

    /**
     * This constructor passes the error message to its super class.
     *
     * @param msg Error message
     */
    public FramePayloadException(String msg) {
      super(msg);
    }
  }

  /**
   * This constructor prints the error message to handle errors related to FrameMsgIdException.
   */
  public static class FrameMsgIdException extends FrameExceptions {

    /**
     * This constructor passes the error message to its super class.
     *
     * @param msg Error message
     */
    public FrameMsgIdException(String msg) {
      super(msg);
    }
  }
}
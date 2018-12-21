package protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type Frame represents a type of frame with some attributes. This class uses "Builder Pattern"
 * to build the Frame object.
 */
public class Frame {

  private String name;
  private String desc;
  private int payloadCount;
  private int requiredPayloadCount;
  private List<Payload> payloadGroups;

  private Frame(FrameBuilder builder) {
    this.name = builder.name;
    this.desc = builder.desc;
    this.payloadCount = builder.payloadCount;
    this.requiredPayloadCount = builder.requiredPayloadCount;
    this.payloadGroups = builder.payloadGroups;
  }

  /**
   * Gets the frame's name.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the frame's description.
   *
   * @return the description
   */
  public String getDesc() {
    return this.desc;
  }

  /**
   * Gets payload count.
   *
   * @return the payload count
   */
  public int getPayloadCount() {
    return payloadCount;
  }

  /**
   * Gets required payload count.
   *
   * @return the required payload count
   */
  public int getRequiredPayloadCount() {
    return requiredPayloadCount;
  }

  /**
   * Gets payload groups.
   *
   * @return the payload groups
   */
  public List<Payload> getPayloadGroups() {
    return payloadGroups;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Frame frame = (Frame) obj;
    return Objects.equals(this.toString(), frame.toString());
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    return "Frame{" + "name='" + name + '\'' + '}';
  }

  /**
   * The class of Frame builder that used to build an Frame object.
   */
  public static class FrameBuilder {

    private String name;
    private String desc = "";
    private int payloadCount;
    private int requiredPayloadCount;
    private List<Payload> payloadGroups;

    /**
     * Instantiates a new Frame builder.
     *
     * @param name the option's name
     */
    public FrameBuilder(String name) {
      this.name = name;
      this.payloadGroups = new ArrayList<>();
    }

    /**
     * Sets description.
     *
     * @param desc the description
     * @return the Frame builder
     */
    public FrameBuilder setDesc(String desc) {
      this.desc = desc;
      return this;
    }

    /**
     * Add payload frame builder.
     *
     * @param count the count
     * @param regex the regex
     * @param optional the optional
     * @return the frame builder
     */
    public FrameBuilder addPayload(int count, String regex, boolean optional) {
      Payload payload = new Payload(count, regex, optional);
      payloadGroups.add(payload);
      payloadCount += optional ? 0 : count;
      requiredPayloadCount += count;
      return this;
    }

    /**
     * Add payload frame builder.
     *
     * @param count the count
     * @param regex the regex
     * @return the frame builder
     */
    public FrameBuilder addPayload(int count, String regex) {
      return addPayload(count, regex, false);
    }

    /**
     * Build an Frame object according to the attributes assigned.
     *
     * @return the completed Frame object
     */
    public Frame build() {
      return new Frame(this);
    }
  }

  /**
   * The type Payload.
   */
  public static class Payload {

    private int count;
    private String regex;
    private boolean optional;

    /**
     * Instantiates a new Payload.
     *
     * @param count the count
     * @param regex the regex
     * @param optional the optional
     */
    public Payload(int count, String regex, boolean optional) {
      this.count = count;
      this.regex = regex;
      this.optional = optional;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public int getCount() {
      return count;
    }

    /**
     * Gets regex.
     *
     * @return the regex
     */
    public String getRegex() {
      return regex;
    }

    /**
     * Is optional boolean.
     *
     * @return the boolean
     */
    public boolean isOptional() {
      return optional;
    }
  }
}

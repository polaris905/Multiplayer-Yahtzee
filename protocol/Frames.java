package protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The type Frames that holds and manages all Frame objects. This class represents the rule of the
 * whole Frame objects.
 */
public class Frames {

  private Map<String, Frame> total;

  /**
   * Instantiates a new Frames.
   */
  public Frames() {
    this.total = new HashMap<>();
  }

  /**
   * Add an Frame object.
   *
   * @param frame the frame
   */
  public void addFrame(Frame frame) {
    total.put(frame.getName(), frame);
  }

  /**
   * Gets total Frame objects.
   *
   * @return the total
   */
  public Map<String, Frame> getTotal() {
    return this.total;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Frames frames = (Frames) obj;
    return Objects.equals(total, frames.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total);
  }

  @Override
  public String toString() {
    return "Frames{" + "total=" + total.toString() + '}';
  }
}

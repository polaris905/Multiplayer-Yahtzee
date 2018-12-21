package server.game;

import java.util.Objects;
import java.util.Random;

/**
 * Class to simulate dice related issues.
 */
public class Dice implements Comparable {

  private int value;
  private Random random = new Random();

  /**
   * Get the value of the dice.
   *
   * @return the value of dice, should be 1-6
   */
  public int getValue() {
    return value;
  }

  /**
   * Set the value of the dice.
   *
   * @param value the value of dice, should be 1-6
   */
  public void setValue(int value) {
    this.value = value;
  }

  /**
   * Simulate the rolling.
   */
  public void throwDice() {
    value = random.nextInt(6) + 1;
  }

  /**
   * Comparator for dice, sort from 1 to 6.
   *
   * @param other target dice
   * @return true if current dice's value is smaller than target dices.
   */
  @Override
  public int compareTo(Object other) {
    if (this.value == ((Dice) other).value && this.equals(other)) {
      return 0;
    } else {
      return this.value - ((Dice) other).value;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Dice dice = (Dice) obj;
    return value == dice.value && Objects.equals(random, dice.random);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, random);
  }

  @Override
  public String toString() {
    return "Dice{" + "value=" + value + ", random=" + random + '}';
  }
}

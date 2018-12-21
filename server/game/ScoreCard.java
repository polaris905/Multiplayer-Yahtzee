package server.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Score card to store all score info.
 */
public class ScoreCard {

  private int[] scores;
  private int[] possibleScores;

  /**
   * Initialize the scores and possible scores arrays.
   */
  public ScoreCard() {
    scores = new int[13];
    possibleScores = new int[13];
    Arrays.fill(scores, -1);
  }

  /**
   * Get the total score.
   * @return the score value.
   */
  public int getTotalScore() {
    int totalScore = 0;
    for (int point : scores) {
      if (point != -1) {
        totalScore += point;
      }
    }
    return totalScore;
  }

  /**
   * Getter for scores array.
   * @return the score array.
   */
  public int[] getScores() {
    int[] temp = new int[13];
    for (int i = 0; i < 13; i++) {
      temp[i] = scores[i];
    }
    return temp;
  }

  /**
   * Getter for possible scores array.
   * @return the possible score array.
   */
  public int[] getPossibleScores() {
    int[] temp = new int[13];
    for (int i = 0; i < 13; i++) {
      temp[i] = possibleScores[i];
    }
    return temp;
  }

  /**
   * Calculate all possible situations.
   * @param dices a list of 5 dices.
   */
  public void calculatePossibleScores(List<Dice> dices) {
    for (int i = 0; i < scores.length; i++) {
      possibleScores[i] = calculateScore(dices, i);
    }
  }

  /**
   * Calculate score for target situation.
   * @param dices a list of 5 dices.
   * @param index situation index.
   * @return the score for target situation.
   */
  private int calculateScore(List<Dice> dices, int index) {
    if (index >= 0 && index <= 5) {
      return calculateUpper(dices, index);
    } else if (index >= 6 && index <= 7) {
      return calculateKind(dices);
    } else if (index == 8) {
      return calculateFh(dices);
    } else if (index == 9) {
      return calculateSs(dices);
    } else if (index == 10) {
      return calculateLs(dices);
    } else if (index == 11) {
      return calculateYahtzee(dices);
    } else {
      return calculateSum(dices);
    }
  }

  /**
   * Count the number of dices with target value.
   * @param dices a list of 5 dices.
   * @param index the target dice value.
   * @return the count number.
   */
  private int countOf(List<Dice> dices, int index) {
    int count = 0;
    for (Dice dice : dices) {
      if (dice.getValue() == index + 1) {
        count++;
      }
    }
    return count;
  }

  /**
   * Calculate the upper section.
   * @param dices a list of 5 dices.
   * @param index dice value, should be 1-6.
   * @return the score value.
   */
  private int calculateUpper(List<Dice> dices, int index) {
    return countOf(dices, index) * (index + 1);
  }

  /**
   * Calculate for three kind case.
   * @param dices a list of 5 dices.
   * @return the score.
   */
  private int calculateKind(List<Dice> dices) {
    for (int i = 1; i <= 6; i++) {
      int count = countOf(dices, i);
      if (count >= 3) {
        return calculateSum(dices);
      }
    }
    return 0;
  }

  /**
   * Sum all dices' value.
   * @param dices a list of 5 dices.
   * @return total value of all dices.
   */
  private int calculateSum(List<Dice> dices) {
    int sum = 0;
    for (Dice dice : dices) {
      sum += dice.getValue();
    }
    return sum;
  }

  /**
   * Calculate for full house.
   * @param dices a list of 5 dices.
   * @return score value.
   */
  private int calculateFh(List<Dice> dices) {
    Map<Integer, Integer> map = new HashMap<>();
    int maxCount = 0;
    for (Dice dice : dices) {
      int count = map.getOrDefault(dice.getValue(), 0) + 1;
      map.put(dice.getValue(), count);
      maxCount = Math.max(maxCount, count);
      if (map.size() > 2) {
        return 0;
      }
    }
    return maxCount <= 3 ? 25 : 0;
  }

  /**
   * Calculate small straight.
   * @param dices a list of 5 dices.
   * @return score value.
   */
  private int calculateSs(List<Dice> dices) {
    List<Dice> list = new ArrayList<>(dices);
    Collections.sort(list);
    int count = 0;
    for (int i = 0; i < list.size(); i++) {
      if (i == 0) {
        count++;
      } else if (list.get(i).getValue() == list.get(i - 1).getValue() + 1) {
        count++;
      } else if (list.get(i).getValue() != list.get(i - 1).getValue()) {
        return 0;
      }
    }
    return count >= 4 ? 30 : 0;
  }

  /**
   * Calculate large straight.
   * @param dices a list of 5 dices.
   * @return score value.
   */
  private int calculateLs(List<Dice> dices) {
    List<Dice> list = new ArrayList<>(dices);
    Collections.sort(list);
    for (int i = 0; i < list.size(); i++) {
      if (i != 0 && list.get(i).getValue() != list.get(i - 1).getValue() + 1) {
        return 0;
      }
    }
    return 40;
  }

  /**
   * Calculate yahtzee case.
   * @param dices a list of 5 dices.
   * @return score value.
   */
  private int calculateYahtzee(List<Dice> dices) {
    for (int i = 0; i < dices.size(); i++) {
      if (i != 0 && dices.get(i).getValue() != dices.get(i - 1).getValue()) {
        return 0;
      }
    }
    return 50;
  }

  /**
   * Choose target score.
   * @param index index of score choice.
   */
  public void chooseScore(int index) {
    scores[index] = possibleScores[index];
  }

  /**
   * The pattern for different score choice.
   */
  public enum Pattern {
    Aces(0), Twos(1), Threes(2), Fours(3), Fives(4), Sixes(5), ThreeOfKing(6), FourOfKind(7), FH(
        8), SS(9), LS(10), Yahtzee(11), Chance(12);
    private int index;

    /**
     * Set the pattern.
     * @param index index num.
     */
    Pattern(int index) {
      this.index = index;
    }

    /**
     * Getter for pattern's index.
     * @return the index.
     */
    public int getIndex() {
      return index;
    }
  }
}

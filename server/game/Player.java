package server.game;

import server.game.ScoreCard.Pattern;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class for player.
 */
public class Player {

  private static final int DICE_NUMBER = 5;
  private int playerId;
  private PrintWriter output;
  private ScoreCard scoreCard = new ScoreCard();
  private List<Dice> dices = new ArrayList<>(DICE_NUMBER);

  /**
   * Construct the player.
   *
   * @param ide a player should have a unique id.
   * @param output should have a print writer to show message.
   */
  public Player(int ide, PrintWriter output) {
    this.playerId = ide;
    this.output = output;
    for (int i = 0; i < DICE_NUMBER; i++) {
      dices.add(new Dice());
    }
  }

  /**
   * Getter for the player's id.
   *
   * @return the unique id.
   */
  public int getPlayerId() {
    return playerId;
  }

  /**
   * Getter for the player's output.
   *
   * @return the output.
   */
  public PrintWriter getOutput() {
    return output;
  }

  /**
   * Getter for the player's dices.
   *
   * @return a list of dices.
   */
  public List<Dice> getDices() {
    return dices;
  }

  /**
   * Setter for the player's dices.
   * @param dices the dices
   */
  public void setDices(List<Dice> dices) {
    this.dices = dices;
  }

  /**
   * Setter for score card.
   *
   * @param dices a list of dices.
   */
  public void setScoreCard(List<Dice> dices) {
    this.scoreCard.calculatePossibleScores(dices);
  }

  /**
   * Throw the dice - initialization.
   */
  public void throwDices() {
    throwDices(new int[DICE_NUMBER]);
  }

  /**
   * Throw the dice - follow the keep dice instruction.
   *
   * @param indexes a array with dice's index, should be 1-5.
   */
  public void throwDices(int[] indexes) {
    if (indexes.length != DICE_NUMBER) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < DICE_NUMBER; i++) {
      if (indexes[i] == 0) {
        dices.get(i).throwDice();
      }
    }
    scoreCard.calculatePossibleScores(dices);
  }

  /**
   * Choose score.
   *
   * @param scoreName type of the score choice.
   */
  public void chooseScoreSlot(String scoreName) {
    int slotId = Pattern.valueOf(scoreName).getIndex();
    if (scoreCard.getScores()[slotId] == -1) {
      scoreCard.chooseScore(slotId);
    } else {
      throw new IllegalArgumentException("Target score slot is unavailable.");
    }
  }

  /**
   * Show scores.
   *
   * @return a string with above info.
   */
  public String getScoresString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Pattern pattern : Pattern.values()) {
      stringBuilder.append(pattern + ": " + scoreCard.getScores()[pattern.getIndex()] + " ");
    }
    return stringBuilder.append(getTotalScoreString()).toString();
  }

  /**
   * Get the total score.
   *
   * @return a string with above info.
   */
  public String getTotalScoreString() {
    return "Total: " + scoreCard.getTotalScore();
  }

  /**
   * Show possible scores.
   *
   * @return a string with above info.
   */
  public String getPossibleScoresString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Pattern pattern : Pattern.values()) {
      stringBuilder.append(
          pattern + ": " + (scoreCard.getScores()[pattern.getIndex()] == -1 ? scoreCard
              .getPossibleScores()[pattern.getIndex()] : "UNAVAILABLE")).append(" ");
    }
    return stringBuilder.toString();
  }

  /**
   * Show all dices with their value.
   *
   * @return a string with above info.
   */
  public String showDices() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Dice dice : dices) {
      stringBuilder.append(dice.getValue() + " ");
    }
    return stringBuilder.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Player player = (Player) obj;
    return playerId == player.playerId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(playerId);
  }

  @Override
  public String toString() {
    return "Player{" + "playerId=" + playerId + '}';
  }
}


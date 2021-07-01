import java.util.ArrayList;
import java.util.List;

/**
 * Blackjack
 * Author: Peter Mitchell (2021)
 *
 * Hand class:
 * Stores the cards contained in a single hand.
 */
public class Hand {
    /**
     * List of cards representing face values as Strings.
     */
    private List<String> cards;
    /**
     * Used to hide the second card for the dealer's hand when printing.
     */
    private boolean hideHand;
    /**
     * Stores the value of the current hand. It is updated when the hand is changed.
     */
    private int handValue;
    /**
     * The current bet associated with this hand.
     */
    private int bet;

    /**
     * Creates a default empty hand with no cards.
     *
     * @param isPlayer Used to determine if the second card should be hidden.
     */
    public Hand(boolean isPlayer) {
        cards = new ArrayList<>();
        handValue = 0;
        hideHand = !isPlayer;
        bet = 0;
    }

    /**
     * Adds the provided card to the hand and calculates the
     * new max value of the hand.
     *
     * @param faceValue The card to add to the hand.
     */
    public void addCard(String faceValue) {
        cards.add(faceValue);
        calculateMaxValueOfHand();
    }

    /**
     * Changes the bet associated with this hand.
     *
     * @param bet New bet to apply to this hand.
     */
    public void setBet(int bet) {
        this.bet = bet;
    }

    /**
     * Gets the bet for the current hand.
     *
     * @return The current bet for this hand.
     */
    public int getBet() {
        return bet;
    }

    /**
     * Makes the hand reveal the hidden cards. Used by the dealer.
     */
    public void revealHand() {
        hideHand = false;
    }

    /**
     * Gets the maximum value associated with the hand.
     *
     * @return Maximum value of this hand.
     */
    public int getHandValue() {
        return handValue;
    }

    /**
     * Checks if the hand can be split with two of the same card.
     * Also required to have at least $2 bet to allow splitting of the bet.
     *
     * @return True if the hand can be split.
     */
    public boolean canSplit() {
        if(cards.size() == 2 && bet > 1) {
            return cards.get(0).equals(cards.get(1));
        }
        return false;
    }

    /**
     * Creates a new second hand and moves half the bet to it
     * along with the duplicate card.
     *
     * @return The new hand that was split off from the original.
     */
    public Hand getSplitHand() {
        Hand newHand = new Hand(true);
        newHand.addCard(cards.get(0));
        int betRemainder = bet % 2;
        bet /= 2;
        newHand.setBet(bet + betRemainder);
        cards.remove(0);
        calculateMaxValueOfHand();
        return newHand;
    }

    /**
     * Calculates the maximum value of the hand based on face values,
     * and then handles cases where aces could be either 1 or 11 to
     * maximise score.
     */
    private void calculateMaxValueOfHand() {
        int aces = 0;
        int newScore = 0;
        for(String card : cards) {
            if(card.equals("A")) {
                aces++;
                newScore++;
            } else if(card.equals("J") || card.equals("Q") || card.equals("K"))
                newScore += 10;
            else newScore += Integer.parseInt(card);
        }
        if(aces > 0 && newScore < 12) {
            newScore += 10;
        }
        if(aces > 1 && newScore < 12) {
            newScore += 10;
        }
        handValue = newScore;
    }

    /**
     * Gets the String representation of the hand.
     * If the hand has to be hidden the second card is shown as ?.
     * If the hand only has two cards and is 21 for score it is shown with (BLACKJACK).
     *
     * @return A String version of the hand.
     */
    @Override
    public String toString() {
        String result = "";
        if(hideHand) {
            result += cards.get(0) + " ? ";
        } else {
            for (String card : cards) {
                result += card + " ";
            }
            result += "= " + getHandValue();
            if(cards.size() == 2 && getHandValue() == 21) {
                result += " (BLACKJACK!)";
            }
        }
        return result;
    }
}

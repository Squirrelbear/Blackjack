import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Blackjack
 * Author: Peter Mitchell (2021)
 *
 * DeckOfCards class:
 * Defines a deck of cards filled with 4x each face value card with no suit.
 * Drawing more cards past the end of the deck automatically refills the deck.
 */
public class DeckOfCards {
    /**
     * The current deck of remaining cards.
     */
    private List<String> deck;
    /**
     * All possible face values that a deck can contain.
     */
    private final String[] faceValues = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    /**
     * Creates a deck of cards with 4x each of the face cards and shuffles them randomly.
     */
    public DeckOfCards() {
        deck = new ArrayList<>();
        fillDeck();
        shuffle();
    }

    /**
     * Empties the current deck and fills it with a new ordered set of 4x each card.
     */
    public void fillDeck() {
        deck.clear();
        for(String faceValue : faceValues) {
            for(int i = 0; i < 4; i++)
                deck.add(faceValue);
        }
    }

    /**
     * Shuffles the deck to randomise the order.
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }

    /**
     * Checks if the deck is empty and refills it with a new deck if necessary.
     * Then draws the top card from the deck and returns it.
     *
     * @return The next card from the deck.
     */
    public String drawCard() {
        if(deck.isEmpty()) {
            fillDeck();
            shuffle();
        }
        String drawnCard = deck.get(0);
        deck.remove(0);
        return drawnCard;
    }

    /**
     * Gets if the deck has no cards in it.
     *
     * @return True if the deck is currently empty.
     */
    public boolean isDeckEmpty() {
        return deck.isEmpty();
    }
}

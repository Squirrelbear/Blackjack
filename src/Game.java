import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Blackjack
 * Author: Peter Mitchell (2021)
 *
 * Game class:
 * Creates and controls the flow of the game.
 * The player is given starting cash and then continues
 * to bet on hands with options for what they want to
 * do with each hand. The game ends when the player runs
 * out of money.
 */
public class Game {
    /**
     * Entry point for the Game to create the game and start it.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    /**
     * The deck of cards that is used for drawing cards to put in hands.
     */
    private DeckOfCards deck;
    /**
     * The dealers hand that is being played against by the player.
     */
    private Hand dealer;
    /**
     * One or more hands of the player to play against the dealer.
     * Multiple hands will only happen during splits.
     */
    private List<Hand> playerHands;
    /**
     * Reference to the Scanner for player input.
     */
    private Scanner scan;
    /**
     * Current cash that the player owns. Any currently bet cash is
     * excluded until it is evaluated as part of the hand result.
     */
    private int playerCash = 50;
    /**
     * The amount to be bet on the current hand. Can be changed before
     * the hand is revealed each new round.
     */
    private int defaultBet = 5;
    /**
     * The number of rounds that have been played.
     */
    private int roundCount = 0;

    /**
     * Creates the default state of the game setting up the new deck,
     * an empty set of player hands, and the Scanner ready for player inout.
     */
    public Game() {
        deck = new DeckOfCards();
        playerHands = new ArrayList<>();
        scan = new Scanner(System.in);
    }

    /**
     * Starts the game by looping to play new rounds until the player runs out of cash.
     */
    public void start() {
        do {
            playRound();
        } while(playerCash > 0);
        System.out.println("Game Over! You ran out of cash! :(");
        System.out.println("You played " + roundCount + " rounds before losing everything.");
    }

    /**
     * Plays all the steps for a single round. Starts by setting the bet for that round.
     * Then deals the cards, shows what has been dealt (hiding the second dealer card),
     * then playing the player's turn/s and dealer's turn. Then evaluating the resulting
     * state of each hand and dealing out any winnings.
     */
    private void playRound() {
        roundCount++;
        System.out.println("Starting round " + roundCount);
        setBet();
        dealStartOfRound();

        showPlayerHands();
        System.out.println("Dealer hand: " + dealer);
        System.out.println();

        playerTurn();
        dealerTurn();

        System.out.println();
        checkRoundResults();
    }

    /**
     * Plays through all the player's hand/s. For each hand the player is given the
     * choice to stand/hit/double down/split/forfeit. Once the player either ends
     * the hand by choice or busts it that had is complete for the round.
     */
    private void playerTurn() {
        for(int i = 0; i < playerHands.size(); i++) {
            boolean handEnded = false;
            Hand curHand = playerHands.get(i);
            while(!handEnded) {
                int action = promptPlayerAction(curHand);
                switch (action) {
                    case 3:
                        doubleHandBet(curHand); // continues on to perform Hit action
                    case 2:
                        drawCardsToHand(curHand, 1);
                        break;
                    case 4:
                        performSplitChoice(curHand);
                        i--; // Reset to begin fresh for the hand with the single card
                        break;
                    case 5:
                        foldHand(curHand);
                        break;
                }
                handEnded = (action != 2 && action != 3) || playerHands.get(i).getHandValue() > 21;
            }
            if(curHand.getHandValue() > 21) {
                System.out.println("Oh no! Your hand went bust: " + playerHands.get(i));
            }
        }
    }

    /**
     * Processes the dealer's turn by drawing cards until their score is 17 or higher.
     */
    private void dealerTurn() {
        dealer.revealHand();
        System.out.println("Dealer hand: " + dealer);
        while(dealer.getHandValue() < 17) {
            drawCardsToHand(dealer,1);
            System.out.println("Dealer hit for another card.");
            System.out.println("Dealer hand: " + dealer);
        }
        if(dealer.getHandValue() > 21) {
            System.out.println("Dealer bust!");
        }
    }

    /**
     * Creates the new hands for both dealer and player.
     * Then draws two cards to each and applies the selected bet amount to the hand.
     */
    private void dealStartOfRound() {
        dealer = new Hand(false);
        playerHands.clear();
        playerHands.add(new Hand(true));
        drawCardsToHand(playerHands.get(0), 2);
        drawCardsToHand(dealer, 2);
        playerHands.get(0).setBet(defaultBet);
        playerCash -= defaultBet;
    }

    /**
     * Draws a number of cards from the deck and adds them to the hand.
     *
     * @param handToPlaceIn Reference to the hand to add cards to.
     * @param numOfCards The number of cards to add to the hand.
     */
    private void drawCardsToHand(Hand handToPlaceIn, int numOfCards) {
        for(int i = 0; i < numOfCards; i++) {
            String card = deck.drawCard();
            handToPlaceIn.addCard(card);
        }
    }

    /**
     * Splits a hand into two new hands by making a hand split itself.
     * Bet is evenly divided between hands.
     *
     * @param handToSplit Hand that has a pair to be split.
     */
    private void performSplitChoice(Hand handToSplit) {
        System.out.println("Splitting your hands into two.");
        if(handToSplit.canSplit()) {
            playerHands.add(handToSplit.getSplitHand());
        }
    }

    /**
     * Prints all the cards controlled by the player.
     * Handles both the case where they have just a single hand, and
     * when a split has occurred resulting in 2 or more hands.
     */
    private void showPlayerHands() {
        if(playerHands.size() > 1) {
            System.out.println("Player hands:");
            for(int i = 0; i < playerHands.size(); i++) {
                System.out.println("Split Hand " + (i+1) + ": " + playerHands.get(i));
            }
        } else {
            System.out.println("Player hand: " + playerHands.get(0));
        }
    }

    /**
     * Checks the result of a hand and shows the outcome.
     * Winnings are also applied by this method where some change
     * in playerCash is necessary.
     *
     * @param hand The hand to check for a result.
     */
    private void evaluateHandResult(Hand hand) {
        if(hand.getBet() == 0) {
            System.out.println(hand + " (FORFEIT)");
        } else if (hand.getHandValue() > 21) {
            System.out.println(hand + " (BUST)");
        } else if(hand.getHandValue() == dealer.getHandValue()) {
            System.out.println(hand + " (DRAW)");
            playerCash += hand.getBet();
        }else if(dealer.getHandValue() > 21 || hand.getHandValue() > dealer.getHandValue()) {
            System.out.println(hand + " (WON)");
            playerCash += hand.getBet() * 2;
        } else {
            System.out.println(hand + " (LOST)");
        }
    }

    /**
     * Lets the player change the current bet to any value from 1 to playerCash.
     * The player can continue using the same bet from the last round if they wish.
     */
    private void setBet() {
        // Cap bet based on how much cash the player has.
        defaultBet = Math.min(defaultBet, playerCash);

        System.out.println("Your current bet is $" + defaultBet
                            + " and you have $" + playerCash + ".");
        List<String> validOptions = new ArrayList<>();
        validOptions.add("1");
        validOptions.add("2");
        String questionPrompt = "Choose one of the following.\n"
                                + "1. Keep current bet\n2. Change bet";
        int choice = Integer.parseInt(getQuestionAnswer(questionPrompt, validOptions));
        if(choice == 2) {
            String question = "Enter a number between 1 and " + playerCash + ".";
            defaultBet = getIntInputInRange(question, 1, playerCash);
        }
    }

    /**
     * Removes more cash from the player to double a single hand bet.
     *
     * @param handToDoubleBet Reference to the hand to apply doubled bet to.
     */
    private void doubleHandBet(Hand handToDoubleBet) {
        playerCash -= handToDoubleBet.getBet();
        handToDoubleBet.setBet(handToDoubleBet.getBet() * 2);
        System.out.println("Bet has been doubled for this hand to: $"+handToDoubleBet.getBet());
    }

    /**
     * Returns half the cash bet on the folded hand and sets the bet to 0 for that hand.
     *
     * @param handToFold Reference to the hand to forfeit/fold
     */
    private void foldHand(Hand handToFold) {
        playerCash += handToFold.getBet() / 2;
        handToFold.setBet(0);
    }

    /**
     * Prints out all the hand results for the dealer and
     * all the player's hands.
     */
    private void checkRoundResults() {
        System.out.println("Result of hand(s):");
        System.out.println("Dealer hand: " + dealer);
        for(Hand hand : playerHands) {
            evaluateHandResult(hand);
        }
        System.out.println();
    }

    /**
     * Used to evaluate the choice for a single step of a specific hand.
     * Provides options to stand/hit/forfeit and if conditions are met it will
     * provide additional options to split or double down.
     *
     * @param currentHand The hand to provide a choice for.
     * @return The selected action for the hand based on valid options.
     */
    private int promptPlayerAction(Hand currentHand) {
        List<String> validOptions = new ArrayList<>();
        validOptions.add("1");
        validOptions.add("2");
        validOptions.add("5");
        String questionPrompt = "Your current hand is: " + currentHand + "\n"
                                + "Choose one of the following.\n"
                                + "1. Stand\n2. Hit\n";
        if(currentHand.getBet() <= playerCash) {
            validOptions.add("3");
            questionPrompt += "3. Double Down (hit, and also double your bet)\n";
        }
        if(currentHand.canSplit()) {
            validOptions.add("4");
            questionPrompt += "4. Split (split pair into another hand)\n";
        }
        questionPrompt += "5. Surrender Hand (forfeit half your bet)";

        return Integer.parseInt(getQuestionAnswer(questionPrompt, validOptions));
    }

    /**
     * Asks the player for input and continues to ask until one of the valid
     * responses has been entered.
     *
     * @param question The message to question the player with showing valid options.
     * @param validResponses A list of Strings with valid options for the player to enter.
     * @return The chosen option from the validResponses.
     */
    private String getQuestionAnswer(String question, List<String> validResponses) {
        System.out.println(question);
        String result = quitOrGetString();

        while(!validResponses.contains(result)) {
            System.out.println("Sorry, that input was not valid! Try again.");
            System.out.println(question);
            result = quitOrGetString();
        }
        return result;
    }

    /**
     * Asks the player for a number in a range and continues to ask until
     * valid input has been entered successfully.
     *
     * @param question The question to pose to the player to get input.
     * @param min The minimum valid number to enter.
     * @param max The maximum valid number to enter.
     * @return A valid number in the range of min and max.
     */
    private int getIntInputInRange(String question, int min, int max) {
        int input;
        do {
            System.out.println(question);
            while (!scan.hasNextInt()) {
                quitOrGetString();
                System.out.println("That is not a number! You can only enter a number between "
                        + min + " and " + max + ".");
            }
            input = scan.nextInt();

            if(input < min || input > max) {
                System.out.println("That number is not in the valid range! You can only enter a number between "
                                    + min + " and " + max + ".");
            }
            scan.nextLine();
        } while(input < min || input > max);
        return input;
    }

    /**
     * Gets the current line and checks if it is "q" or "quit" to exit the game.
     * Otherwise just returns the String that was entered.
     *
     * @return The current line of input text from Scanner.
     */
    private String quitOrGetString() {
        String input = scan.nextLine();
        if(input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
            System.exit(0);
        }
        return input;
    }
}

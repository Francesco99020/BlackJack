package org.example.blackjack;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.*;

public class GameManager {
    private HBox playerCardBox, dealerCardBox;
    private int playerTotal;
    private ArrayList<Card> playerCards;
    //For Splits
    private ArrayList<Card>[] splitHands;//array gets flipped on table so first index is right-most on table
    private  boolean hasSplit;
    private ArrayList<HBox> splitCardBoxes;
    private int currentHandIndex;
    private int[] splitTotals;
    private int numberOfSplitHands = 0;
    //End of Splits
    private int dealerTotal;
    private ArrayList<Card> dealerCards;
    private boolean firstHandOfShoe;
    private Scene gameScene;

    private Shoe shoe;

    public GameManager(Shoe shoe, Scene gameScene, HBox playerCardBox, HBox dealerCardBox) {
        this.gameScene = gameScene;
        this.shoe = shoe;
        this.playerCardBox = playerCardBox;
        this.dealerCardBox = dealerCardBox;
        firstHandOfShoe = true;
    }

    public void UpdateScene() {
        playerCardBox.getChildren().clear();
        if(hasSplit){
            for(int i = 0; i < splitCardBoxes.size(); i++){
                splitCardBoxes.get(i).getChildren().clear();
                for(Card card : splitHands[i]){
                    ImageView cardImageView = new ImageView(card.getImage());
                    cardImageView.setFitHeight(100);
                    cardImageView.setPreserveRatio(true);
                    splitCardBoxes.get(i).getChildren().add(cardImageView);
                }
                splitCardBoxes.get(i).setStyle(i == currentHandIndex ? "-fx-border-color: gold;" : "-fx-border-color: transparent;");
                playerCardBox.getChildren().add(splitCardBoxes.get(i));
            }
        } else {
            for (Card card : playerCards) {
                ImageView cardImageView = new ImageView(card.getImage());
                cardImageView.setFitHeight(100);
                cardImageView.setPreserveRatio(true);
                playerCardBox.getChildren().add(cardImageView);
            }
        }
        dealerCardBox.getChildren().clear();
        for (Card card : dealerCards) {
            ImageView cardImageView = new ImageView(card.getImage());
            cardImageView.setFitHeight(100);
            cardImageView.setPreserveRatio(true);
            dealerCardBox.getChildren().add(cardImageView);
        }
    }


    private void dealersTurn() {
        // Reveal the dealer's first concealed card
        dealerCards.getFirst().setConcealed(false);
        dealerTotal = getTotal(dealerCards);
        UpdateScene();

        if(playerTotal == -1){
            showOutcomeDialog();
            return;
        }

        // Prepare a Timeline to handle each card draw and scene update
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Define what happens in each frame of the Timeline
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            // Check if dealer needs to hit
            if (dealerTotal < 17 && dealerTotal != -1) {
                dealerHit(); // Draw a card and update dealerTotal
                UpdateScene();
            } else {
                // Dealer is done hitting; show the outcome dialog
                timeline.stop();
                showOutcomeDialog();
            }
        }));

        // Start the timeline for the dealer's turn animations
        timeline.play();
    }

    // Method to show outcome dialog
    private void showOutcomeDialog() {
        if (hasSplit) {
            // Collect outcomes for each split hand
            StringBuilder splitResults = new StringBuilder("Split Hand Results:\n");

            for (int i = 0; i < numberOfSplitHands; i++) {
                splitResults.append("Hand ").append(i + 1).append(": ").append(decideWinner(i)).append("\n");
            }

            Platform.runLater(() -> {
                Alert outcomeAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
                outcomeAlert.setTitle("All Split Hand Results");
                outcomeAlert.setHeaderText("Results for Each Split Hand");
                outcomeAlert.setContentText(splitResults.toString());

                outcomeAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        Deal();  // Start the next hand
                    }
                });
            });
        } else {
            // No split, show a single outcome for the regular hand
            String result = decideWinner();

            Platform.runLater(() -> {
                Alert outcomeAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
                outcomeAlert.setTitle("Hand Result");
                outcomeAlert.setHeaderText(result);
                outcomeAlert.setContentText("Click Continue to start the next hand.");

                outcomeAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        Deal();  // Start the next hand
                    }
                });
            });
        }
    }


    public String decideWinner(){
        System.out.println("Player: " + playerTotal);
        System.out.println("Dealer: " + dealerTotal);
        if(dealerTotal > 21 || (dealerTotal < playerTotal && playerTotal <= 21)){//player wins
            System.out.println("player wins");
            return "You Win!";
        } else if(dealerTotal == playerTotal) {//push
            System.out.println("Push");
            return "Push!";
        } else {//dealer wins
            System.out.println("Dealer wins");
            return "Dealer Wins";
        }
    }

    public String decideWinner(int hand){
        int playerTotal = getTotal(splitHands[hand]);
        System.out.println("Player: " + playerTotal);
        System.out.println("Dealer: " + dealerTotal);
        if(dealerTotal > 21 || (dealerTotal < playerTotal && playerTotal <= 21)){//player wins
            System.out.println("player wins");
            return "You Win!";
        } else if(dealerTotal == playerTotal) {//push
            System.out.println("Push");
            return "Push!";
        } else {//dealer wins
            System.out.println("Dealer wins");
            return "Dealer Wins";
        }
    }

    public void dealerHit(){
        dealerCards.add(shoe.drawCard());
        dealerTotal = getTotal(dealerCards);
        UpdateScene();
    }

    public void Hit(){
        if(hasSplit){
            //find first split hand that isn't finished
            splitHands[currentHandIndex].add(shoe.drawCard());
            splitCardBoxes.get(currentHandIndex).getChildren().add(new ImageView(splitHands[currentHandIndex].getLast().getImage()));
            if(getTotal(splitHands[currentHandIndex]) == -1){//bust move to next hand or dealer turn
                currentHandIndex++;
                if(currentHandIndex != numberOfSplitHands){
                    splitHands[currentHandIndex].add(shoe.drawCard());
                    splitCardBoxes.get(currentHandIndex).getChildren().add(new ImageView(splitHands[currentHandIndex].getLast().getImage()));
                }
                if(currentHandIndex == numberOfSplitHands){//out of hands to play
                    dealersTurn();
                }
            }
        } else {
            playerCards.add(shoe.drawCard());
            playerTotal = getTotal(playerCards);
            System.out.println(playerTotal);
            if(playerTotal == -1){//bust
                dealersTurn();
            }
        }
        UpdateScene();
    }

    public void Stand(){
        if(hasSplit){
            currentHandIndex++;
            if(currentHandIndex != numberOfSplitHands){
                splitHands[currentHandIndex].add(shoe.drawCard());
                splitCardBoxes.get(currentHandIndex).getChildren().add(new ImageView(splitHands[currentHandIndex].getLast().getImage()));
            }
            UpdateScene();
            if(currentHandIndex == numberOfSplitHands){//out of hands to play
                dealersTurn();
            }
        } else{
            UpdateScene();
            dealersTurn();
        }
    }

    public void Double(){
        if(hasSplit){
            if(splitHands[currentHandIndex].size() != 2){
                System.out.println("Cannot Double with more than 2 cards");
                return;
            }else {
                Hit();
            }
        }
        if(playerCards.size() != 2){
            System.out.println("Cannot Double with more than 2 cards");
            return;
        }
        Hit();
        UpdateScene();
        dealersTurn();
    }

    private void realignSplitCardBoxes(){
        splitCardBoxes.add(new HBox());
        int index = 0;
        for(ArrayList<Card> hand : splitHands){
            if(hand == null) break;
            splitCardBoxes.get(index).getChildren().clear();
            for(Card c : hand){
                splitCardBoxes.get(index).getChildren().add(new ImageView(c.getImage()));
            }
            index++;
        }
        UpdateScene();
    }

    private boolean reorderSplits(){
        if(numberOfSplitHands+1 <= splitHands.length){//can add split [[A,3,7],[J,K],[10],[9],[]] -> [[A,3,7],[J],[k],[10],[9]]
            ArrayList<Card> next = new ArrayList<>();
            boolean foundSplit = false;
            for(int i = 0; i < splitHands.length; i++){
                if(i == currentHandIndex){//hand to split
                    next.add(splitHands[i].getLast());
                    splitHands[i].removeLast();
                    foundSplit = true;
                }
                if(foundSplit){
                    ArrayList<Card> temp = splitHands[i];
                    splitHands[i] = next;
                    next = temp;
                }
            }
            numberOfSplitHands++;
            return true;
        } else {
            return false;
        }
    }

    public void Split(){
        if(!hasSplit && playerCards.getFirst().getValue() == playerCards.getLast().getValue()){//has split and is first split
            //initialize splitHands
            hasSplit = true;
            splitHands = new ArrayList[5];
            splitHands[0] = new ArrayList<Card>();
            splitHands[1] = new ArrayList<Card>();
            numberOfSplitHands = 2;

            splitTotals = new int[5];

            //initialize splitCardBoxes
            splitCardBoxes = new ArrayList<>();
            splitCardBoxes.add(new HBox());
            splitCardBoxes.add(new HBox());

            // Use a Timeline to add delay for drawing new cards
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0), event -> {//add first card to split hand 1
                        //add each card from player hand to split hands
                        //top card goes to right most hand
                        splitHands[0].add(playerCards.getLast());
                        splitCardBoxes.getFirst().getChildren().add(new ImageView(splitHands[0].getFirst().getImage()));
                        //Bottom card goes to left hand
                        splitHands[1].add(playerCards.getFirst());
                        splitCardBoxes.getLast().getChildren().add(new ImageView(splitHands[1].getFirst().getImage()));
                        currentHandIndex = 0;//start at the right
                        UpdateScene();
                    }),
                    new KeyFrame(Duration.seconds(1), event -> {//add second card of hand to split hand 2
                       splitHands[0].add(shoe.drawCard());
                        UpdateScene();
                    })
            );

            timeline.setCycleCount(1);
            timeline.setOnFinished(event -> {
                splitTotals[0] = getTotal(splitHands[0]);
                splitTotals[1] = getTotal(splitHands[1]);
            });
            timeline.play();
        } else if(hasSplit){
            if(splitHands[currentHandIndex].getFirst().getValue() == splitHands[currentHandIndex].getLast().getValue()){//current hand has split
                //split hand and move last card to new hand to current hand
                if(reorderSplits()){//split successful
                    realignSplitCardBoxes();
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.seconds(1), event -> {
                                splitHands[currentHandIndex].add(shoe.drawCard());
                                splitCardBoxes.get(currentHandIndex).getChildren().add(new ImageView(splitHands[currentHandIndex].getLast().getImage()));
                                UpdateScene();
                            })
                    );
                    timeline.setCycleCount(1);
                    timeline.play();
                } else {
                    System.out.println("Split limit reached");
                }
            }
        } else {
            System.out.println("Cannot split current hand");
        }
    }

    public void Surrender(){
        Deal();
    }

    public void InitialDeal() {
        //Reset Split
        hasSplit = false;
        // Reset player and dealer hands, initialize discard rule if needed
        playerCards = new ArrayList<>();
        dealerCards = new ArrayList<>();

        if (firstHandOfShoe) {  // Discard first card if it's the first hand
            shoe.drawCard();
            firstHandOfShoe = false;
        }

        // Timeline to handle sequential dealing with new order
        Timeline dealTimeline = new Timeline();
        dealTimeline.setCycleCount(4); // Total of four cards to deal

        // Define alternating dealing order for player and dealer
        dealTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            if (playerCards.size() <= dealerCards.size()) {
                // Deal to player if player has equal or fewer cards than dealer
                playerCards.add(shoe.drawCard());
            } else {
                // Deal to dealer otherwise
                Card dealerCard = shoe.drawCard();
                if (dealerCards.isEmpty()) {  // Conceal the dealer's first card
                    dealerCard.setConcealed(true);
                }
                dealerCards.add(dealerCard);
            }

            // Update the scene after each card is dealt
            UpdateScene();
        }));

        // After all cards are dealt, calculate totals
        dealTimeline.setOnFinished(event -> {
            playerTotal = getTotal(playerCards);
            dealerTotal = getTotal(dealerCards);
            System.out.println("Player: " + playerTotal);
            System.out.println("Dealer: " + dealerTotal);
            if((playerCards.getFirst().getValue() == 1 && playerCards.get(1).getValue() == 10) || (playerCards.getFirst().getValue() == 10 && playerCards.get(1).getValue() == 1)) {//player BlackJack
                showOutcomeDialog();
            }
            if((dealerCards.get(1).peek() == 1 && dealerCards.getFirst().peek() == 10) || (dealerCards.get(1).peek() == 10 && dealerCards.getFirst().peek() == 1)){//dealer BlackJack
                showOutcomeDialog();
            }
        });

        // Start the dealing timeline
        dealTimeline.play();
    }

    public void Deal() {
        //Reset Split
        hasSplit = false;
        UpdateScene();
        // Reset hands and apply similar sequential dealing order as in InitialDeal
        playerCards = new ArrayList<>();
        dealerCards = new ArrayList<>();

        Timeline dealTimeline = new Timeline();
        dealTimeline.setCycleCount(4); // Total of four cards to deal

        // Define alternating dealing action
        dealTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            if (playerCards.size() <= dealerCards.size()) {
                // Deal to player if player has equal or fewer cards than dealer
                playerCards.add(shoe.drawCard());
            } else {
                // Deal to dealer otherwise
                Card dealerCard = shoe.drawCard();
                if (dealerCards.isEmpty()) {
                    dealerCard.setConcealed(true);
                }
                dealerCards.add(dealerCard);
            }

            // Update the scene after each card is dealt
            UpdateScene();
        }));

        // After all cards are dealt, calculate totals
        dealTimeline.setOnFinished(event -> {
            playerTotal = getTotal(playerCards);
            dealerTotal = getTotal(dealerCards);
            System.out.println("Player: " + playerTotal);
            System.out.println("Dealer: " + dealerTotal);
            if((playerCards.getFirst().getValue() == 1 && playerCards.get(1).getValue() == 10) || (playerCards.getFirst().getValue() == 10 && playerCards.get(1).getValue() == 1)) {//player BlackJack
                showOutcomeDialog();
            }
            if((dealerCards.get(1).peek() == 1 && dealerCards.getFirst().peek() == 10) || (dealerCards.get(1).peek() == 10 && dealerCards.getFirst().peek() == 1)){//dealer BlackJack
                dealersTurn();
            }
        });

        dealTimeline.play();
    }

    /**
     * @returns largest total under or equal to 21, if -1 then bust
     */
    public int getTotal(ArrayList<Card> cards) {
        int total = 0;
        boolean hasAce = false;
        for(Card c : cards){
            int value = c.getValue();
            if(value == 1 && !hasAce){
                hasAce = true;
                total+=11;
            } else if (value != -1) {
                total += value;
            }
        }
        if(total > 21 && hasAce) total -= 10;//ace turns from 11 to 1
        return total <= 21 ? total : -1;
    }
}
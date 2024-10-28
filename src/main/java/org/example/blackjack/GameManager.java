package org.example.blackjack;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
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
    private ArrayList<ArrayList<Card>> splitCards;
    private ArrayList<Integer> splitTotals;
    private ArrayList<Boolean> splitHandFinished;
    private  boolean hasSplit;
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
        for (Card card : playerCards) {
            ImageView cardImageView = new ImageView(card.getImage());
            cardImageView.setFitHeight(100);
            cardImageView.setPreserveRatio(true);
            playerCardBox.getChildren().add(cardImageView);
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


    public String decideWinner(){
        if(hasSplit){//check each split hand individually
            for(int t : splitTotals){
                System.out.println("Player: " + t);
                System.out.println("Dealer: " + dealerTotal);
                if(dealerTotal > 21 || (dealerTotal < t && t <= 21)){//player wins
                    System.out.println("player wins");
                    return "You Win!";
                } else if(dealerTotal == t) {//push
                    System.out.println("Push");
                    return "Push!";
                } else {//dealer wins
                    System.out.println("Dealer wins");
                    return "Dealer Wins";
                }
            }
        }
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
            int index = -1;
            for(int i = 0; i < splitHandFinished.size(); i++){
                if(!splitHandFinished.get(i)){//hand not finished so hit for this hand
                    index = i;
                    break;
                }
            }
            if(index != -1){
                splitCards.get(index).add(shoe.drawCard());
                splitTotals.set(index, getTotal(splitCards.get(index)));
                System.out.println(splitTotals.get(index));
                if(splitTotals.get(index) == -1 || splitTotals.get(index) > 21){//bust
                    splitHandFinished.set(index, true);
                    if(index == splitHandFinished.size()-1){//all split hands finished
                        dealersTurn();
                    }
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
            for(int i = 0; i < splitHandFinished.size(); i++){
                if(!splitHandFinished.get(i)){
                    splitHandFinished.set(i, true);
                    if(i != splitHandFinished.size()-1) return;//still have additional hands to finish
                }
            }
        }
        UpdateScene();
        dealersTurn();
    }

    public void Double(){
        if(hasSplit){
            for(int i = 0; i < splitHandFinished.size(); i++){
                if(!splitHandFinished.get(i)){
                    if(splitCards.get(i).size() != 2){
                        System.out.println("Cannot Double with more than 2 cards");
                        return;
                    } else if (i == splitHandFinished.size()-1){//all split hands done, dealer turn
                        Hit();
                        dealersTurn();
                    } else {
                        Hit();
                        System.out.println(splitTotals.get(i));
                        splitHandFinished.set(i, true);
                    }
                }
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

    public void Split(){
        //add check for multiple splits, not just first hand
        if(hasSplit){//find hand that can split
            for(int i = 0; i < splitCards.size(); i++){
                if(splitCards.get(i).getFirst() == splitCards.get(i).get(1)){//split found
                    splitCards.add((new ArrayList<>()));//create new hand
                    splitCards.getLast().add(splitCards.get(i).getLast());//add last card of hand being split to new hand
                    splitCards.get(i).removeLast();//remove last card because it was added to new hand
                    //Add 1 new card to each split hand
                    splitCards.get(i).add(shoe.drawCard());
                    splitCards.getLast().add(shoe.drawCard());
                    splitHandFinished.add(false);//make last hand not finished
                    //set new totals of the split hands with the new card
                    splitTotals.add(getTotal(splitCards.getLast()));
                    splitCards.set(i, splitCards.get(i));
                    System.out.println(splitTotals.get(i));
                }
            }
            System.out.println("No split hands Detected");
        }
        if(playerCards.getFirst().getValue() == playerCards.getLast().getValue()){//can split first hand
            hasSplit = true;
            splitCards = new ArrayList<>();
            splitTotals = new ArrayList<>();
            splitHandFinished = new ArrayList<>();
            splitCards.add(new ArrayList<Card>());
            splitCards.add(new ArrayList<Card>());
            splitCards.getFirst().add(playerCards.getFirst());
            splitCards.getFirst().add(shoe.drawCard());
            splitCards.getLast().add(playerCards.getLast());
            splitCards.getLast().add(shoe.drawCard());
            splitHandFinished.add(false);
            splitHandFinished.add(false);
            splitTotals.add(getTotal(splitCards.getFirst()));
            splitTotals.add(getTotal(splitCards.getLast()));
            System.out.println(splitTotals.getFirst());
        } else{
            System.out.println("Cannot Split this hand");
        }
        UpdateScene();
    }

    public void Surrender(){
        Deal();
    }

    public void InitialDeal() {
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
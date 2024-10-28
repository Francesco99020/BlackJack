package org.example.blackjack;

import javafx.scene.Scene;

import java.util.*;

public class GameManager {
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

    public GameManager(Shoe shoe, Scene gameScene){
        this.gameScene = gameScene;
        this.shoe = shoe;
        firstHandOfShoe = true;
    }

    private void UpdateScene(){

    }

    private void dealersTurn(){
        //flip dealers first card
        dealerCards.getFirst().setConcealed(false);
        dealerTotal = getTotal(dealerCards);
        while (dealerTotal < 17 && dealerTotal != -1){
            System.out.println("dealer total: " + dealerTotal);
            dealerHit();
        }
        decideWinner();
        Deal();
    }

    public void decideWinner(){
        if(hasSplit){//check each split hand individually
            for(int t : splitTotals){
                System.out.println("Player: " + t);
                System.out.println("Dealer: " + dealerTotal);
                if(dealerTotal > 21 || (dealerTotal < t && t <= 21)){//player wins
                    System.out.println("player wins");
                } else if(dealerTotal == t) {//push
                    System.out.println("Push");
                } else {//dealer wins
                    System.out.println("Dealer wins");
                }
            }
        }
        System.out.println("Player: " + playerTotal);
        System.out.println("Dealer: " + dealerTotal);
        if(dealerTotal > 21 || (dealerTotal < playerTotal && playerTotal <= 21)){//player wins
            System.out.println("player wins");
        } else if(dealerTotal == playerTotal) {//push
            System.out.println("Push");
        } else {//dealer wins
            System.out.println("Dealer wins");
        }
    }

    public void dealerHit(){
        dealerCards.add(shoe.drawCard());
        dealerTotal = getTotal(dealerCards);
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
                decideWinner();
                Deal();
            }
        }
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
    }

    public void Surrender(){
        Deal();
    }

    public void InitialDeal(){
        if(firstHandOfShoe){//discard first card
            shoe.drawCard();
            firstHandOfShoe = false;
        }
        playerCards = new ArrayList<>();
        dealerCards = new ArrayList<>();

        playerCards.add(shoe.drawCard());

        Card dealerCard = shoe.drawCard();
        dealerCard.setConcealed(true);
        dealerCards.add(dealerCard);

        playerCards.add(shoe.drawCard());

        dealerCards.add(shoe.drawCard());

        playerTotal = getTotal(playerCards);
        dealerTotal = getTotal(dealerCards);
        System.out.println("Player: " + playerTotal);
        System.out.println("Dealer: " + dealerTotal);
    }

    public void Deal(){
        if(hasSplit){
            hasSplit=false;
        }
        playerCards = new ArrayList<>();
        dealerCards = new ArrayList<>();

        playerCards.add(shoe.drawCard());

        Card dealerCard = shoe.drawCard();
        dealerCard.setConcealed(true);
        dealerCards.add(dealerCard);

        playerCards.add(shoe.drawCard());

        dealerCards.add(shoe.drawCard());

        playerTotal = getTotal(playerCards);
        dealerTotal = getTotal(dealerCards);
        System.out.println("Player: " + playerTotal);
        System.out.println("Dealer: " + dealerTotal);
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
package org.example.blackjack;

import java.util.*;

public class Shoe {
    private final Stack<Card> shoe;

    public Shoe(Deck[] decks){
        shoe = new Stack<Card>();
        ArrayList<Card> allCards = new ArrayList<>();
        for(Deck d : decks){
            d.shuffle();
            while (d.remainingCards() != 0){
                allCards.add(d.drawCard());
            }
        }
        Collections.shuffle(allCards, new Random());
        for(Card c : allCards){
            shoe.push(c);
        }
    }

    public Card drawCard(){
        System.out.println("Cards remaining in shoe: " + cardsInShoe());
        return shoe.pop();
    }

    public int cardsInShoe(){
        return shoe.size();
    }
}
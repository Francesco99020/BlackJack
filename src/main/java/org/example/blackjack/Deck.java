package org.example.blackjack;

import javafx.scene.image.Image;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    private final List<Card> deck;

    public Deck() {
        this.deck = new ArrayList<>();
        initializeDeck();
    }

    private void initializeDeck() {
//        deck.add(new Card(1, new Image(String.valueOf(getClass().getResource("/images/002-ace.png")))));
//        deck.add(new Card(1, new Image(String.valueOf(getClass().getResource("/images/007-club-card-1.png")))));
//        deck.add(new Card(1, new Image(String.valueOf(getClass().getResource("/images/008-heart-card-1.png")))));
//        deck.add(new Card(1, new Image(String.valueOf(getClass().getResource("/images/013-diamond-3.png")))));
//        deck.add(new Card(2, new Image(String.valueOf(getClass().getResource("/images/015-club-card-4.png")))));
//        deck.add(new Card(2, new Image(String.valueOf(getClass().getResource("/images/017-spades-4.png")))));
//        deck.add(new Card(2, new Image(String.valueOf(getClass().getResource("/images/031-diamond-7.png")))));
//        deck.add(new Card(2, new Image(String.valueOf(getClass().getResource("/images/041-heart-card-7.png")))));
//        deck.add(new Card(3, new Image(String.valueOf(getClass().getResource("/images/021-heart-card-2.png")))));
//        deck.add(new Card(3, new Image(String.valueOf(getClass().getResource("/images/004-club-card.png")))));
//        deck.add(new Card(3, new Image(String.valueOf(getClass().getResource("/images/023-spades-6.png")))));
//        deck.add(new Card(3, new Image(String.valueOf(getClass().getResource("/images/050-diamond-12.png")))));
//        deck.add(new Card(4, new Image(String.valueOf(getClass().getResource("/images/010-diamond-2.png")))));
//        deck.add(new Card(4, new Image(String.valueOf(getClass().getResource("/images/026-spades-8.png")))));
//        deck.add(new Card(4, new Image(String.valueOf(getClass().getResource("/images/034-heart-card-5.png")))));
//        deck.add(new Card(4, new Image(String.valueOf(getClass().getResource("/images/040-club-card-8.png")))));
//        deck.add(new Card(5, new Image(String.valueOf(getClass().getResource("/images/003-heart-card.png")))));
//        deck.add(new Card(5, new Image(String.valueOf(getClass().getResource("/images/024-spades-7.png")))));
//        deck.add(new Card(5, new Image(String.valueOf(getClass().getResource("/images/043-diamond-11.png")))));
//        deck.add(new Card(5, new Image(String.valueOf(getClass().getResource("/images/030-club-card-6.png")))));
//        deck.add(new Card(6, new Image(String.valueOf(getClass().getResource("/images/032-diamond-8.png")))));
//        deck.add(new Card(6, new Image(String.valueOf(getClass().getResource("/images/016-spades-3.png")))));
//        deck.add(new Card(6, new Image(String.valueOf(getClass().getResource("/images/047-heart-card-9.png")))));
//        deck.add(new Card(6, new Image(String.valueOf(getClass().getResource("/images/049-club-card-12.png")))));
//        deck.add(new Card(7, new Image(String.valueOf(getClass().getResource("/images/006-spades-1.png")))));
//        deck.add(new Card(7, new Image(String.valueOf(getClass().getResource("/images/009-diamond-1.png")))));
//        deck.add(new Card(7, new Image(String.valueOf(getClass().getResource("/images/025-club-card-5.png")))));
//        deck.add(new Card(7, new Image(String.valueOf(getClass().getResource("/images/036-heart-card-6.png")))));
//        deck.add(new Card(8, new Image(String.valueOf(getClass().getResource("/images/012-spades-2.png")))));
//        deck.add(new Card(8, new Image(String.valueOf(getClass().getResource("/images/052-heart-card-12.png")))));
//        deck.add(new Card(8, new Image(String.valueOf(getClass().getResource("/images/029-diamond-6.png")))));
//        deck.add(new Card(8, new Image(String.valueOf(getClass().getResource("/images/042-club-card-9.png")))));
//        deck.add(new Card(9, new Image(String.valueOf(getClass().getResource("/images/038-diamond-10.png")))));
//        deck.add(new Card(9, new Image(String.valueOf(getClass().getResource("/images/045-club-card-10.png")))));
//        deck.add(new Card(9, new Image(String.valueOf(getClass().getResource("/images/051-heart-card-11.png")))));
//        deck.add(new Card(9, new Image(String.valueOf(getClass().getResource("/images/005-spades.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/011-club-card-2.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/014-club-card-3.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/018-spades-5.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/019-diamond-4.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/022-diamond-5.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/020-spade-card.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/027-heart-card-3.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/028-spades-9.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/033-heart-card-4.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/035-spades-10.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/037-diamond-9.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/039-club-card-7.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/044-heart-card-8.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/046-club-card-11.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/048-heart-card-10.png")))));
        deck.add(new Card(10, new Image(String.valueOf(getClass().getResource("/images/001-diamond.png")))));

    }

    public void shuffle() {
        Collections.shuffle(deck, new Random());
    }

    public Card drawCard() {
        if (!deck.isEmpty()) {
            return deck.removeFirst();
        }
        throw new IllegalStateException("No cards left in the deck");
    }

    public int remainingCards() {
        return deck.size();
    }
}

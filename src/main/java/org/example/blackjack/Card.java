package org.example.blackjack;

import javafx.scene.image.Image;

import java.util.Objects;

public class Card{
    private final int value;
    private final Image image;

    private final Image concealedImage;
    private boolean isConcealed;
    public Card(int value, Image image){
        this.value = value;
        this.image = image;
        isConcealed = false;
        concealedImage = new Image(Objects.requireNonNull(getClass().getResource("/images/playing-card-back.png")).toExternalForm());
    }

    public boolean isConcealed() {
        return isConcealed;
    }

    public Image getImage() {
        return isConcealed ? concealedImage : image;
    }

    /**
     * @return card value, if -1 then card is concealed, if 1 then card is Ace (ie 1 / 11)
     */
    public int getValue() {
        return isConcealed ? -1 : value;
    }

    public  int peek(){return  value;}

    public void setConcealed(boolean concealed) {
        isConcealed = concealed;
    }
}

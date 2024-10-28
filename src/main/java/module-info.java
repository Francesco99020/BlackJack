module org.example.blackjack {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.blackjack to javafx.fxml;
    exports org.example.blackjack;
}
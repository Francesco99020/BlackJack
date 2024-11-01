package org.example.blackjack;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    private Stage primaryStage;

    private Scene titleScene;

    private Scene gameScene;

    private Shoe shoe;

    private HBox PlayerCardBox;
    private HBox DealerCardBox;
    private GameManager gameManager;
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        titleScene = createTitleScene(primaryStage);

        primaryStage.setTitle("Blackjack");
        primaryStage.setScene(titleScene);
        primaryStage.show();
    }

    public Scene createTitleScene(Stage primaryStage) {
        // Create a title text
        Text titleText = new Text("Blackjack");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        titleText.setFill(Color.GREEN);

        // Create buttons
        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        startButton.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white;");

        Button exitButton = new Button("Exit");
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        exitButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;");

        // Set button actions
        startButton.setOnAction(e -> startGame(primaryStage));
        exitButton.setOnAction(e -> primaryStage.close());

        // Arrange buttons in a vertical box
        VBox buttonBox = new VBox(20, startButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Create a BorderPane to hold title and buttons
        BorderPane root = new BorderPane();
        root.setTop(titleText);
        BorderPane.setAlignment(titleText, Pos.CENTER);
        root.setCenter(buttonBox);

        // Set the background color to resemble a casino table (dark green)
        root.setStyle("-fx-background-color: darkgreen;");

        // Create and return the scene
        return new Scene(root, 600, 400);
    }

    public Scene createGameScene(Stage primaryStage) {
        AnchorPane root = new AnchorPane();

        // Load Background Image
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResource("/images/Table.png")).toExternalForm());
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());

        // Load Card Deck Image
        Image cardDeck = new Image(Objects.requireNonNull(getClass().getResource("/images/card-deck.png")).toExternalForm());
        ImageView cardDeckImageView = new ImageView(cardDeck);
        cardDeckImageView.setPreserveRatio(true);
        cardDeckImageView.setFitHeight(250);
        cardDeckImageView.setTranslateX(1500);

        HBox playerCardBox = new HBox(10);
        playerCardBox.setAlignment(Pos.CENTER);
        AnchorPane.setBottomAnchor(playerCardBox, 100.0); // Position above buttons

        HBox dealerCardBox = new HBox(10);
        dealerCardBox.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(dealerCardBox, 100.0); // Position near top

        // Center playerCardBox and dealerCardBox horizontally
        playerCardBox.translateXProperty().bind(root.widthProperty().subtract(playerCardBox.widthProperty()).divide(2));
        dealerCardBox.translateXProperty().bind(root.widthProperty().subtract(dealerCardBox.widthProperty()).divide(2));

        PlayerCardBox = playerCardBox;
        DealerCardBox = dealerCardBox;

        // Create buttons
        Button hitButton = new Button("Hit");
        hitButton.setOnAction(e -> handleHit());
        Button standButton = new Button("Stand");
        standButton.setOnAction(e -> handleStand());
        Button doubleButton = new Button("Double");
        doubleButton.setOnAction(e -> handleDouble());
        Button splitButton = new Button("Split");
        splitButton.setOnAction(e -> handleSplit());
        Button surrenderButton = new Button("Surrender");
        surrenderButton.setOnAction(e -> handleSurrender());

        // Style buttons
        hitButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        standButton.setStyle("-fx-font-size: 14px; -fx-background-color: #f44336; -fx-text-fill: white;");
        doubleButton.setStyle("-fx-font-size: 14px; -fx-background-color: #FF9800; -fx-text-fill: white;");
        splitButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2196F3; -fx-text-fill: white;");
        surrenderButton.setStyle("-fx-font-size: 14px; -fx-background-color: #9C27B0; -fx-text-fill: white;");

        // Arrange buttons in HBox
        HBox buttonBox = new HBox(20, hitButton, standButton, doubleButton, splitButton, surrenderButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setTranslateY(-20); // Offset from the bottom
        buttonBox.prefWidthProperty().bind(root.widthProperty());

        // Add background, card deck, and buttons to the scene
        root.getChildren().addAll(backgroundImageView, cardDeckImageView, buttonBox, playerCardBox, dealerCardBox);
        AnchorPane.setBottomAnchor(buttonBox, 20.0); // Position buttonBox at the bottom

        return new Scene(root, 1200, 800);
    }


    private void startGame(Stage primaryStage) {
        gameScene = createGameScene(primaryStage);
        primaryStage.setScene(gameScene);
        primaryStage.setMaximized(true);

        Deck[] playDecks = new Deck[6];
        playDecks[0] = new Deck();
        playDecks[1] = new Deck();
        playDecks[2] = new Deck();
        playDecks[3] = new Deck();
        playDecks[4] = new Deck();
        playDecks[5] = new Deck();

        shoe = new Shoe(playDecks);

        gameManager = new GameManager(shoe, gameScene, PlayerCardBox, DealerCardBox);
        gameManager.InitialDeal();
        gameManager.UpdateScene();

        primaryStage.show();
    }

    private void handleHit(){
        System.out.println("Hit");
        gameManager.Hit();
    }

    private void handleStand(){
        System.out.println("Stand");
        gameManager.Stand();
    }

    private void handleDouble(){
        System.out.println("Double");
        gameManager.Double();
    }

    private void handleSplit(){
        System.out.println("Split");
        gameManager.Split();
    }

    private void handleSurrender(){
        System.out.println("Surrender");
        gameManager.Surrender();
    }
}

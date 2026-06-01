import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane menu = createmenu();

        Scene scene = new Scene(menu, 800, 700);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(scene);
        primaryStage.show();


    }
    public static void main(String[] args){
        launch(args);
    }
    private Pane createmenu(){
        Pane menu = new Pane();
        menu.setStyle("-fx-background-image: src/main/resources/download.jpg");

        Label highscore = new Label("Endless Highscore: ");
        highscore.setStyle("-fx-font-weight: bold");
        HBox leftCorner = new HBox(10, highscore);
        leftCorner.setAlignment(Pos.BOTTOM_LEFT);
        leftCorner.setLayoutX(0);
        leftCorner.setLayoutY(675);

        Media menuMusic = new Media(new File("src/main/resources/Menu.mp3").toURI().toString());
        MediaPlayer menuPlayer = new MediaPlayer(menuMusic);
        menuPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        menuPlayer.play();

        Label gameTitle = new Label("SPACE INVADERS:\n     EVOLUTION");
        gameTitle.setStyle("-fx-font-weight: bold");
        gameTitle.setAlignment(Pos.CENTER);
        gameTitle.setLayoutX(350);
        gameTitle.setLayoutY(100);
        Label selecioneNivel = new Label("Selecione o Nível");
        selecioneNivel.setAlignment(Pos.CENTER);
        selecioneNivel.setLayoutX(350);
        selecioneNivel.setLayoutY(150);
        Button easy = new Button("EASY");
        easy.setPrefWidth(100);
        Button hard = new Button("HARD");
        hard.setPrefWidth(100);
        Button endless = new Button("ENDLESS");
        endless.setPrefWidth(100);
        easy.setStyle("-fx-background-color: green;" +
                "-fx-text-fill: white;" + "-fx-font-size: 18px;");
        hard.setStyle("-fx-background-color: red;" +
                "-fx-text-fill: white;" + "-fx-font-size: 18px;");
        endless.setStyle("-fx-background-color: blue;" +
                "-fx-text-fill: white;" + "-fx-font-size: 18px;");

        VBox buttonsContainer = new VBox(20);
        buttonsContainer.setLayoutX(350); // Center the buttons
        buttonsContainer.setLayoutY(300);
        buttonsContainer.getChildren().addAll(easy, hard, endless);

        menu.getChildren().addAll(gameTitle, selecioneNivel, buttonsContainer, leftCorner);
        return menu;
    }

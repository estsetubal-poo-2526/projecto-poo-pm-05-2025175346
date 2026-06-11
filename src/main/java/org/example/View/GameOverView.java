package org.example.View;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.File;

public class GameOverView extends VBox {

    public GameOverView(App app, int score, int highScore) {
        super(25);

        AudioClip gameoverSound = new AudioClip(new File("src/main/resources/Sounds/gameover.wav").toURI().toString());
        gameoverSound.play();

        this.setPrefWidth(App.WIDTH);
        this.setPrefHeight(App.HEIGHT);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(50, 20, 50, 20));
        this.setStyle("-fx-background-color: #05050a;");

        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Impact", FontWeight.BOLD, 46));
        gameOverText.setFill(Color.RED);
        gameOverText.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255, 0, 0, 0.8), 15, 0, 0, 0);");

        VBox blocoScores = new VBox(10);
        blocoScores.setAlignment(Pos.CENTER);
        blocoScores.setStyle("-fx-background-color: rgba(20, 20, 35, 0.7); -fx-background-radius: 10px; -fx-padding: 15px; -fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1px;");
        blocoScores.setMaxWidth(220);

        Text scoreText = new Text("A Tua Pontuação: " + (score < 0 ? 0 : score));
        scoreText.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        scoreText.setFill(Color.WHITE);

        Text recordeText = new Text("Recorde Atual: " + highScore);
        recordeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
        recordeText.setFill(Color.GOLD);
        blocoScores.getChildren().addAll(scoreText, recordeText);

        Button btnAgain = new Button("Tentar Novamente");
        Button btnLeave = new Button("Sair do Jogo");
        btnAgain.setPrefWidth(180); btnAgain.setPrefHeight(45);
        btnLeave.setPrefWidth(180); btnLeave.setPrefHeight(45);

        configurarEstiloBotao(btnAgain, "#ff3333", "rgba(50, 10, 10, 0.7)");
        configurarEstiloBotao(btnLeave, "#00bfff", "rgba(10, 10, 35, 0.7)");

        btnAgain.setOnAction(event -> app.restartGame());
        btnLeave.setOnAction(event -> System.exit(0));

        this.getChildren().addAll(gameOverText, blocoScores, btnAgain, btnLeave);
    }

    private void configurarEstiloBotao(Button btn, String corHex, String rgbaFundo) {
        btn.setStyle("-fx-background-color: " + rgbaFundo + "; -fx-text-fill: " + corHex + "; -fx-border-color: " + corHex + "; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(three-pass-box, " + corHex + ", 10, 0, 0, 0);");
        ScaleTransition aumentar = new ScaleTransition(Duration.millis(150), btn);
        aumentar.setToX(1.1); aumentar.setToY(1.1);
        ScaleTransition diminuir = new ScaleTransition(Duration.millis(150), btn);
        diminuir.setToX(1); diminuir.setToY(1);
        btn.setOnMouseEntered(e -> { diminuir.stop(); aumentar.play(); });
        btn.setOnMouseExited(e -> { aumentar.stop(); diminuir.play(); });
    }
}
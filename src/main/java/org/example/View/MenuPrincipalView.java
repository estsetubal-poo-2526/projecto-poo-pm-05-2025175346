package org.example.View;

import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.example.Models.AlienComum;
import org.example.Models.AlienRapido;
import org.example.Models.AlienTanque;
import java.io.InputStream;

public class MenuPrincipalView extends Pane {

    public MenuPrincipalView(App app, int highScore) {
        this.setStyle("-fx-background-color: #05050a;");

        InputStream streamImagem = getClass().getResourceAsStream("/Images/FundoMenu1.jpg");
        if (streamImagem == null) {
            streamImagem = getClass().getResourceAsStream("Images/FundoMenu1.jpg");
        }

        if (streamImagem != null) {
            Image fundoImg = new Image(streamImagem);
            ImageView fundo1 = new ImageView(fundoImg);
            fundo1.setFitWidth(App.WIDTH);
            fundo1.setFitHeight(App.HEIGHT);
            fundo1.setSmooth(true);

            ImageView fundo2 = new ImageView(fundoImg);
            fundo2.setFitWidth(App.WIDTH);
            fundo2.setFitHeight(App.HEIGHT);
            fundo2.setY(-App.HEIGHT);
            fundo2.setSmooth(true);

            this.getChildren().addAll(fundo1, fundo2);

            AnimationTimer motorFundoFluido = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    double velocidade = 1.0;
                    fundo1.setY(fundo1.getY() + bandwidthCorrection(fundo1.getY(), velocidade));
                    fundo2.setY(fundo2.getY() + velocidade);

                    if (fundo1.getY() >= App.HEIGHT) fundo1.setY(fundo2.getY() - App.HEIGHT);
                    if (fundo2.getY() >= App.HEIGHT) fundo2.setY(fundo1.getY() - App.HEIGHT);
                }

                private double bandwidthCorrection(double currentY, double vel) {
                    return vel;
                }
            };
            motorFundoFluido.start();
        }

        VBox contentorTitulo = new VBox(-5);
        contentorTitulo.setAlignment(Pos.CENTER);

        Label txtSpace = new Label("SPACE INVADERS");
        txtSpace.setFont(Font.font("Impact", FontWeight.BOLD, 32));
        txtSpace.setTextFill(Color.web("#00ffcc"));
        txtSpace.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 204, 0.6), 12, 0, 0, 0);");

        Label txtEvolution = new Label("EVOLUTION");
        txtEvolution.setFont(Font.font("Impact", FontWeight.BOLD, 24));
        txtEvolution.setTextFill(Color.WHITE);
        txtEvolution.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255, 255, 255, 0.4), 8, 0, 0, 0);");
        contentorTitulo.getChildren().addAll(txtSpace, txtEvolution);

        VBox painelCentral = new VBox(20);
        painelCentral.setAlignment(Pos.CENTER);
        painelCentral.setMaxWidth(240);
        painelCentral.setStyle("-fx-background-color: rgba(15, 15, 30, 0.82); -fx-background-radius: 15px; -fx-border-color: rgba(0, 255, 204, 0.2); -fx-border-width: 1.5px; -fx-border-radius: 15px; -fx-padding: 25px;");

        Label recordeMenuLabel = new Label("RECORDE ENDLESS\n" + highScore);
        recordeMenuLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-alignment: center; -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.5), 10, 0, 0, 0);");

        VBox caixasOpcoes = new VBox(15);
        caixasOpcoes.setAlignment(Pos.CENTER);

        Button btnFacil = new Button("Fácil");
        Button btnMedio = new Button("Médio");
        Button btnEndless = new Button("EndLess");

        btnFacil.setPrefWidth(150); btnFacil.setPrefHeight(45);
        btnMedio.setPrefWidth(150); btnMedio.setPrefHeight(45);
        btnEndless.setPrefWidth(150); btnEndless.setPrefHeight(45);

        configurarAnimacaoBotao(btnFacil, "#00ff00", "rgba(10, 50, 10, 0.75)");
        configurarAnimacaoBotao(btnMedio, "#ff9900", "rgba(50, 30, 10, 0.75)");
        configurarAnimacaoBotao(btnEndless, "#00bfff", "rgba(10, 20, 50, 0.75)");

        btnFacil.setOnAction(event -> {
            app.setModoDificuldade("FACIL");
            AlienComum.SPEED = 0.6; AlienRapido.SPEED = 1.3; AlienTanque.SPEED = 0.3;
            app.startGame();
        });

        btnMedio.setOnAction(event -> {
            app.setModoDificuldade("MEDIO");
            AlienComum.SPEED = 1.2; AlienRapido.SPEED = 2.2; AlienTanque.SPEED = 0.6;
            app.startGame();
        });

        btnEndless.setOnAction(event -> {
            app.setModoDificuldade("ENDLESS");
            AlienComum.SPEED = 1.6; AlienRapido.SPEED = 2.8; AlienTanque.SPEED = 0.9;
            app.startGame();
        });

        caixasOpcoes.getChildren().addAll(btnFacil, btnMedio, btnEndless);

        Region separadorEspaco = new Region();
        separadorEspaco.setPrefHeight(2);

        painelCentral.getChildren().addAll(recordeMenuLabel, separadorEspaco, caixasOpcoes);

        VBox layoutVerticalMenu = new VBox(45);
        layoutVerticalMenu.setAlignment(Pos.TOP_CENTER);
        layoutVerticalMenu.setPadding(new Insets(60, 0, 0, 0));
        layoutVerticalMenu.setPrefWidth(App.WIDTH);
        layoutVerticalMenu.setPrefHeight(App.HEIGHT);
        layoutVerticalMenu.getChildren().addAll(contentorTitulo, painelCentral);

        this.getChildren().add(layoutVerticalMenu);
    }

    private void configurarAnimacaoBotao(Button btn, String corHex, String rgbaFundo) {
        btn.setStyle("-fx-background-color: " + rgbaFundo + "; -fx-text-fill: " + corHex + "; -fx-border-color: " + corHex + "; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(three-pass-box, " + corHex + ", 8, 0, 0, 0);");
        ScaleTransition aumentar = new ScaleTransition(Duration.millis(120), btn);
        aumentar.setToX(1.1); aumentar.setToY(1.1);
        ScaleTransition diminuir = new ScaleTransition(Duration.millis(120), btn);
        diminuir.setToX(1); diminuir.setToY(1);
        btn.setOnMouseEntered(e -> { diminuir.stop(); aumentar.play(); });
        btn.setOnMouseExited(e -> { aumentar.stop(); diminuir.play(); });
    }
}
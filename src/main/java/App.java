package org.example;

import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import java.io.InputStream;

public class appStart extends Application {

    @Override
    public void start(Stage primaryStage) {
        /* Primeiro passo: criar um palco, isto é uma janela de exibição da nossa interface gráfica! */
        Scene menu1 = menu1();
        primaryStage.setScene(menu1);
        primaryStage.setTitle("Space Invaders Desevolucion");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /* Segundo passo: criar uma cena para colocar no nosso palco, definir largura, altura, e um tipo de layout para utilizar */
    public Scene menu1() {
        /* Definição de um titulo para o Menu */
        Label tituloMenu = new Label("Space Invadares Desevolucion");
        tituloMenu.setStyle("-fx-text-fill: #00ffcc;" +
                " -fx-font-size: 24px;" +
                " -fx-font-weight: bold;");

        /* Definição de um layout a utilizar para o nosso menu */
        BorderPane backGroundMenu = new BorderPane();

        BorderPane.setAlignment(tituloMenu, Pos.CENTER);
        BorderPane.setMargin(tituloMenu, new Insets(40, 0, 0, 0));
        backGroundMenu.setTop(tituloMenu);

        /* Definição de um segundo layout a utilizar para as caixas de opções do nosso menu, vBox */
        VBox caixasOpcoes = new VBox(25);

        /* Retangulo à volta das opções */
        caixasOpcoes.setMaxWidth(220); // Limitar a largura da minha Vertical Box
        caixasOpcoes.setMaxHeight(300); // Limitar a altura da minha Vertical Box

        /* Definição das nossas caixas de opções (Easy, Hard, EndLess) */
        Button btnFacil = new Button("Fácil");
        Button btnMedio = new Button("Médio");
        Button btnEndless = new Button("EndLess");

        /* Definição do tamanho dos nossos botões */
        btnFacil.setPrefWidth(150); btnFacil.setPrefHeight(50);
        btnMedio.setPrefWidth(150); btnMedio.setPrefHeight(50);
        btnEndless.setPrefWidth(150); btnEndless.setPrefHeight(50);

        /* Animação dos botões */
        ScaleTransition aumentarF = new ScaleTransition(Duration.millis(150), btnFacil);
        aumentarF.setToX(1.15); aumentarF.setToY(1.15);

        ScaleTransition diminuirF = new ScaleTransition(Duration.millis(150), btnFacil);
        diminuirF.setToX(1); diminuirF.setToY(1);

        ScaleTransition aumentarM = new ScaleTransition(Duration.millis(150), btnMedio);
        aumentarM.setToX(1.15); aumentarM.setToY(1.15);

        ScaleTransition diminuirM = new ScaleTransition(Duration.millis(150), btnMedio);
        diminuirM.setToX(1); diminuirM.setToY(1);

        ScaleTransition aumentarE = new ScaleTransition(Duration.millis(150), btnEndless);
        aumentarE.setToX(1.15); aumentarE.setToY(1.15);

        ScaleTransition diminuirE = new ScaleTransition(Duration.millis(150), btnEndless);
        diminuirE.setToX(1.00); diminuirE.setToY(1.00);

        btnFacil.setOnMouseEntered(event -> { diminuirF.stop(); aumentarF.play(); });
        btnFacil.setOnMouseExited(event -> { aumentarF.stop(); diminuirF.play(); });

        btnMedio.setOnMouseEntered(event -> { diminuirM.stop(); aumentarM.play(); });
        btnMedio.setOnMouseExited(event -> { aumentarM.stop(); diminuirM.play(); });

        btnEndless.setOnMouseEntered(event -> { diminuirE.stop(); aumentarE.play(); });
        btnEndless.setOnMouseExited(event -> { aumentarE.stop(); diminuirE.play(); });

        // TODO
        btnFacil.setOnAction(event -> {});

        /* Adiciona os botões à Vertical Box */
        caixasOpcoes.getChildren().addAll(btnFacil, btnMedio, btnEndless);

        /* Centrar os nossos botões */
        caixasOpcoes.setAlignment(Pos.CENTER);

        /* Estilos CSS das Caixas e Botões */
        btnFacil.setStyle("-fx-background-color: rgba(10, 30, 10, 0.6);" +
                          "-fx-text-fill: white;" +
                          "-fx-background-radius: 15px;" +
                          "-fx-font-weight: bold; " +
                          "-fx-font-size: 14px;"
        );


        btnMedio.setStyle("-fx-background-color: rgba(30, 20, 10, 0.6); " +   // Fundo laranja escuro semi-transparente
                          "-fx-text-fill: #ff9900; " +                         // Texto laranja laser
                          "-fx-border-color: #ff9900; " +                      // Borda laranja laser
                          "-fx-border-width: 2px; " +
                          "-fx-border-radius: 8px; " +
                          "-fx-background-radius: 8px; " +
                          "-fx-font-weight: bold; " +
                          "-fx-font-size: 14px; " +
                          "-fx-effect: dropshadow(three-pass-box, #ff9900, 10, 0, 0, 0);");

        btnEndless.setStyle("-fx-background-color: Blue; -fx-text-fill: white; -fx-background-radius: 15px; -fx-font-weight: bold; -fx-font-size: 14px;");

        caixasOpcoes.setStyle(
                "-fx-background-color: rgba(20, 20, 35, 0.7); " +
                        "-fx-background-radius: 15px; " +
                        "-fx-border-color: #ffffff; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 15px; " +
                        "-fx-padding: 30px;"
        );

        /* Fundo do menu1 - Lógica de Carregamento e Animação */
        InputStream streamImagem = getClass().getResourceAsStream("/FundoMenu1.jpg");

        if (streamImagem == null) {
            // Plano de contingência: Se falhar a imagem, aplica cor sólida escura
            backGroundMenu.setStyle("-fx-background-color: #0b0b16;");
            System.out.println("ERRO: O ficheiro FundoMenu1.jpg não foi encontrado na pasta resources! A usar fundo de segurança.");
        } else {
            Image fundoMenuImg = new Image(streamImagem);
            final double[] deslocamentoY = {0.0};

            AnimationTimer motorFundo = new AnimationTimer() {

                /* A cada fração de segundo o Animation Timer executa o metodo handle */
                @Override
                public void handle(long now) { // O argumento now dá o tempo atual do sistema em nanossegundos
                    deslocamentoY[0] += 0.5; // A cada frame, soma 0.5 píxeis à nossa posição vertical

                    if (deslocamentoY[0] >= 800) {
                        deslocamentoY[0] = 0.0;
                    }

                    BackgroundPosition novaPosicao = new BackgroundPosition(
                            javafx.geometry.Side.LEFT, 0, false,
                            javafx.geometry.Side.TOP, deslocamentoY[0], false
                    );

                    BackgroundImage configMovimento = new BackgroundImage(
                            fundoMenuImg,
                            BackgroundRepeat.REPEAT,
                            BackgroundRepeat.REPEAT,
                            novaPosicao,
                            BackgroundSize.DEFAULT
                    );

                    backGroundMenu.setBackground(new Background(configMovimento));
                }
            };
            motorFundo.start();
        }

        // Posiciona a caixa de botões no centro do painel principal
        backGroundMenu.setCenter(caixasOpcoes);

        // Criação e retorno final da Scene (Válido para qualquer fluxo do IF)
        Scene cenaMenu = new Scene(backGroundMenu, 500, 800);
        return cenaMenu;
    }
}

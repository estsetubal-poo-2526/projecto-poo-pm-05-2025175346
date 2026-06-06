import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.TextField;
import javafx.util.Duration;

public class appStart extends Application {

    public static final int WIDTH = 350;

    public static final int HEIGHT = 800;

    public void start(Stage primaryStage) {
        /*Primeiro passo criar um palco, isto é uma janela de exibição da nossa interface gráfica!*/

        Stage uniqueStage = new Stage();

        Scene menu1 = menu1();
        uniqueStage.setScene(menu1);
        uniqueStage.fullScreenProperty();
        uniqueStage.show();

    }

    public static void main(String[] args) {
        launch();
    }


    /*Segundo passo, criar uma cena para colocar no nosso palco, definir largura, altura, e um tipo de layout para utilizar*/

    public Scene menu1() {
        /* Definição de um titulo para o Menu */

        Label tituloMenu = new Label("Space Invadares Desevolucion");
        tituloMenu.setStyle("-fx-text-fill: #00ffcc;" +
                " -fx-font-size: 24px;" +
                " -fx-font-weight: bold;");

        /* Definição de um layout a utilizar para o nosso menu, Pane(Tabuleiro Livre - Pode ser bom para trabalhar com o backGround do nosso menu)*/

        BorderPane backGroundMenu = new BorderPane();

        BorderPane.setAlignment(tituloMenu, Pos.CENTER);
        BorderPane.setMargin(tituloMenu, new Insets(40, 0, 0, 0));
        backGroundMenu.setTop(tituloMenu);

        backGroundMenu.setStyle("-fx-background-color: #0b0b16;");

        /* Definição de um segundo layout a utilizar para as caixas de opções do nosso menu, vBox(Vai colocar os Nodes verticalmente) */

        VBox caixasOpcoes = new VBox(25);

        /* Definição das nossas caixas de opções (Easy, Hard, EndLess) */

        Button btnFacil = new Button("Fácil");
        Button btnMedio = new Button("Médio");
        Button btnEndless = new Button("Endless");

        /*Definição do tamnho dos nosso botões */

        btnFacil.setPrefWidth(150);
        btnFacil.setPrefHeight(50);
        btnMedio.setPrefWidth(150);
        btnMedio.setPrefHeight(50);
        btnEndless.setPrefWidth(150);
        btnEndless.setPrefHeight(50);

        /*Animação dos botões + SetOnAction */

        ScaleTransition aumentarF = new ScaleTransition(Duration.millis(150), btnFacil);
        aumentarF.setToX(1.15); //Aumenta o botão na  horizontal em 15%
        aumentarF.setToY(1.15); // Aumenta o botão na vertical em 15%

        ScaleTransition diminuirF = new ScaleTransition(Duration.millis(150), btnFacil);
        diminuirF.setToX(1); //Diminui o botão na vertical em 15%
        diminuirF.setToY(1); //Diminui o botão na vertical em 15%

        ScaleTransition aumentarM = new ScaleTransition(Duration.millis(150), btnMedio);
        aumentarM.setToX(1.15); //Aumenta o botão na  horizontal em 15%
        aumentarM.setToY(1.15); // Aumenta o botão na vertical em 15%

        ScaleTransition diminuirM = new ScaleTransition(Duration.millis(150), btnMedio);
        diminuirM.setToX(1); //Diminui o botão na vertical em 15%
        diminuirM.setToY(1); //Diminui o botão na vertical em 15%

        ScaleTransition aumentarE = new ScaleTransition(Duration.millis(150), btnEndless);
        aumentarE.setToX(1.15); //Aumenta o botão na  horizontal em 15%
        aumentarE.setToY(1.15); // Aumenta o botão na vertical em 15%

        ScaleTransition diminuirE = new ScaleTransition(Duration.millis(150), btnEndless);
        diminuirE.setToX(1.00); //Diminui o botão na vertical em 15%
        diminuirE.setToY(1.00); //Diminui o botão na vertical em 15%

        caixasOpcoes.getChildren().addAll(btnFacil, btnMedio, btnEndless);


        btnFacil.setOnMouseEntered(event ->{
            diminuirF.stop();
            aumentarF.play();
        });

        btnFacil.setOnMouseExited(event -> {
            aumentarF.stop();
            diminuirF.play();
        });


        btnMedio.setOnMouseEntered(event ->{
            diminuirM.stop();
            aumentarM.play();
        });

        btnMedio.setOnMouseExited(event -> {
            aumentarM.stop();
            diminuirM.play();
        });

        btnEndless.setOnMouseEntered(event ->{
            diminuirE.stop();
            aumentarE.play();
        });

        btnEndless.setOnMouseExited(event -> {
            aumentarE.stop();
            diminuirE.play();
        });

        btnFacil.setOnAction(event -> {

        });

        /* Centrar os nosso botões*/

        caixasOpcoes.setAlignment(Pos.CENTER);
        caixasOpcoes.setStyle("");

        backGroundMenu.setCenter(caixasOpcoes);

        Scene menu1 = new Scene(backGroundMenu, 500, 800);
        menu1.setRoot(backGroundMenu);

        /* CSS do Java */

        btnFacil.setStyle(
                "-fx-background-color: green; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px;"
        );

        btnMedio.setStyle(
                "-fx-background-color: red; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px;"
        );

        btnEndless.setStyle(
                "-fx-background-color: Blue; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px;"
        );
        return menu1;
    }
}
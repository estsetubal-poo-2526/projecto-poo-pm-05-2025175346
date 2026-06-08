import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GestorJogo extends Application {

    public MediaPlayer menuMusic;
    public MediaPlayer levelMusic;

    //Dimensões da Scene
    public static final int WIDTH = 350;
    public static final int HEIGHT = 800;

    //Número de vidas do jogador
    public static int vidas = 3;
    private final Label vidasLabel = new Label("Vidas: " + vidas);

    //Pontuação
    private int score = 0;
    private final Label scoreLabel = new Label("Pontuação: " + score);
    private int scoreLimit = -1;

    //Booleano que serve para indicar se o jogo deve reiniciar ou não
    private boolean reset = false;

    //Booleano que serve para indicar se a mensagem que anuncia uma nova ronda apareceu ou não
    private boolean novaRonda = false;

    private boolean tanqueExists = false;

    private boolean rapidoExists = false;

    private boolean facilDone = false;

    private boolean difDone = false;

    private String curLevel = "";

    //Booleano que serve para indicar se o jogo está a decorrer ou não
    private boolean gameRunning = false;

    //Listas de entidades moveis, uma delas já com objetos e outra que será adicionada para adicionar mais objetos
    private final List<EntidadeMovel> gameObjects = new ArrayList<>();
    private final List<EntidadeMovel> newObjects = new ArrayList<>();

    //Nave do jogador
    private NaveJogador nave = new NaveJogador(WIDTH / 2, HEIGHT - 40);

    //Node de raiz para o jogo
    private Pane pane = new Pane();

    //Scene do jogo
    private Scene scene = new Scene(pane, WIDTH, HEIGHT, Color.BLACK);

    //Stage da aplicação
    private Stage primaryStage;

    //Gerador de números aleatórios
    private final Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        /*Primeiro passo criar um palco, isto é uma janela de exibição da nossa interface gráfica!*/
        this.primaryStage = primaryStage;

        Image icon = new Image("/spaceship.jpg");
        primaryStage.setTitle("Space Invaders: Evolution");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);

        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));

        vidasLabel.setTranslateX(10);
        vidasLabel.setTranslateY(40);
        vidasLabel.setTextFill(Color.WHITE);
        vidasLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));

        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        pane.setStyle("-fx-background-color: black;"); //POR MUDAR
        pane.getChildren().addAll(canvas, scoreLabel, vidasLabel);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gameObjects.add(nave);

        menu1();

        String menuFile = "src/main/resources/Menu.mp3";
        Media menuMedia = new Media(new File(menuFile).toURI().toString());
        menuMusic = new MediaPlayer(menuMedia);
        menuMusic.setCycleCount(MediaPlayer.INDEFINITE);
        menuMusic.play();

        String levelFile = "src/main/resources/Level.mp3";
        Media levelMedia = new Media(new File(levelFile).toURI().toString());
        levelMusic = new MediaPlayer(levelMedia);
        levelMusic.setCycleCount(MediaPlayer.INDEFINITE);

        AudioClip w = new AudioClip(new File("src/main/resources/win.mp3").toURI().toString());

        primaryStage.getIcons().add(icon);
        primaryStage.fullScreenProperty();
        primaryStage.setResizable(false);

        jogadorComandos(scene);

        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastAlienSpawned = 0;
            private long lastPowerUpSpawned = 0;

            @Override
            public void handle(long now) {
                if (!gameRunning) return;

                if (scoreLimit != -1 && score >= scoreLimit) {
                    if (curLevel.equals("FACIL")) {
                        facilDone = true;
                    } else if (curLevel.equals("MEDIO")) {
                        difDone = true;
                    }

                    w.play();
                    voltarParaMenu();
                    return;
                }

                if (reset) {
                    this.start();
                    reset = false;
                }

                gc.setFill(Color.BLACK);
                gc.clearRect(0, 0, WIDTH, HEIGHT);

                if (now - lastAlienSpawned > 1_000_000_000) {
                    spawnAlien();
                    spawnAlienTanque();
                    spawnAlienRapido();
                    lastAlienSpawned = now;
                }

                if (now - lastPowerUpSpawned > 10_000_000_000L) {
                    spawnPowerUp();
                    lastPowerUpSpawned = now;
                }

                if (score >= 100 && score % 100 == 0) {
                    tanqueExists = false;
                    for (EntidadeMovel obj : gameObjects) {
                        if (obj instanceof AlienTanque) {
                            tanqueExists = true;
                            break;
                        }
                    }
                    if (!tanqueExists){
                        spawnAlienTanque();
                    }
                } else if (score >= 200 && score % 200 == 0) {
                    rapidoExists = false;
                    for (EntidadeMovel obj : gameObjects) {
                        if (obj instanceof AlienRapido) {
                            rapidoExists = true;
                            break;
                        }
                    }
                    if (!rapidoExists){
                        spawnAlienRapido();
                    }
                }

                colicoes();
                perderVidas();

                gameObjects.addAll(newObjects);
                newObjects.clear();

                for (EntidadeMovel obj : gameObjects) {
                    obj.update();
                    obj.render(gc);
                }

                Iterator<EntidadeMovel> iterator = gameObjects.iterator();
                while (iterator.hasNext()) {
                    EntidadeMovel obj = iterator.next();
                    if (obj.isDead()) {
                        iterator.remove();
                    }
                }
            }
        };

        gameLoop.start();
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }


    /*Segundo passo, criar uma cena para colocar no nosso palco, definir largura, altura, e um tipo de layout para utilizar*/

    public void menu1() {
        /* Definição de um titulo para o Menu */
        Label tituloMenu = new Label("Space Invaders: Evolution");
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
        Button btnFacil = new Button(facilDone ? "✓ Fácil" : "Fácil");
        Button btnDificil = new Button(difDone ? "✓ Difícil" : "Difícil");
        Button btnEndless = new Button("Endless");

        /* Definição do tamanho dos nossos botões */
        btnFacil.setPrefWidth(150);
        btnFacil.setPrefHeight(50);
        btnDificil.setPrefWidth(150);
        btnDificil.setPrefHeight(50);
        btnEndless.setPrefWidth(150);
        btnEndless.setPrefHeight(50);

        /* Animação dos botões */
        ScaleTransition aumentarF = new ScaleTransition(Duration.millis(150), btnFacil);
        aumentarF.setToX(1.15);
        aumentarF.setToY(1.15);

        ScaleTransition diminuirF = new ScaleTransition(Duration.millis(150), btnFacil);
        diminuirF.setToX(1);
        diminuirF.setToY(1);

        ScaleTransition aumentarM = new ScaleTransition(Duration.millis(150), btnDificil);
        aumentarM.setToX(1.15);
        aumentarM.setToY(1.15);

        ScaleTransition diminuirM = new ScaleTransition(Duration.millis(150), btnDificil);
        diminuirM.setToX(1);
        diminuirM.setToY(1);

        ScaleTransition aumentarE = new ScaleTransition(Duration.millis(150), btnEndless);
        aumentarE.setToX(1.15);
        aumentarE.setToY(1.15);

        ScaleTransition diminuirE = new ScaleTransition(Duration.millis(150), btnEndless);
        diminuirE.setToX(1.00);
        diminuirE.setToY(1.00);

        btnFacil.setOnMouseEntered(event -> {
            diminuirF.stop();
            aumentarF.play();
        });
        btnFacil.setOnMouseExited(event -> {
            aumentarF.stop();
            diminuirF.play();
        });

        btnDificil.setOnMouseEntered(event -> {
            diminuirM.stop();
            aumentarM.play();
        });
        btnDificil.setOnMouseExited(event -> {
            aumentarM.stop();
            diminuirM.play();
        });

        btnEndless.setOnMouseEntered(event -> {
            diminuirE.stop();
            aumentarE.play();
        });
        btnEndless.setOnMouseExited(event -> {
            aumentarE.stop();
            diminuirE.play();
        });

        /* Adiciona os botões à Vertical Box */
        caixasOpcoes.getChildren().addAll(btnFacil, btnDificil, btnEndless);

        /* Centrar os nossos botões */
        caixasOpcoes.setAlignment(Pos.CENTER);

        /* Estilos CSS das Caixas e Botões */
        btnFacil.setStyle("-fx-background-color: rgba(10, 30, 10, 0.6);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 15px;" +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px;"
        );


        btnDificil.setStyle("-fx-background-color: rgba(30, 20, 10, 0.6); " +   // Fundo laranja escuro semi-transparente
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
        InputStream streamImagem = getClass().getResourceAsStream("/download.jpg");

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
                            Side.LEFT, 0, false,
                            Side.TOP, deslocamentoY[0], false
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

        btnEndless.setOnAction(event -> {
            startGame();
        });
        btnFacil.setOnAction(event -> {
            scoreLimit = 5000;
            curLevel = "FACIL";
            showTempMessage("Chegue até aos\n5000 Pontos", 135, HEIGHT / 2, 2);
            startGame();
        });

        btnDificil.setOnAction(event -> {
            scoreLimit = 15000;
            curLevel = "MEDIO";
            startGame();
        });

        primaryStage.setScene(cenaMenu);
    }

    //Método que serve para tratar das colições entre as entidades móveis
    private void colicoes() {
        AudioClip liveLost = new AudioClip(new File("src/main/resources/aliendead.wav").toURI().toString());
        AudioClip powerupObtained = new AudioClip(new File("src/main/resources/powerupObtained.mp3").toURI().toString());
        List<Projetil> projetils = new ArrayList<>();
        List<AlienComum> a1 = new ArrayList<>();
        List<PowerUp> powerUps = new ArrayList<>();

        for (EntidadeMovel obj : gameObjects) {
            if (obj instanceof Projetil) {
                projetils.add((Projetil) obj);
            } else if (obj instanceof AlienComum) {
                a1.add((AlienComum) obj);
            } else if (obj instanceof PowerUp) {
                powerUps.add((PowerUp) obj);
            }
        }

        for (Projetil projetil : projetils) {
            for (AlienComum comum : a1) {
                if (projetil.getBounds().intersects(comum.getBounds())) {
                    projetil.setDead(true);
                    if (comum instanceof AlienTanque) {
                        ((AlienTanque) comum).takeDamage();
                    } else {
                        comum.setDead(true);
                        liveLost.play();
                        score += 50;
                    }
                    scoreLabel.setText("Pontuação: " + score);


                }

                for (PowerUp powerUp : powerUps) {
                    if (projetil.getBounds().intersects(powerUp.getBounds())) {
                        projetil.setDead(true);
                        powerUp.setDead(true);
                        powerupObtained.play();
                        score += 50;
                        scoreLabel.setText("Pontuação: " + score);
                    }
                }
            }
        }
        if (score % 100 == 0 && score > 0 && !novaRonda) {
            showTempMessage("Nova Ronda!!!", 135, HEIGHT / 2, 2);
            novaRonda = true;
        } else if (score % 100 != 0) {
            novaRonda = false;
        }
    }

    //Método que serve para decrementar o número de vidas quando um alien chega ao chão
    private void perderVidas(){
        AudioClip liveLost = new AudioClip(new File("src/main/resources/aliendead.wav").toURI().toString());
        List<AlienComum> a1 = new ArrayList<>();

        for (EntidadeMovel obj : gameObjects) {
            if (obj instanceof AlienComum) {
                a1.add((AlienComum) obj);
            }
        }

        for (AlienComum comum : a1) {
            if (comum.getY() + comum.getHeight() / 2 >= HEIGHT) {
                comum.setDead(true);
                comum.SPEED = comum.SPEED + 0.4;
                vidas--;
                liveLost.play();
                score -= 10;
                vidasLabel.setText("Vidas: " + vidas);
                if (vidas <= 0) resetGame();
            }
        }
    }

    //Método que mostra a tela de fim de jogo
    private void showLosingScreen(){
        AudioClip gameover = new AudioClip(new File("src/main/resources/gameover.wav").toURI().toString());
        gameover.play();
        Pane paneOver = new Pane();
        paneOver.setPrefWidth(WIDTH);
        paneOver.setPrefHeight(HEIGHT);
        paneOver.setStyle("-fx-background-color: black;");

        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Helvetica", FontWeight.BOLD, 40));
        gameOverText.setFill(Color.RED);
        gameOverText.setX((WIDTH - gameOverText.getLayoutBounds().getWidth()) / 2);
        gameOverText.setY(150);

        if (score < 0) score = 0;
        Text scoreText = new Text("Pontuação: " + score);
        scoreText.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        scoreText.setFill(Color.WHITE);
        scoreText.setX((WIDTH - scoreText.getLayoutBounds().getWidth()) / 2);
        scoreText.setY(250);

        Button btnAgain = new Button("Tente Outra Vez");
        btnAgain.setStyle("-fx-background-color: red; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 15px; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px;");

        Button btnLeave = new Button("Sair...");
        btnLeave.setStyle("-fx-background-color: Blue; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 15px; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px;");

        btnAgain.setPrefWidth(150);
        btnAgain.setPrefHeight(50);
        btnLeave.setPrefWidth(150);
        btnLeave.setPrefHeight(50);

        ScaleTransition aumentarA = new ScaleTransition(Duration.millis(150), btnAgain);
        aumentarA.setToX(1.15); //Aumenta o botão na  horizontal em 15%
        aumentarA.setToY(1.15); // Aumenta o botão na vertical em 15%

        ScaleTransition diminuirA = new ScaleTransition(Duration.millis(150), btnAgain);
        diminuirA.setToX(1); //Diminui o botão na vertical em 15%
        diminuirA.setToY(1); //Diminui o botão na vertical em 15%

        ScaleTransition aumentarL = new ScaleTransition(Duration.millis(150), btnLeave);
        aumentarL.setToX(1.15); //Aumenta o botão na  horizontal em 15%
        aumentarL.setToY(1.15); // Aumenta o botão na vertical em 15%

        ScaleTransition diminuirL = new ScaleTransition(Duration.millis(150), btnLeave);
        diminuirL.setToX(1); //Diminui o botão na vertical em 15%
        diminuirL.setToY(1); //Diminui o botão na vertical em 15%

        btnAgain.setOnMouseEntered(event ->{
            diminuirA.stop();
            aumentarA.play();
        });

        btnAgain.setOnMouseExited(event -> {
            aumentarA.stop();
            diminuirA.play();
        });

        btnLeave.setOnMouseEntered(event ->{
            diminuirL.stop();
            aumentarL.play();
        });

        btnLeave.setOnMouseExited(event -> {
            aumentarL.stop();
            diminuirL.play();
        });

        btnAgain.setOnAction(event -> restartGame());
        btnLeave.setOnAction(event -> voltarParaMenu());

        btnAgain.setLayoutX(115);
        btnAgain.setLayoutY(350);
        btnLeave.setLayoutX(115);
        btnLeave.setLayoutY(450);
        levelMusic.stop();

        paneOver.getChildren().addAll(gameOverText, scoreText, btnAgain, btnLeave);

        Scene youlost = new Scene(paneOver);
        primaryStage.setScene(youlost);
    }

    /**Método que serve para introduzir os comandos do jogador:
     * A e D para andar para a esquerda ou para a direita, respetivamente
     * SPACE para usar o Projétil
     *
     * @param scene
     */
    private void jogadorComandos (Scene scene){

        AudioClip pew = new AudioClip(new File("src/main/resources/projetil.mp3").toURI().toString());

        //Quando o jogador prime as teclas...
        scene.setOnKeyPressed(
                event -> {
                    switch (event.getCode()){
                        case A:
                            nave.setMoveLeft(true);
                            break;
                        case D:
                            nave.setMoveRight(true);
                            break;
                        case SPACE:
                            pew.play();
                            nave.shoot(newObjects);
                            break;
                    }
                }
        );

        //Quando o jogador solta as teclas...
        scene.setOnKeyReleased(
                event -> {
                    switch (event.getCode()) {
                        case A:
                            nave.setMoveLeft(false);
                            break;
                        case D:
                            nave.setMoveRight(false);
                            break;
                    }
                }
        );

    }

    //Método para adicionar aliens ao jogo
    private void spawnAlien(){
        int x = random.nextInt(WIDTH - 50) + 25;

        if (score % 100 == 0 && score > 0) {
            AlienTanque tanque = new AlienTanque(x, -40);
            gameObjects.add(tanque);
        } else if (score % 200 == 0 && score > 0){
            AlienRapido rapido = new AlienRapido(x, -40);
            gameObjects.add(rapido);
        } else {
            AlienComum comum = new AlienComum(x, -40);
            gameObjects.add(comum);
        }
    }

    private void spawnAlienRapido(){
        if(gameObjects.stream().noneMatch(n -> n instanceof AlienRapido)){
            AlienRapido rapido = new AlienRapido(WIDTH / 2, -40);
            gameObjects.add(rapido);
        }
    }

    private void spawnAlienTanque(){
        if(gameObjects.stream().noneMatch(n -> n instanceof AlienTanque)){
            AlienTanque tanque = new AlienTanque(WIDTH / 2, -40);
            gameObjects.add(tanque);
        }
    }

    //Método para adicionar um power up num canto aleatório do jogo
    private void spawnPowerUp() {
        int x = random.nextInt(WIDTH - PowerUp.WIDTH) + PowerUp.WIDTH / 2;
        PowerUp powerUp = new PowerUp(x, -PowerUp.HEIGHT / 2);
        gameObjects.add(powerUp);
    }

    /** Método que serve para mostrar no ecrã uma mensagem temporária
     * Isto vai ser útil para demonstrar a passagem das rondas
     *
     * @param message Mensagem que se quer mostrar
     * @param x Dimensão x da mensagem
     * @param y Dimensão y da mensagem
     * @param duration Duração em que a mensagem vai ser demonstrada
     */
    private void showTempMessage(String message, double x, double y, double duration) {
        Text tempMessage = new Text(message);
        tempMessage.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        tempMessage.setFill(Color.PURPLE);
        tempMessage.setX(x);
        tempMessage.setY(y);
        pane.getChildren().add(tempMessage);

        PauseTransition pause = new PauseTransition(Duration.seconds(duration));
        pause.setOnFinished(event -> pane.getChildren().remove(tempMessage));
        pause.play();
    }

    //Método para iniciar o jogo
    private void startGame(){
        gameRunning = true;
        menuMusic.stop();
        levelMusic.play();
        primaryStage.setScene(scene);
    }

    //Metodo para reiniciar o jogo quando o jogador perde as vidas todas
    private void resetGame() {
        gameRunning = false;
        showLosingScreen();
    }

    /** Restarts the game when the player chooses to try again. */
    private void restartGame() {
        gameObjects.clear();
        vidas = 3;
        score = 0;
        vidasLabel.setText("Lives: " + vidas);
        scoreLabel.setText("Score: " + score);
        gameObjects.add(nave);
        reset = true;
        gameRunning = true;
        levelMusic.play();
        primaryStage.setScene(scene);
    }
    private void voltarParaMenu() {
        gameRunning = false;
        levelMusic.stop();

        gameObjects.clear();
        score = 0;
        vidas = 3;
        scoreLabel.setText("Pontuação: " + score);
        vidasLabel.setText("Vidas: " + vidas);
        gameObjects.add(nave);

        menu1();
        menuMusic.play();
    }
}
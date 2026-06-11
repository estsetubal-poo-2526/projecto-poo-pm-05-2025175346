package org.example;

import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
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

public class App extends Application {

    private int rondaAtual = 1;

    // Elementos visuais para o Sistema de Corações
    private final HBox contentorCoracoes = new HBox(5);
    private Image imagemCoracao;

    // Variáveis para o Sistema de High Score permanente
    private int highScore = 0;
    private final Label highScoreLabel = new Label("Recorde: 0");
    private static final String FICHEIRO_HIGHSCORE = "highscore.txt";

    // Dimensões da Scene
    public static final int WIDTH = 350;
    public static final int HEIGHT = 800;

    // Número de vidas do jogador
    public static int vidas = 3;

    // Pontuação
    private int score = 0;
    private final Label scoreLabel = new Label("Pontuação: " + score);

    // Variável para guardar o modo de jogo selecionado: "FACIL", "MEDIO" ou "ENDLESS"
    private String modoDificuldade = "ENDLESS";

    // Sinalizadores de estado do jogo
    private boolean novaRonda = false;
    private boolean tanqueExists = false;
    private boolean rapidoExists = false;
    private boolean gameRunning = false;

    // Listas de entidades móveis
    private final List<EntidadeMovel> gameObjects = new ArrayList<>();
    private final List<EntidadeMovel> newObjects = new ArrayList<>();

    // Nave do jogador
    private NaveJogador nave = new NaveJogador(WIDTH / 2.0, HEIGHT - 40);

    // Nós de interface base
    private Pane pane = new Pane();
    private Scene scene = new Scene(pane, WIDTH, HEIGHT, Color.BLACK);
    private Stage primaryStage;

    private final Random random = new Random();

    // Controlo de tempo de Spawns
    private long lastAlienSpawned = 0;
    private long lastPowerUpSpawned = 0;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Carrega o High Score guardado localmente
        carregarHighScore();

        // Tenta carregar o sprite do coração
        try {
            InputStream streamCoracao = getClass().getResourceAsStream("/coracao.png");
            if (streamCoracao == null) {
                streamCoracao = getClass().getResourceAsStream("coracao.png");
            }
            if (streamCoracao != null) {
                imagemCoracao = new Image(streamCoracao);
            }
        } catch (Exception e) {
            System.out.println("Não foi possível carregar a imagem do coração.");
        }

        try {
            InputStream streamIcon = getClass().getResourceAsStream("/spaceship.jpg");
            if (streamIcon != null) {
                Image icon = new Image(streamIcon);
                primaryStage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.out.println("Não foi possível carregar o ícone.");
        }

        primaryStage.setTitle("Space Invaders: Evolution");
        primaryStage.setScene(scene);

        // Estilo e posicionamento das Labels de Interface
        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));

        highScoreLabel.setTranslateX(10);
        highScoreLabel.setTranslateY(35);
        highScoreLabel.setTextFill(Color.GOLD);
        highScoreLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));

        contentorCoracoes.setTranslateX(10);
        contentorCoracoes.setTranslateY(65);
        atualizarCoracoesVisuais();

        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        pane.setStyle("-fx-background-color: black;");
        pane.getChildren().addAll(canvas, scoreLabel, highScoreLabel, contentorCoracoes);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gameObjects.add(nave);

        jogadorComandos(scene);
        menu1();
        primaryStage.setResizable(false);

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameRunning) return;

                if (lastAlienSpawned == 0) lastAlienSpawned = now;
                if (lastPowerUpSpawned == 0) lastPowerUpSpawned = now;

                gc.setFill(Color.BLACK);
                gc.clearRect(0, 0, WIDTH, HEIGHT);

                long intervaloSpawn = 1_500_000_000L;
                if (modoDificuldade.equals("FACIL")) {
                    intervaloSpawn = 2_200_000_000L;
                } else if (modoDificuldade.equals("ENDLESS")) {
                    intervaloSpawn = 1_000_000_000L;
                }

                if (now - lastAlienSpawned > intervaloSpawn) {
                    spawnAlien();
                    lastAlienSpawned = now;
                }

                if (now - lastPowerUpSpawned > 12_000_000_000L) {
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

    public void menu1() {
        // 1. TÍTULO EVOLUÍDO: Design em caixa, com fontes estilizadas e brilho Neon ciano
        VBox contentorTitulo = new VBox(-5); // Junta ligeiramente as duas linhas para um design mais compacto
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

        // Painel base onde vamos montar o ecrã
        Pane painelMenuCompleto = new Pane();
        painelMenuCompleto.setStyle("-fx-background-color: #05050a;"); // Fundo fallback escuro

        // 2. ATUALIZADO: Nova Imagem FundoMenu1 com Animação Fluida Total
        // NOTA: Se o teu ficheiro for .jpg em vez de .png, altera a extensão abaixo para "/FundoMenu1.jpg"
        InputStream streamImagem = getClass().getResourceAsStream("/FundoMenu1.jpg");
        if (streamImagem == null) {
            streamImagem = getClass().getResourceAsStream("FundoMenu1.jpg");
        }

        if (streamImagem != null) {
            Image fundoImg = new Image(streamImagem);

            // Criamos dois nós de imagem idênticos que cobrem perfeitamente o ecrã
            javafx.scene.image.ImageView fundo1 = new javafx.scene.image.ImageView(fundoImg);
            fundo1.setFitWidth(WIDTH);
            fundo1.setFitHeight(HEIGHT);
            fundo1.setSmooth(true);

            javafx.scene.image.ImageView fundo2 = new javafx.scene.image.ImageView(fundoImg);
            fundo2.setFitWidth(WIDTH);
            fundo2.setFitHeight(HEIGHT);
            fundo2.setY(-HEIGHT); // Fica posicionada logo acima do ecrã, colada à primeira
            fundo2.setSmooth(true);

            painelMenuCompleto.getChildren().addAll(fundo1, fundo2);

            // Motor de movimento contínuo (Ajustado para evitar qualquer micro-espaço vazio entre frames)
            AnimationTimer motorFundoFluido = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    double velocidade = 1.0; // Velocidade ideal para scroll espacial de fundo

                    fundo1.setY(fundo1.getY() + velocidade);
                    fundo2.setY(fundo2.getY() + velocidade);

                    // Ciclo infinito: Quando a imagem passa do limite inferior, salta precisamente para o topo da outra
                    if (fundo1.getY() >= HEIGHT) {
                        fundo1.setY(fundo2.getY() - HEIGHT);
                    }
                    if (fundo2.getY() >= HEIGHT) {
                        fundo2.setY(fundo1.getY() - HEIGHT);
                    }
                }
            };
            motorFundoFluido.start();
        } else {
            System.out.println("Aviso: Não foi possível encontrar o ficheiro FundoMenu1.png na pasta resources.");
        }

        // 3. PAINEL CENTRAL (Recorde + Botões de Opções)
        VBox painelCentral = new VBox(20);
        painelCentral.setAlignment(Pos.CENTER);
        painelCentral.setMaxWidth(240);
        painelCentral.setStyle("-fx-background-color: rgba(15, 15, 30, 0.82); -fx-background-radius: 15px; -fx-border-color: rgba(0, 255, 204, 0.2); -fx-border-width: 1.5px; -fx-border-radius: 15px; -fx-padding: 25px;");

        Label recordeMenuLabel = new Label("RECORDE ENDLESS\n" + highScore);
        recordeMenuLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-alignment: center; -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.5), 10, 0, 0, 0);");

        Region separador = new Region();
        separador.setPrefHeight(2);

        VBox caixasOpcoes = new VBox(15);
        caixasOpcoes.setAlignment(Pos.CENTER);

        Button btnFacil = new Button("Fácil");
        Button btnMedio = new Button("Médio");
        Button btnEndless = new Button("EndLess");

        btnFacil.setPrefWidth(150); btnFacil.setPrefHeight(45);
        btnMedio.setPrefWidth(150); btnMedio.setPrefHeight(45);
        btnEndless.setPrefWidth(150); btnEndless.setPrefHeight(45);

        // Animações de Hover dos botões (Efeito Scale Zoom)
        ScaleTransition aumentarF = new ScaleTransition(Duration.millis(120), btnFacil);
        aumentarF.setToX(1.1); aumentarF.setToY(1.1);
        ScaleTransition diminuirF = new ScaleTransition(Duration.millis(120), btnFacil);
        diminuirF.setToX(1); diminuirF.setToY(1);

        ScaleTransition aumentarM = new ScaleTransition(Duration.millis(120), btnMedio);
        aumentarM.setToX(1.1); aumentarM.setToY(1.1);
        ScaleTransition diminuirM = new ScaleTransition(Duration.millis(120), btnMedio);
        diminuirM.setToX(1); diminuirM.setToY(1);

        ScaleTransition aumentarE = new ScaleTransition(Duration.millis(120), btnEndless);
        aumentarE.setToX(1.1); aumentarE.setToY(1.1);
        ScaleTransition diminuirE = new ScaleTransition(Duration.millis(120), btnEndless);
        diminuirE.setToX(1); diminuirE.setToY(1);

        btnFacil.setOnMouseEntered(event -> { diminuirF.stop(); aumentarF.play(); });
        btnFacil.setOnMouseExited(event -> { aumentarF.stop(); diminuirF.play(); });
        btnMedio.setOnMouseEntered(event -> { diminuirM.stop(); aumentarM.play(); });
        btnMedio.setOnMouseExited(event -> { aumentarM.stop(); diminuirM.play(); });
        btnEndless.setOnMouseEntered(event -> { diminuirE.stop(); aumentarE.play(); });
        btnEndless.setOnMouseExited(event -> { aumentarE.stop(); diminuirE.play(); });

        btnFacil.setStyle("-fx-background-color: rgba(10, 50, 10, 0.75); -fx-text-fill: #00ff00; -fx-border-color: #00ff00; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(three-pass-box, #00ff00, 8, 0, 0, 0);");
        btnMedio.setStyle("-fx-background-color: rgba(50, 30, 10, 0.75); -fx-text-fill: #ff9900; -fx-border-color: #ff9900; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(three-pass-box, #ff9900, 8, 0, 0, 0);");
        btnEndless.setStyle("-fx-background-color: rgba(10, 20, 50, 0.75); -fx-text-fill: #00bfff; -fx-border-color: #00bfff; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(three-pass-box, #00bfff, 8, 0, 0, 0);");

        caixasOpcoes.getChildren().addAll(btnFacil, btnMedio, btnEndless);
        painelCentral.getChildren().addAll(recordeMenuLabel, separador, caixasOpcoes);

        // 4. ORGANIZAÇÃO E LAYOUT FINAL NO ECRÃ
        VBox layoutVerticalMenu = new VBox(45);
        layoutVerticalMenu.setAlignment(Pos.TOP_CENTER);
        layoutVerticalMenu.setPadding(new Insets(60, 0, 0, 0));
        layoutVerticalMenu.setPrefWidth(WIDTH);
        layoutVerticalMenu.setPrefHeight(HEIGHT);
        layoutVerticalMenu.getChildren().addAll(contentorTitulo, painelCentral);

        painelMenuCompleto.getChildren().add(layoutVerticalMenu);

        Scene cenaMenu = new Scene(painelMenuCompleto, WIDTH, HEIGHT);
        jogadorComandos(cenaMenu);

        btnFacil.setOnAction(event -> {
            modoDificuldade = "FACIL";
            AlienComum.SPEED = 0.6;
            AlienRapido.SPEED = 1.3;
            AlienTanque.SPEED = 0.3;
            startGame();
        });

        btnMedio.setOnAction(event -> {
            modoDificuldade = "MEDIO";
            AlienComum.SPEED = 1.2;
            AlienRapido.SPEED = 2.2;
            AlienTanque.SPEED = 0.6;
            startGame();
        });

        btnEndless.setOnAction(event -> {
            modoDificuldade = "ENDLESS";
            AlienComum.SPEED = 1.6;
            AlienRapido.SPEED = 2.8;
            AlienTanque.SPEED = 0.9;
            startGame();
        });

        primaryStage.setScene(cenaMenu);
    }

    private void carregarHighScore() {
        java.io.File f = new java.io.File(FICHEIRO_HIGHSCORE);
        if (f.exists()) {
            try (java.util.Scanner scanner = new java.util.Scanner(f)) {
                if (scanner.hasNextInt()) {
                    highScore = scanner.nextInt();
                    highScoreLabel.setText("Recorde: " + highScore);
                }
            } catch (java.io.IOException e) {
                System.out.println("Erro ao carregar o High Score: " + e.getMessage());
            }
        }
    }

    private void atualizarCoracoesVisuais() {
        contentorCoracoes.getChildren().clear();
        if (imagemCoracao != null) {
            for (int i = 0; i < vidas; i++) {
                javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(imagemCoracao);
                iv.setFitWidth(25);
                iv.setFitHeight(25);
                contentorCoracoes.getChildren().add(iv);
            }
        }
    }

    private void guardarHighScore() {
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("Recorde: " + highScore);
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(FICHEIRO_HIGHSCORE))) {
                writer.print(highScore);
            } catch (java.io.IOException e) {
                System.out.println("Erro ao guardar o High Score: " + e.getMessage());
            }
        }
    }

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
            if (projetil.isDead()) continue;

            for (AlienComum comum : a1) {
                if (comum.isDead()) continue;

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
                    break;
                }
            }

            if (!projetil.isDead()) {
                for (PowerUp powerUp : powerUps) {
                    if (powerUp.isDead()) continue;

                    if (projetil.getBounds().intersects(powerUp.getBounds())) {
                        projetil.setDead(true);
                        powerUp.setDead(true);
                        powerupObtained.play();
                        score += 50;
                        scoreLabel.setText("Pontuação: " + score);
                        break;
                    }
                }
            }
        }

        if (score % 500 == 0 && score > 0 && !novaRonda) {
            rondaAtual++;
            showTempMessage("RONDA " + rondaAtual, WIDTH / 2.0, HEIGHT / 2.0, 2.5);
            novaRonda = true;

            if (!modoDificuldade.equals("ENDLESS")) {
                AlienComum.SPEED += 0.2;
            }
        } else if (score % 500 != 0) {
            novaRonda = false;
        }
    }

    private void perderVidas(){
        AudioClip liveLost = new AudioClip(new File("src/main/resources/aliendead.wav").toURI().toString());
        List<AlienComum> a1 = new ArrayList<>();

        for (EntidadeMovel obj : gameObjects) {
            if (obj instanceof AlienComum) {
                a1.add((AlienComum) obj);
            }
        }

        for (AlienComum comum : a1) {
            if (comum.getY() + comum.getHeight() / 2.0 >= HEIGHT) {
                comum.setDead(true);
                vidas--;
                liveLost.play();
                score -= 10;
                if (score < 0) score = 0;

                atualizarCoracoesVisuais();
                scoreLabel.setText("Pontuação: " + score);

                if (vidas <= 0) {
                    guardarHighScore();
                    resetGame();
                }
            }
        }
    }

    private void showLosingScreen(){
        AudioClip gameover = new AudioClip(new File("src/main/resources/gameover.wav").toURI().toString());
        gameover.play();

        VBox layoutGameOver = new VBox(25);
        layoutGameOver.setPrefWidth(WIDTH);
        layoutGameOver.setPrefHeight(HEIGHT);
        layoutGameOver.setAlignment(Pos.CENTER);
        layoutGameOver.setPadding(new Insets(50, 20, 50, 20));
        layoutGameOver.setStyle("-fx-background-color: #05050a;");

        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Impact", FontWeight.BOLD, 46));
        gameOverText.setFill(Color.RED);
        gameOverText.setStroke(Color.BLACK);
        gameOverText.setStrokeWidth(2);
        gameOverText.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255, 0, 0, 0.8), 15, 0, 0, 0);");

        VBox blocoScores = new VBox(10);
        blocoScores.setAlignment(Pos.CENTER);
        blocoScores.setStyle("-fx-background-color: rgba(20, 20, 35, 0.7); -fx-background-radius: 10px; -fx-padding: 15px; -fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1px;");
        blocoScores.setMaxWidth(220);

        if (score < 0) score = 0;
        Text scoreText = new Text("A Tua Pontuação: " + score);
        scoreText.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        scoreText.setFill(Color.WHITE);

        Text recordeText = new Text("Recorde Atual: " + highScore);
        recordeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
        recordeText.setFill(Color.GOLD);

        blocoScores.getChildren().addAll(scoreText, recordeText);

        Button btnAgain = new Button("Tentar Novamente");
        btnAgain.setPrefWidth(180);
        btnAgain.setPrefHeight(45);
        btnAgain.setStyle("-fx-background-color: rgba(50, 10, 10, 0.7); -fx-text-fill: #ff3333; -fx-border-color: #ff3333; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(three-pass-box, rgba(255,51,51,0.4), 10, 0, 0, 0);");

        Button btnLeave = new Button("Sair do Jogo");
        btnLeave.setPrefWidth(180);
        btnLeave.setPrefHeight(45);
        btnLeave.setStyle("-fx-background-color: rgba(10, 10, 35, 0.7); -fx-text-fill: #00bfff; -fx-border-color: #00bfff; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(three-pass-box, rgba(0,191,255,0.4), 10, 0, 0, 0);");

        ScaleTransition aumentarA = new ScaleTransition(Duration.millis(150), btnAgain);
        aumentarA.setToX(1.1); aumentarA.setToY(1.1);
        ScaleTransition diminuirA = new ScaleTransition(Duration.millis(150), btnAgain);
        diminuirA.setToX(1); diminuirA.setToY(1);

        ScaleTransition aumentarL = new ScaleTransition(Duration.millis(150), btnLeave);
        aumentarL.setToX(1.1); aumentarL.setToY(1.1);
        ScaleTransition diminuirL = new ScaleTransition(Duration.millis(150), btnLeave);
        diminuirL.setToX(1); diminuirL.setToY(1);

        btnAgain.setOnMouseEntered(event -> { diminuirA.stop(); aumentarA.play(); });
        btnAgain.setOnMouseExited(event -> { aumentarA.stop(); diminuirA.play(); });
        btnLeave.setOnMouseEntered(event -> { diminuirL.stop(); aumentarL.play(); });
        btnLeave.setOnMouseExited(event -> { aumentarL.stop(); diminuirL.play(); });

        btnAgain.setOnAction(event -> restartGame());
        btnLeave.setOnAction(event -> System.exit(0));

        layoutGameOver.getChildren().addAll(gameOverText, blocoScores, btnAgain, btnLeave);

        Scene youlost = new Scene(layoutGameOver, WIDTH, HEIGHT);
        jogadorComandos(youlost);
        primaryStage.setScene(youlost);
    }

    private void jogadorComandos(Scene activeScene) {
        AudioClip pew = new AudioClip(new File("src/main/resources/projetil.mp3").toURI().toString());

        activeScene.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case A: case LEFT:
                    nave.setMoveLeft(true);
                    break;
                case D: case RIGHT:
                    nave.setMoveRight(true);
                    break;
                case SPACE:
                    if (gameRunning) {
                        pew.play();
                        nave.shoot(newObjects);
                    }
                    break;
            }
        });

        activeScene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case A: case LEFT:
                    nave.setMoveLeft(false);
                    break;
                case D: case RIGHT:
                    nave.setMoveRight(false);
                    break;
            }
        });
    }

    private void spawnAlien(){
        int xPos = random.nextInt(WIDTH - 50) + 25;

        if (modoDificuldade.equals("FACIL")) {
            AlienComum comum = new AlienComum(xPos, -40);
            gameObjects.add(comum);
            return;
        }

        if (score % 100 == 0 && score > 0) {
            AlienTanque tanque = new AlienTanque(xPos, -40);
            gameObjects.add(tanque);
        } else if (score % 200 == 0 && score > 0){
            AlienRapido rapido = new AlienRapido(xPos, -40);
            gameObjects.add(rapido);
        } else {
            AlienComum comum = new AlienComum(xPos, -40);
            gameObjects.add(comum);
        }
    }

    private void spawnAlienRapido(){
        if(gameObjects.stream().noneMatch(n -> n instanceof AlienRapido)){
            AlienRapido rapido = new AlienRapido(WIDTH / 2.0, -40);
            gameObjects.add(rapido);
        }
    }

    private void spawnAlienTanque(){
        if(gameObjects.stream().noneMatch(n -> n instanceof AlienTanque)){
            AlienTanque tanque = new AlienTanque(WIDTH / 2.0, -40);
            gameObjects.add(tanque);
        }
    }

    private void spawnPowerUp() {
        int xPos = random.nextInt(WIDTH - PowerUp.WIDTH) + PowerUp.WIDTH / 2;
        PowerUp powerUp = new PowerUp(xPos, -PowerUp.HEIGHT / 2.0);
        gameObjects.add(powerUp);
    }

    private void showTempMessage(String message, double centerX, double centerY, double durationSeconds) {
        Text tempMessage = new Text(message);
        tempMessage.setFont(Font.font("Impact", FontWeight.BOLD, 36));
        tempMessage.setFill(Color.web("#00ffcc"));
        tempMessage.setStroke(Color.BLACK);
        tempMessage.setStrokeWidth(2);

        double textWidth = tempMessage.getLayoutBounds().getWidth();
        tempMessage.setX(centerX - textWidth / 2.0);
        tempMessage.setY(centerY);

        pane.getChildren().add(tempMessage);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), tempMessage);
        scale.setFromX(0.3); scale.setFromY(0.3);
        scale.setToX(1.2); scale.setToY(1.2);
        scale.setOnFinished(e -> {
            ScaleTransition stabilize = new ScaleTransition(Duration.millis(100), tempMessage);
            stabilize.setToX(1.0); stabilize.setToY(1.0);
            stabilize.play();
        });

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(Duration.seconds(durationSeconds), tempMessage);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(0.5));

        fade.setOnFinished(event -> pane.getChildren().remove(tempMessage));

        scale.play();
        fade.play();
    }

    private void startGame(){
        gameRunning = true;
        primaryStage.setScene(scene);
    }

    private void resetGame() {
        gameRunning = false;
        showLosingScreen();
    }

    private void restartGame() {
        gameObjects.clear();
        newObjects.clear();

        vidas = 3;
        score = 0;
        rondaAtual = 1;

        scoreLabel.setText("Pontuação: " + score);
        atualizarCoracoesVisuais();

        nave = new NaveJogador(WIDTH / 2.0, HEIGHT - 40);
        gameObjects.add(nave);

        lastAlienSpawned = 0;
        lastPowerUpSpawned = 0;

        menu1();
    }
}

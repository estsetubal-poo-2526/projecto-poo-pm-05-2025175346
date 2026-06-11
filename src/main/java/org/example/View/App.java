package org.example.View;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import org.example.Models.EntidadeMovel;
import org.example.Models.AlienComum;
import org.example.Models.AlienRapido;
import org.example.Models.AlienTanque;
import org.example.Models.NaveJogador;
import org.example.Models.PowerUp;
import org.example.Models.Projetil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class App extends Application {

    private int rondaAtual = 1;
    private final HBox contentorCoracoes = new HBox(5);
    private Image imagemCoracao;

    private int highScore = 0;
    private final Label highScoreLabel = new Label("Recorde: 0");
    private static final String FICHEIRO_HIGHSCORE = "highscore.txt";

    public static final int WIDTH = 350;
    public static final int HEIGHT = 800;

    public static int vidas = 3;
    private int score = 0;
    private final Label scoreLabel = new Label("Pontuação: " + score);

    private String modoDificuldade = "ENDLESS";

    private boolean novaRonda = false;
    private boolean tanqueExists = false;
    private boolean rapidoExists = false;
    private boolean gameRunning = false;

    private final List<EntidadeMovel> gameObjects = new ArrayList<>();
    private final List<EntidadeMovel> newObjects = new ArrayList<>();

    private NaveJogador nave = new NaveJogador(WIDTH / 2.0, HEIGHT - 40);

    private Pane pane = new Pane();
    private Scene scene = new Scene(pane, WIDTH, HEIGHT, Color.BLACK);
    private Stage primaryStage;

    private final Random random = new Random();

    private long lastAlienSpawned = 0;
    private long lastPowerUpSpawned = 0;

    // CORREÇÃO: O GraphicsContext tem de ser um atributo da classe
    private GraphicsContext gc;

    @Override
    public void start(Stage primaryStage) throws org.example.exceptions.RecursoInvalidoException {
        this.primaryStage = primaryStage;

        carregarHighScore();
        inicializarComponentesMedia();
        configurarInterfaceGrafica();
        inicializarGameLoop();

        menu1();

        primaryStage.setTitle("Space Invaders: Evolution");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void inicializarComponentesMedia() throws org.example.exceptions.RecursoInvalidoException {
        try {
            imagemCoracao = carregarImagemSegura("/Images/coracao.png");
            Image icon = carregarImagemSegura("/Images/spaceship.jpg");
            primaryStage.getIcons().add(icon);
        } catch (org.example.exceptions.RecursoInvalidoException e) {
            System.err.println("Aviso Crítico de Inicialização: " + e.getMessage());
            // Se quiseres que o jogo feche caso não encontre as imagens, podes adicionar:
            // System.exit(1);
        }
    }

    private Image carregarImagemSegura(String caminho) throws org.example.exceptions.RecursoInvalidoException {
        InputStream stream = getClass().getResourceAsStream(caminho);
        if (stream == null) {
            stream = getClass().getResourceAsStream(caminho.substring(1));
        }
        if (stream == null) {
            throw new org.example.exceptions.RecursoInvalidoException("O ficheiro gráfico mapeado em '" + caminho + "' não pôde ser localizado nos recursos globais.");
        }
        return new Image(stream);
    }

    private void configurarInterfaceGrafica() {
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
        // CORREÇÃO: Atribuição correta ao atributo global
        this.gc = canvas.getGraphicsContext2D();

        pane.setStyle("-fx-background-color: black;");
        pane.getChildren().addAll(canvas, scoreLabel, highScoreLabel, contentorCoracoes);

        gameObjects.add(nave);
        jogadorComandos(scene);
    }

    private void inicializarGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameRunning) return;

                atualizarTemporizadoresFundo(now);
                renderizarLimpezaEcra();
                controlarFrequenciaSpawns(now);
                validarInimigosRobustos();
                processarDeteçãoColisoes();
                atualizarERenderizarEntidades();
                removerEntidadesInativas();
            }
        };
        gameLoop.start();
    }

    private void atualizarTemporizadoresFundo(long now) {
        if (lastAlienSpawned == 0) lastAlienSpawned = now;
        if (lastPowerUpSpawned == 0) lastPowerUpSpawned = now;
    }

    private void renderizarLimpezaEcra() {
        gc.setFill(Color.BLACK);
        gc.clearRect(0, 0, WIDTH, HEIGHT);
    }

    private void controlarFrequenciaSpawns(long now) {
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
    }

    private void validarInimigosRobustos() {
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
    }

    private void processarDeteçãoColisoes() {
        colicoes();
        perderVidas();
        gameObjects.addAll(newObjects);
        newObjects.clear();
    }

    private void atualizarERenderizarEntidades() {
        for (EntidadeMovel obj : gameObjects) {
            obj.update();
            obj.render(gc);
        }
    }

    private void removerEntidadesInativas() {
        Iterator<EntidadeMovel> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            EntidadeMovel obj = iterator.next();
            if (obj.isDead()) {
                iterator.remove();
            }
        }
    }

    public void setModoDificuldade(String modo) {
        this.modoDificuldade = modo;
    }

    public void startGame() {
        this.gameRunning = true;
        // CORREÇÃO: Forçar os controlos a estarem ativos na scene do jogo ao iniciar
        jogadorComandos(this.scene);
        this.primaryStage.setScene(this.scene);
    }

    public void menu1() {
        MenuPrincipalView menuView = new MenuPrincipalView(this, highScore);
        Scene cenaMenu = new Scene(menuView, WIDTH, HEIGHT);
        jogadorComandos(cenaMenu);
        primaryStage.setScene(cenaMenu);
    }

    private void showLosingScreen() {
        GameOverView gameOverView = new GameOverView(this, score, highScore);
        Scene youlost = new Scene(gameOverView, WIDTH, HEIGHT);
        jogadorComandos(youlost);
        primaryStage.setScene(youlost);
    }

    public void restartGame() {
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
        AudioClip liveLost = new AudioClip(new File("src/main/resources/Sounds/aliendead.wav").toURI().toString());
        AudioClip powerupObtained = new AudioClip(new File("src/main/resources/Sounds/powerupObtained.mp3").toURI().toString());

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
        AudioClip liveLost = new AudioClip(new File("src/main/resources/Sounds/aliendead.wav").toURI().toString());
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

    private void jogadorComandos(Scene activeScene) {
        AudioClip pew = new AudioClip(new File("src/main/resources/Sounds/projetil.mp3").toURI().toString());

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

        javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(Duration.millis(300), tempMessage);
        scale.setFromX(0.3); scale.setFromY(0.3);
        scale.setToX(1.2); scale.setToY(1.2);
        scale.setOnFinished(e -> {
            javafx.animation.ScaleTransition stabilize = new javafx.animation.ScaleTransition(Duration.millis(100), tempMessage);
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

    private void resetGame() {
        gameRunning = false;
        showLosingScreen();
    }
}
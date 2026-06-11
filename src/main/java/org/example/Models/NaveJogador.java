package org.example.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.View.App;

import java.util.List;
import java.util.Objects;

public class NaveJogador extends EntidadeMovel{
    //Representa a nave do jogador, que o jogador vai poder controlar no jogo

    //Dimensões do projetil
    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;

    //Velocidade do projetil
    private static final double SPEED = 5;

    //Controlos da nave (só se move para a esquerda ou para a direita)
    private boolean moveLeft;
    private boolean moveRight;

    //"Vidas" do jogador (por mudar)
    private int health = 3;

    //Imagem da nave
    private final Image spaceship;

    //Serve para indicar se o jogador está "morto" ou não
    private boolean dead = false;

    //Construtor da NaveJogador
    public NaveJogador(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.spaceship = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/spaceship.jpg")));
    }

    //Métodos abstratos da EntidadeMóvel

    /** Getter para o width
     *
     * @return width
     */
    @Override
    public double getWidth() {
        return WIDTH;
    }

    /** Getter para o height
     *
     * @return height
     */
    @Override
    public double getHeight() {
        return HEIGHT;
    }

    /** Getter para verificar o estado do projétil
     *
     * @return true se o projétil desapareceu, se não devolve false
     */
    @Override
    public boolean isDead() {
        return dead;
    }

    //Atualiza a posição da nave
    @Override
    public void update() {
        if (moveLeft && x - WIDTH / 2 - SPEED >= 0) {
            x -= SPEED;
        }

        // CORRIGIDO: Mudado de appStart.WIDTH para GestorJogo.WIDTH
        if (moveRight && x + WIDTH / 2 + SPEED <= App.WIDTH) {
            x += SPEED;
        }
    }

    /** Apresenta a nave no programa
     *
     * @param gc serve para "renderizar" a nave
     */
    @Override
    public void render(GraphicsContext gc) {
        if (spaceship != null) { //Verifica se a imagem foi renderizada ou não
            double imageWidth = spaceship.getWidth();
            double imageHeight = spaceship.getHeight();

            double aspectRatio = imageWidth / imageHeight;

            double scaledWidth = WIDTH * 2;
            double scaledHeight = HEIGHT * 2;

            if (scaledHeight > scaledWidth / aspectRatio) {
                scaledHeight = scaledWidth / aspectRatio;
            } else {
                scaledWidth = scaledHeight * aspectRatio;
            }

            double drawX = x - scaledWidth / 2;
            double drawY = y - scaledHeight / 2;

            gc.drawImage(spaceship, drawX, drawY, scaledWidth, scaledHeight);
        } else { //Caso a imagem não tenha sido renderizada...
            gc.setFill(Color.BLUE);
            gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        }
    }

    /** Getter para as "vidas"
     *
     * @return health
     */
    public int getHealth() {
        return health;
    }

    /** Setter para as "vidas"
     *
     * @param health
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**Setter para nave se mover para a esquerda
     *
     * @param moveLeft
     */
    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    /**Setter para nave se mover para a direita
     *
     * @param moveRight
     */
    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    /** Método que guarda o projétil lançado pela nave
     *
     * @param objects lista de entidades meveis que vai guardar o projétil
     */
    public void shoot(List<EntidadeMovel> objects) {
        Projetil bullet = new Projetil(x, y - HEIGHT / 2 - Projetil.HEIGHT);
        objects.add(bullet);
    }

    /** Setter para verificar o estado da nave
     *
     * @param dead
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }
}

package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class AlienTanque extends AlienComum{
    /*Representa um inimigo do tipo tanque (2 HP) */

    //Número de vidas do alien
    private int health = 2;

    //Dimensões do alien !!!É LIGEIRAMENTE MAIOR!!!
    protected static final int WIDTH = 40;
    protected static final int HEIGHT = 40;

    //Velocidade do alien
    public static double SPEED = 0.5;

    //Serve para indicar se o alien está "morto" ou não
    private boolean dead = false;

    //Imagem do powerup
    private final Image alienTanque;

    // Construtor do powerup
    public AlienTanque(double x, double y) {
        super(x, y);
        this.alienTanque =
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/alientanque.png")));
    }

    //Métodos abstratos da EntidadeMóvel

    //Atualiza a posição do powerup
    @Override
    public void update() {
        y += SPEED;
    }

    /** Apresenta o powerup no programa
     *
     * @param gc serve para "renderizar" o powerup
     */
    @Override
    public void render(GraphicsContext gc) {
        if (alienTanque != null) { //Verifica se a imagem foi renderizada ou não
            gc.drawImage(alienTanque, x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        } else {
            //Caso a imagem não tenha sido renderizada...
            gc.setFill(Color.DARKRED);
            gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        }
    }
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

    /** Setter para verificar o estado do projétil
     *
     * @param dead
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void takeDamage() {
        health--;
        if (health <= 0) {
            setDead(true);
        }
    }
}

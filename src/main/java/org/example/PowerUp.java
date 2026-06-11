package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class PowerUp extends EntidadeMovel {
    /*Representa um objeto que pode beneficiar o jogador*/

    //Dimensões do powerup
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    //Velocidade do powerup
    private static final double SPEED = 2;

    //Serve para indicar se o powerup está "morto" ou não
    private boolean dead = false;

    //Imagem do powerup
    private final Image powerUp;

    // Construtor do powerup
    public PowerUp(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.powerUp =
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/powerup.png")));
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
        if (powerUp != null) { //Verifica se a imagem foi renderizada ou não
            double scaledWidth = WIDTH * 2;
            double scaledHeight = HEIGHT * 2;

            double drawX = x - scaledWidth / 2;
            double drawY = y - scaledHeight / 2;

            gc.drawImage(powerUp, drawX, drawY, scaledWidth, scaledHeight);
        } else {
            //Caso a imagem não tenha sido renderizada...
            gc.setFill(Color.GREEN);
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
}

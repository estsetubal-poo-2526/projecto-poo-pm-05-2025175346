package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class AlienRapido extends AlienComum{
    /*Representa um inimigo do tipo rápido (velocidade incrementada)*/

    //Dimensões do alien
    protected static final int WIDTH = 35;
    protected static final int HEIGHT = 35;

    //Velocidade do alien !!!A VELOCIDADE FOI INCREMENTADA!!!
    public static double SPEED = 2;

    //Serve para indicar se o alien está "morto" ou não
    private boolean dead = false;

    //Imagem do powerup
    private final Image alienRapido;

    // Construtor do powerup
    public AlienRapido(double x, double y) {
        super(x, y);
        this.alienRapido =
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/alienrapido.png")));
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
        if (alienRapido != null) { //Verifica se a imagem foi renderizada ou não
            gc.drawImage(alienRapido, x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        } else {
            //Caso a imagem não tenha sido renderizada...
            gc.setFill(Color.PALEVIOLETRED);
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

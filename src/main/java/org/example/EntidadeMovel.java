package org.example;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

// Representa um objeto dentro do jogo (A nave do jogador, os aliens, o projétil e os "power-ups"
public abstract class EntidadeMovel {
    protected double x; //posição X
    protected double y; //posição Y
    protected double width; //largura do objeto
    protected double height; //altura do objeto

    public EntidadeMovel(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Métodos abstratos
    public abstract void update();

    public abstract void render(GraphicsContext gc);

    public abstract boolean isDead();

    public abstract double getWidth();

    public abstract double getHeight();

    //Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Bounds getBounds() {
        return new Rectangle(x - getWidth() / 2, y - getHeight() / 2, getWidth(), getHeight())
                .getBoundsInLocal();
    }
}

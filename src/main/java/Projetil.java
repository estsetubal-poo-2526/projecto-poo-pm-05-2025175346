import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Projetil extends EntidadeMovel{
    /*Representa o "tiro" que vem da nave do jogador*/

    //Dimensões do projetil
    public static final int WIDTH = 4;
    public static final int HEIGHT = 15;

    //Velocidade do projetil
    private static final double SPEED = 7;

    //Serve para indicar se o projetil está "morto" ou não
    private boolean dead = false;

    /** Construtor do Projetil
     *
     *@param x posição x
     *@param y posição y
     *
     */
    public Projetil(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
    }

    //Métodos abstratos da EntidadeMóvel

    //Atualiza a posição do projetil
    @Override
    public void update() {
        y -= SPEED;
    }

    /** Apresenta o projétil no programa
     *
     * @param gc serve para "renderizar" o projétil
     */
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
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

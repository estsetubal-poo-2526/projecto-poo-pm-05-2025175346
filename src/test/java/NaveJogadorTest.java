import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NaveJogadorTest {

    private NaveJogador n;

    @BeforeEach
    void setUp(){
        n = new NaveJogador(10, 15);
    }


    @Test
    void getWidthReturnsWidth() {
        assertEquals(40, n.getWidth());
    }

    @Test
    void getHeightReturnsHeight() {
        assertEquals(40, n.getHeight());
    }

    @Test
    void isDeadReturnsFalse() {
        assertFalse(n.isDead());
    }

    @Test
    void getHealthReturnsTrue() {
        assertEquals(20, n.getHealth());
    }
}
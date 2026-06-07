import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjetilTest {

    private Projetil p;

    @BeforeEach
    void setUp(){
        p = new Projetil(10, 15);
    }


    @Test
    void getWidthReturnsWidth() {
        assertEquals(4, p.getWidth());
    }

    @Test
    void getHeightReturnsHeight() {
        assertEquals(15, p.getHeight());
    }

    @Test
    void isDeadReturnsFalse() {
        assertFalse(p.isDead());
    }
}
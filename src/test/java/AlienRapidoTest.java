import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlienRapidoTest {

    private AlienRapido a;

    @BeforeEach
    void setUp(){
        a = new AlienRapido(10, 15);
    }


    @Test
    void getWidthReturnsWidth() {
        assertEquals(35, a.getWidth());
    }

    @Test
    void getHeightReturnsHeight() {
        assertEquals(35, a.getHeight());
    }

    @Test
    void isDeadReturnsFalse() {
        assertFalse(a.isDead());
    }
}
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlienComumTest {

    private AlienComum a;

    @BeforeEach
    void setUp(){
        a = new AlienComum(10, 15);
    }


    @Test
    void getWidthReturnsWidth() {
        assertEquals(30, a.getWidth());
    }

    @Test
    void getHeightReturnsHeight() {
        assertEquals(30, a.getHeight());
    }

    @Test
    void isDeadReturnsFalse() {
        assertFalse(a.isDead());
    }
}
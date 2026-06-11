import org.example.Models.AlienTanque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlienTanqueTest {

    private AlienTanque a;

    @BeforeEach
    void setUp(){
        a = new AlienTanque(10, 15);
    }


    @Test
    void getWidthReturnsWidth() {
        assertEquals(40, a.getWidth());
    }

    @Test
    void getHeightReturnsHeight() {
        assertEquals(40, a.getHeight());
    }

    @Test
    void isDeadReturnsFalse() {
        assertFalse(a.isDead());
    }

    @Test
    void isDeadReturnsTrue() {
        a.takeDamage();
        a.takeDamage();
        assertTrue(a.isDead());
    }
}
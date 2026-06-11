import org.example.PowerUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PowerUpTest {

    private PowerUp p;

    @BeforeEach
    void setUp(){
        p = new PowerUp(10, 15);
    }


    @Test
    void getWidthReturnsWidth() {
        assertEquals(20, p.getWidth());
    }

    @Test
    void getHeightReturnsHeight() {
        assertEquals(20, p.getHeight());
    }

    @Test
    void isDeadReturnsFalse() {
        assertFalse(p.isDead());
    }
}
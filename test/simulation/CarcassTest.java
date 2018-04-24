package simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import simulation.fields.Carcass;
import utils.Vector;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CarcassTest {

    Carcass carcass;

    @BeforeEach
    public void beforeEach() {
        carcass = new Carcass(50, new Vector(0, 0));
    }

    @Test
    public void consume() {
        carcass.consume(10);
        assertEquals(40, carcass.getNutrition());
    }

    @Test
    public void isAlive() {
        assertTrue(carcass.isAlive());
    }

    @Test
    public void isDead() {
        carcass.consume(50);
        assertFalse(carcass.isAlive());
    }
}

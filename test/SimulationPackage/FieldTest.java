package SimulationPackage;

import GFX.ColorRGB;
import SimulationPackage.Entities.Field;
import VectorPackage.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {
    private Field subject01 = null;
    private Field subject02 = null;

    @BeforeEach
    public void beforeEach() {
        subject01 = new Field(new Vector(0, 0), 2, new ColorRGB(0, 0, 0));
        subject02 = new Field(new Vector(0, 0), 3, new ColorRGB(0, 0, 0));
    }

    @Test
    public void getPosition() {
        Vector pos = subject01.getPosition();
        assertTrue(pos.x == 0 && pos.y == 0);
    }

    @Test
    public void getRadius() {
        assertEquals(2, subject01.getRadius());
    }

    @Test
    public void getColor() {
        ColorRGB testColor = new ColorRGB(0, 0, 0);
        assertTrue(subject01.getColor().equals(testColor));
    }

    @Test
    public void setPosition() {
        Vector newPos = new Vector(0, 0);
        subject01.setPosition(newPos);
        assertTrue(subject01.getPosition().equals(newPos));
    }

    @Test
    public void setRadius() {
        subject01.setRadius(5);
        assertEquals(5, subject01.getRadius());
    }

    @Test
    public void setColor() {
        ColorRGB testColor = new ColorRGB(10, 50, 20);
        subject01.setColor(testColor);
        assertTrue(subject01.getColor().equals(testColor));
    }

    @Test
    public void setCollidingTrue() {
        assertTrue(subject01.isColliding(subject02));
    }

    @Test
    public void setCollidingFalse() {
        Vector newPos = new Vector(10, 10);
        subject02.setPosition(newPos);
        assertFalse(subject01.isColliding(subject02));
    }
}

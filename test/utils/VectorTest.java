package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VectorTest {

    Vector vector1, vector2;

    @BeforeEach
    public void beforeEach() {
        vector1 = new Vector(0 ,0);
        vector2 = new Vector(0 ,0);
    }

    @Test
    public void equality() {
        assertTrue(vector1.equals(vector2));
    }
}

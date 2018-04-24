package simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FishGenomeTest {

    FishGenome genome;
    FishGenome genome2;

    @BeforeEach
    public void beforeEach() {
        Settings.defaultAbbreviated();
        Settings.toFile("default"); //create a default settings profile
        Settings.fromFile("default"); //use the default profile
        genome = new FishGenome();
        genome2 = new FishGenome(genome);
    }

    @Test
    public void mutate() {
        float[] previousAttributes = genome.getArray();
        genome.mutate();
        int i = 0;
        boolean isMutated = false;
        for (float attribute : genome.getArray()) {
            if (attribute != previousAttributes[i]) {
                isMutated = true;
            }
            i++;
        }
        assertTrue(isMutated);
    }

    @Test
    public void calculateSimilarityInterval() {
        float x = genome.calculateSimilarity(genome);
        assertTrue(0 <= x && x <= 1);
    }

    @Test
    public void calculateSimilarityEqual() {
        assertEquals(1, genome.calculateSimilarity(genome));
    }

    @Test
    public void calculateSimilarity() {
        genome2.mutate();
        assertTrue(genome.calculateSimilarity(genome2) < 1);
    }
}

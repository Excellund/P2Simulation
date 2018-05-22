package simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import utils.Color;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static simulation.Settings.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FishGenomeTest {

    FishGenome genome;
    FishGenome genomeCopy;
    FishGenome genomeCrossOver;
    FishGenome genomeFromArray;
    FishGenome genomeSpecificAttributes;

    @BeforeEach
    public void beforeEach() {
        defaultAbbreviated();
        useAbbreviated();
        genome = new FishGenome();
        genomeCopy = new FishGenome(genome);
        genomeCrossOver = new FishGenome(new FishGenome(), new FishGenome());
        float[] genomeArray = {0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 255f, 255f, 255f};
        genomeFromArray = new FishGenome(genomeArray, new FishGenome(), new FishGenome());
        genomeSpecificAttributes = new FishGenome(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 0.11f, new Color(255, 255, 255), new FishGenome(), new FishGenome());
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
    public void fullMutation() {
        float[] previousAttributes = genome.getArray();
        EXPECTED_MUTATION_AMOUNT = 1000;
        genome.mutate();
        int i = 0;
        boolean allIsMutated = true;
        for (float attribute : genome.getArray()) {
            if (attribute == previousAttributes[i]) {
                allIsMutated = false;
            }
            i++;
        }
        assertTrue(allIsMutated);
    }

    @Test
    public void mutationBounds() {
        MUTATION_GAUSSIAN_VARIANCE = 1000;
        genome.mutate();
        boolean isBoundsCorrect = true;
        for (float attribute : genome.getArray()) {
            if (!(0 <= attribute && attribute <= 1)) {
                isBoundsCorrect = false;
            }
        }
        assertTrue(isBoundsCorrect);
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
        genomeCopy.mutate();
        assertTrue(genome.calculateSimilarity(genomeCopy) < 1);
    }

    @Test
    public void getSize() {
        assertEquals(0.1f, genomeSpecificAttributes.getSize());
    }

    @Test
    public void getHerbivoreEfficiency() {
        assertEquals(0.3f, genomeSpecificAttributes.getHerbivoreEfficiency());
    }

    @Test
    public void getCarnivoreEfficiency() {
        assertEquals(0.4f, genomeSpecificAttributes.getCarnivoreEfficiency());
    }

    @Test
    public void getHerbivoreTendency() {
        assertEquals(0.5f, genomeSpecificAttributes.getHerbivoreTendency());
    }

    @Test
    public void getPredationTendency() {
        assertEquals(0.6f, genomeSpecificAttributes.getPredationTendency());
    }

    @Test
    public void getScavengeTendency() {
        assertEquals(0.7f, genomeSpecificAttributes.getScavengeTendency());
    }

    @Test
    public void getSchoolingTendency() {
        assertEquals(0.8f, genomeSpecificAttributes.getSchoolingTendency());
    }

    @Test
    public void getAttackAbility() {
        assertEquals(0.9f, genomeSpecificAttributes.getAttackAbility());
    }

    @Test
    public void getColor() {
        assertEquals(new Color(255, 255, 255).getIntRepresentation(), genomeSpecificAttributes.getColor().getIntRepresentation());
    }

    @Test
    public void parentGenomeAClass() {
        assertEquals(FishGenome.class, genomeSpecificAttributes.getParentGenomeA().getClass());
    }

    @Test
    public void parentGenomeBClass() {
        assertEquals(FishGenome.class, genomeSpecificAttributes.getParentGenomeB().getClass());
    }

    @Test
    public void cloneConstructor() {
        FishGenome genome1 = genome;
        FishGenome genome2 = new FishGenome(genome);

        assertEquals(genome1, genome2);
    }

    @Test
    public void toArrayTest() {
        float[] genomeArray1 = genome.getArray();
        float[] genomeArray2 = new FishGenome(genome).getArray();

        assertTrue(Arrays.equals(genomeArray1, genomeArray2));
    }

    @Test
    public void FromArrayTest() {
        FishGenome genome1 = genome;
        FishGenome genome2 = new FishGenome(genome.getArray(), genome.getParentGenomeA(), genome.getParentGenomeB());

        assertTrue(Arrays.equals(genome1.getArray(), genome2.getArray()));
    }
}

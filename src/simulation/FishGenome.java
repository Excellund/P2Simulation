package simulation;

import utils.CountingRandom;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FishGenome {
    // All attributes (genes) will have values between 0 and 1
    private float size;
    private float speed;

    private float herbivoreEfficiency;
    private float carnivoreEfficiency;

    private float herbivoreTendency;
    private float predationTendency;
    private float scavengeTendency;
    private float schoolingTendency;

    private float attackAbility; //Amount of damage capable of doing to other fish.

    private float numSpawns; //Number of eggs laid
    private float spawnSize; //Size of fish at birth/hatch

    private FishGenome parentGenomeA, parentGenomeB;

    public FishGenome(float size, float speed, float herbivoreEfficiency, float carnivoreEfficiency, float herbivoreTendency, float predationTendency, float scavengeTendency, float schoolingTendency, float attackAbility, float numSpawns, float spawnSize, FishGenome parentGenomeA, FishGenome parentGenomeB) {
        setGenes(
                size,
                speed,
                herbivoreEfficiency,
                carnivoreEfficiency,
                herbivoreTendency,
                predationTendency,
                scavengeTendency,
                schoolingTendency,
                attackAbility,
                numSpawns,
                spawnSize,
                parentGenomeA,
                parentGenomeB
        );
    }

    // Create new genome from array of genes
    public FishGenome(float[] genome, FishGenome parentGenomeA, FishGenome parentGenomeB) {
        setGenes(
                genome[0],
                genome[1],
                genome[3],
                genome[2],
                genome[4],
                genome[5],
                genome[6],
                genome[7],
                genome[8],
                genome[9],
                genome[10],
                parentGenomeA,
                parentGenomeB
        );
    }

    // Copies other genome.
    public FishGenome(FishGenome other) {
        setGenes(
                other.size,
                other.speed,
                other.herbivoreEfficiency,
                other.carnivoreEfficiency,
                other.herbivoreTendency,
                other.predationTendency,
                other.scavengeTendency,
                other.schoolingTendency,
                other.attackAbility,
                other.numSpawns,
                other.spawnSize,
                other.parentGenomeA,
                other.parentGenomeB
        );
    }

    // Creates new genome from a mix of two others
    public FishGenome(FishGenome genomeA, FishGenome genomeB) {
        float genomeArrayA[] = genomeA.getArray();
        float genomeArrayB[] = genomeB.getArray();
        float genomeArrayAA[] = genomeA.parentGenomeA.getArray();
        float genomeArrayAB[] = genomeA.parentGenomeB.getArray();
        float genomeArrayBA[] = genomeB.parentGenomeA.getArray();
        float genomeArrayBB[] = genomeB.parentGenomeB.getArray();

        int numGenes = genomeArrayA.length;
        float genomeResultArray[] = new float[numGenes];

        Random r = CountingRandom.getInstance();

        for (int i = 0; i < numGenes; i++) {
            float chance = r.nextFloat();

            if (chance < 0.4) {        // 40% chance
                genomeResultArray[i] = genomeArrayA[i];  // Inherit from parent A
            } else if (chance < 0.8) { // 40% chance
                genomeResultArray[i] = genomeArrayB[i];  // Inherit from parent B
            } else if (chance < 0.85) { // 5% chance
                genomeResultArray[i] = genomeArrayAA[i]; // Inherit from parent A of parent A
            } else if (chance < 0.9) { // 5% chance
                genomeResultArray[i] = genomeArrayAB[i]; // Inherit from parent B of parent A
            } else if (chance < 0.95) { // 5% chance
                genomeResultArray[i] = genomeArrayBA[i]; // Inherit from parent A of parent B
            } else {                   // 5% chance
                genomeResultArray[i] = genomeArrayBB[i]; // Inherit from parent B of parent B
            }
        }

        setGenes(
                genomeResultArray[0],
                genomeResultArray[1],
                genomeResultArray[2],
                genomeResultArray[3],
                genomeResultArray[4],
                genomeResultArray[5],
                genomeResultArray[6],
                genomeResultArray[7],
                genomeResultArray[8],
                genomeResultArray[9],
                genomeResultArray[10],
                genomeA,
                genomeB
        );
    }

    // Create random genome
    public FishGenome() {
        Random r = CountingRandom.getInstance();

        this.size = r.nextFloat();
        this.speed = r.nextFloat();

        this.herbivoreEfficiency = r.nextFloat();
        this.carnivoreEfficiency = r.nextFloat();
        this.herbivoreTendency = r.nextFloat();
        this.predationTendency = r.nextFloat();
        this.scavengeTendency = r.nextFloat();

        this.attackAbility = r.nextFloat();
        this.numSpawns = r.nextFloat();

        float spawnSize = r.nextFloat();
        this.spawnSize = spawnSize - this.size < 0 ? spawnSize / 4 : this.size / 4; // Do something to ensure spawnsize is less than size.

        stripUnneededGenomeReferences();
    }

    //TODO: relevant values as arguments (How much to mutate). Implement properly
    public void mutate(int expectedMutationAmount, float mean, float variance, float interval) {
        // Poisson fordeling
        Random r = CountingRandom.getInstance();

        double randomNumber = r.nextDouble();

        // Find the amount of mutations to perform using poisson distribution.
        int attributeMutateAmount = 0;
        double previousProbability = poisson(expectedMutationAmount, attributeMutateAmount);
        double area = 0;
        while(randomNumber > area) {
            attributeMutateAmount++;
            double currentProbability = poisson(expectedMutationAmount, attributeMutateAmount);
            area += currentProbability;
            if (currentProbability < previousProbability) {
                area += (previousProbability - currentProbability) / 2;
            } else {
                area -= (previousProbability - currentProbability) / 2;
            }
        }
        attributeMutateAmount--;
        float[] attributeArray = this.getArray();
        int[] indices = new int[attributeMutateAmount];

        Set<Float> mutatedAttributes = new HashSet();

        // Find the attributes to mutate.
        while(mutatedAttributes.size() < attributeMutateAmount) {
            int randomIndex = r.nextInt(attributeMutateAmount);
            if (mutatedAttributes.add(attributeArray[randomIndex])) {
                indices[mutatedAttributes.size() - 1] = randomIndex;
            }

        }

        for (float element : mutatedAttributes) {
            int i = 0;
            float probability = r.nextFloat();
            if (probability < 0.023) {
                element -= 2 * interval;
            } else if (probability < 0.159) {
                element -= interval;
            } else if (probability < 0.5) {

            } else if (probability < 0.841) {

            } else if (probability < 0.977) {
                element += interval;
            } else {
                element += 2 * interval;
            }
            attributeArray[indices[i]] = element;
            i++;
        }

        this.setAttributes(attributeArray);
    }

    private double poisson(int expectedMutationAmount, int occurrences) {
        return (Math.pow(Math.E, 0 - expectedMutationAmount) * Math.pow(expectedMutationAmount, occurrences)) / factorial(occurrences);
    }

    private double factorial(double number) {
        double result = 1;

        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }

        return result;
    }

    // Returns the similarity of two genomes. Between 0 and 1.
    public float calculateSimilarity(FishGenome other) {
        // Genomes similarity will be defined as 1 - d where d is the distance between two points in an n-dimensional space where n is the number of genes in a genome.
        // Each element of the two points refer to the value of the gene in the genome

        float[] genomeA = this.getArray();
        float[] genomeB = this.getArray();

        float distance = 0;

        for (int i = 0; i < genomeA.length; i++) {
            distance += Math.pow(genomeA[i] - genomeB[i], 2);
        }

        distance = (float) Math.sqrt(distance);
        distance = distance / (float) Math.sqrt(genomeA.length); // Normalize distance to be between 0 and 1

        return 1 - distance;
    }

    // Removes unneeded references to grandgrandparents genomes
    private void stripUnneededGenomeReferences() {
        //Make sure references to older genomes get removed in order to save memory
        if (parentGenomeA != null) {
            if (parentGenomeA.parentGenomeA != null) {
                parentGenomeA.parentGenomeA.parentGenomeA = null;
                parentGenomeA.parentGenomeA.parentGenomeB = null;
            }
            if (parentGenomeA.parentGenomeB != null) {
                parentGenomeA.parentGenomeB.parentGenomeA = null;
                parentGenomeA.parentGenomeB.parentGenomeB = null;
            }
        }
        if (parentGenomeB != null) {
            if (parentGenomeB.parentGenomeA != null) {
                parentGenomeB.parentGenomeA.parentGenomeB = null;
                parentGenomeB.parentGenomeB.parentGenomeA = null;
            }
            if (parentGenomeB.parentGenomeB != null) {
                parentGenomeB.parentGenomeA.parentGenomeA = null;
                parentGenomeB.parentGenomeB.parentGenomeB = null;
            }
        }
    }

    public void print() {
        System.out.println("Size: " + size);
        System.out.println("Speed: " + speed);
        System.out.println("HerbivoreEfficiency: " + herbivoreEfficiency);
        System.out.println("CarnivoreEfficiency: " + carnivoreEfficiency);
        System.out.println("HerbivoreTendency: " + herbivoreTendency);
        System.out.println("PredationTendency: " + predationTendency);
        System.out.println("ScavengeTendency: " + scavengeTendency);
        System.out.println("SchoolingTendency: " + schoolingTendency);
        System.out.println("AttackAbility: " + attackAbility);
        System.out.println("NumSpawns: " + numSpawns);
        System.out.println("SpawnSize: " + spawnSize);
    }

    // Setters
    private void setGenes(float size, float speed, float herbivoreEfficiency, float carnivoreEfficiency, float herbivoreTendency, float predationTendency, float scavengeTendency, float schoolingTendency, float attackAbility, float numSpawns, float spawnSize, FishGenome parentGenomeA, FishGenome parentGenomeB) {
        this.size = size;
        this.speed = speed;
        this.herbivoreEfficiency = herbivoreEfficiency;
        this.carnivoreEfficiency = carnivoreEfficiency;
        this.herbivoreTendency = herbivoreTendency;
        this.predationTendency = predationTendency;
        this.scavengeTendency = scavengeTendency;
        this.schoolingTendency = schoolingTendency;
        this.attackAbility = attackAbility;
        this.numSpawns = numSpawns;
        this.spawnSize = spawnSize;
        this.parentGenomeA = parentGenomeA;
        this.parentGenomeB = parentGenomeB;

        stripUnneededGenomeReferences();
    }

    // Getters
    // Get genome represented as a float array
    private float[] getArray() {
        return new float[]{
                this.size,
                this.speed,
                this.herbivoreEfficiency,
                this.carnivoreEfficiency,
                this.herbivoreTendency,
                this.predationTendency,
                this.scavengeTendency,
                this.schoolingTendency,
                this.attackAbility,
                this.numSpawns,
                this.spawnSize
        };
    }

    private void setAttributes(float[] array) {
        this.size = array[0];
        this.speed = array[1];
        this.herbivoreEfficiency = array[2];
        this.carnivoreEfficiency = array[3];
        this.herbivoreTendency = array[4];
        this.predationTendency = array[5];
        this.scavengeTendency = array[6];
        this.schoolingTendency = array[7];
        this.attackAbility = array[8];
        this.numSpawns = array[9];
        this.spawnSize = array[10];
    }

}

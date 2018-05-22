package simulation;

import simulation.exceptions.InvalidAttributeException;
import utils.Color;
import utils.CountingRandom;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class FishGenome {
    public static final int NUM_ATTRIBUTES = 13;

    //All attributes (genes) will have values between 0 and 1
    private float size; //Max size of fish
    private float speed; //Speed of fish
    private Color color; //Color of fish

    private float herbivoreEfficiency; //Determines how much energy is gained from eating plankton
    private float carnivoreEfficiency; //Determines how much energy is gained from eating fish

    private float herbivoreTendency; //Tendency to seek plankton
    private float predationTendency; //Tendency to attack other fish
    private float scavengeTendency; //Tendency to eat dead fish
    private float schoolingTendency; //Tendency to shcool

    private float attackAbility; //Amount of damage capable of doing to other fish.

    private float spawnSize; //Size of fish at birth/hatch

    private FishGenome parentGenomeA, parentGenomeB;

    //Creates a FishGenome from specified attributes
    public FishGenome(float size, float speed, float herbivoreEfficiency, float carnivoreEfficiency, float herbivoreTendency, float predationTendency, float scavengeTendency, float schoolingTendency, float attackAbility, float spawnSize, Color color, FishGenome parentGenomeA, FishGenome parentGenomeB) {
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
                spawnSize,
                color,
                parentGenomeA,
                parentGenomeB
        );
    }

    //Creates new genome from a mix of two others
    public FishGenome(FishGenome genomeA, FishGenome genomeB) {
        float[] genomeArrayA = genomeA.getArray();
        float[] genomeArrayB = genomeB.getArray();
        float[] genomeArrayAA = genomeA.parentGenomeA.getArray();
        float[] genomeArrayAB = genomeA.parentGenomeB.getArray();
        float[] genomeArrayBA = genomeB.parentGenomeA.getArray();
        float[] genomeArrayBB = genomeB.parentGenomeB.getArray();

        int numGenes = genomeArrayA.length;
        float[] genomeResultArray = new float[numGenes];

        Random r = CountingRandom.getInstance();

        //For each attribute, chose what parent/grandparent to inherit from.
        for (int i = 0; i < numGenes; i++) {
            float chance = r.nextFloat();

            if (chance < 0.4) {        //40% chance
                genomeResultArray[i] = genomeArrayA[i];  //Inherit from parent A
            } else if (chance < 0.8) { //40% chance
                genomeResultArray[i] = genomeArrayB[i];  //Inherit from parent B
            } else if (chance < 0.85) { //5% chance
                genomeResultArray[i] = genomeArrayAA[i]; //Inherit from parent A of parent A
            } else if (chance < 0.9) { //5% chance
                genomeResultArray[i] = genomeArrayAB[i]; //Inherit from parent B of parent A
            } else if (chance < 0.95) { //5% chance
                genomeResultArray[i] = genomeArrayBA[i]; //Inherit from parent A of parent B
            } else {                   //5% chance
                genomeResultArray[i] = genomeArrayBB[i]; //Inherit from parent B of parent B
            }
        }

        //Apply the new attributes
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
                new Color(genomeResultArray[10], genomeResultArray[11], genomeResultArray[12]),
                new FishGenome(genomeA),
                new FishGenome(genomeB)
        );
    }

    //Creates random genome
    public FishGenome() {
        Random r = CountingRandom.getInstance();

        this.size = r.nextFloat();
        this.speed = r.nextFloat();
        this.herbivoreEfficiency = r.nextFloat();
        this.carnivoreEfficiency = r.nextFloat();
        this.herbivoreTendency = r.nextFloat();
        this.predationTendency = r.nextFloat();
        this.scavengeTendency = r.nextFloat();
        this.schoolingTendency = r.nextFloat();
        this.attackAbility = r.nextFloat();
        this.spawnSize = size / (r.nextFloat() * Settings.MAX_FISH_SIZE);
        this.color = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));

        parentGenomeA = new FishGenome(this);
        parentGenomeB = new FishGenome(this);

        stripUnneededGenomeReferences();
    }

    //Create new genome from array of genes
    public FishGenome(float[] genome, FishGenome parentGenomeA, FishGenome parentGenomeB) {
        //Throw an exception if genome array is an invalid size
        if (genome.length != NUM_ATTRIBUTES) {
            throw new InvalidAttributeException();
        }

        setGenes(
                genome[0],
                genome[1],
                genome[2],
                genome[3],
                genome[4],
                genome[5],
                genome[6],
                genome[7],
                genome[8],
                genome[9],
                new Color(genome[10], genome[11], genome[12]),
                parentGenomeA,
                parentGenomeB
        );
    }

    //Copies other genome
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
                other.spawnSize,
                other.color,
                other.parentGenomeA,
                other.parentGenomeB
        );
    }

    //Mutates a genome randomly
    public void mutate() {
        Random r = CountingRandom.getInstance();

        float[] attributes = getArray();

        //Generate number of mutations to perform based on a poisson distribution
        int numMutations = generatePoissonDistributedNumber((int) Settings.EXPECTED_MUTATION_AMOUNT);

        //Ensure there are no more mutations than number of attributes
        if (numMutations > attributes.length) {
            numMutations = attributes.length;
        }

        //Select attributes to mutate.
        Set<Integer> mutationIndices = new HashSet<>();
        while (mutationIndices.size() < numMutations) {
            mutationIndices.add(r.nextInt(attributes.length));
        }

        //Mutate attributes
        for (int index : mutationIndices) {
            //Mutate using a normal distribution
            attributes[index] += r.nextGaussian() * Settings.MUTATION_GAUSSIAN_VARIANCE;

            //Make sure attributes are within bounds
            if (attributes[index] < 0) {
                attributes[index] = 0;
            } else if (attributes[index] > 1) {
                attributes[index] = 1;
            }
        }

        setAttributes(attributes);
    }

    //Java implementation of Donald Knuth's algorithm to generate a random Poisson-distributed number, as described in his book "The Art of Computer Programming, Volume 2"
    //Algorithm also described on wikipedia: https://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables
    private int generatePoissonDistributedNumber(int lambda) {
        Random rand = CountingRandom.getInstance();

        float l = (float) Math.pow(Math.E, -lambda);
        int k = 0;
        float p = 1;

        do {
            k++;
            p = p * rand.nextFloat();
        }
        while (p > l);

        return k - 1;
    }

    //Returns the similarity of two genomes. Return a value between 0 and 1.
    public float calculateSimilarity(FishGenome other) {
        //Genomes similarity will be defined as 1 - d where d is the distance between two points in an n-dimensional space where n is the number of genes in a genome.
        //Each element of the two points refer to the value of the gene in the genome

        float[] genomeA = this.getArray();
        float[] genomeB = other.getArray();

        float distance = 0;

        //Calculate the square of the length of the vector between the two points corresponding to the attributes
        for (int i = 0; i < NUM_ATTRIBUTES; i++) {
            distance += Math.pow(genomeA[i] - genomeB[i], 2);
        }

        distance = (float) Math.sqrt(distance); //Convert from distance squared to distance
        distance = distance / (float) Math.sqrt(NUM_ATTRIBUTES); //Normalize distance to be between 0 and 1

        return 1 - distance; //Subtract the distance from 1 to find the similarity instead of the difference
    }

    //Removes unneeded references to grandgrandparents genomes in order to reclaim memory
    private void stripUnneededGenomeReferences() {
        stripGenomeReference(parentGenomeA);
        stripGenomeReference(parentGenomeB);
    }

    private void stripGenomeReference(FishGenome genome) {
        if (genome != null) {
            stripGenomeReference(genome.parentGenomeA);
            stripGenomeReference(genome.parentGenomeB);
            genome.parentGenomeA = null;
            genome.parentGenomeB = null;
        }
    }

    //Setters
    //Set attributes to specified values
    private void setGenes(float size, float speed, float herbivoreEfficiency, float carnivoreEfficiency, float herbivoreTendency, float predationTendency, float scavengeTendency, float schoolingTendency, float attackAbility, float spawnSize, Color color, FishGenome parentGenomeA, FishGenome parentGenomeB) {
        this.size = size;
        this.speed = speed;
        this.herbivoreEfficiency = herbivoreEfficiency;
        this.carnivoreEfficiency = carnivoreEfficiency;
        this.herbivoreTendency = herbivoreTendency;
        this.predationTendency = predationTendency;
        this.scavengeTendency = scavengeTendency;
        this.schoolingTendency = schoolingTendency;
        this.attackAbility = attackAbility;
        this.spawnSize = spawnSize;
        this.color = color;
        this.parentGenomeA = parentGenomeA;
        this.parentGenomeB = parentGenomeB;

        stripUnneededGenomeReferences();
    }

    //Set attributes from float array
    private void setAttributes(float[] array) {
        setGenes(
                array[0],
                array[1],
                array[2],
                array[3],
                array[4],
                array[5],
                array[6],
                array[7],
                array[8],
                array[9],
                new Color(array[10], array[11], array[12]),
                this.parentGenomeA,
                this.parentGenomeB

        );
    }

    // Getters
    // Get genome represented as a float array in order to make it easier to iterate attributes
    public float[] getArray() {
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
                this.spawnSize,
                this.color.getRedNormalized(),
                this.color.getGreenNormalized(),
                this.color.getBlueNormalized()
        };
    }

    public float getSize() {
        return size;
    }

    public float getHerbivoreEfficiency() {
        return herbivoreEfficiency;
    }

    public float getCarnivoreEfficiency() {
        return carnivoreEfficiency;
    }

    public float getHerbivoreTendency() {
        return herbivoreTendency;
    }

    public float getPredationTendency() {
        return predationTendency;
    }

    public float getScavengeTendency() {
        return scavengeTendency;
    }

    public float getSchoolingTendency() {
        return schoolingTendency;
    }

    public float getAttackAbility() {
        return attackAbility;
    }

    public Color getColor() {
        return color;
    }

    public float getSpeed() {
        return speed;
    }

    public float getSpawnSize() {
        return spawnSize;
    }

    public FishGenome getParentGenomeA() {
        return parentGenomeA;
    }

    public FishGenome getParentGenomeB() {
        return parentGenomeB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FishGenome that = (FishGenome) o;
        return Float.compare(that.size, size) == 0 &&
                Float.compare(that.speed, speed) == 0 &&
                Float.compare(that.herbivoreEfficiency, herbivoreEfficiency) == 0 &&
                Float.compare(that.carnivoreEfficiency, carnivoreEfficiency) == 0 &&
                Float.compare(that.herbivoreTendency, herbivoreTendency) == 0 &&
                Float.compare(that.predationTendency, predationTendency) == 0 &&
                Float.compare(that.scavengeTendency, scavengeTendency) == 0 &&
                Float.compare(that.schoolingTendency, schoolingTendency) == 0 &&
                Float.compare(that.attackAbility, attackAbility) == 0 &&
                Float.compare(that.spawnSize, spawnSize) == 0 &&
                Objects.equals(color, that.color) &&
                Objects.equals(parentGenomeA, that.parentGenomeA) &&
                Objects.equals(parentGenomeB, that.parentGenomeB);
    }

    @Override
    public int hashCode() {

        return Objects.hash(size, speed, color, herbivoreEfficiency, carnivoreEfficiency, herbivoreTendency, predationTendency, scavengeTendency, schoolingTendency, attackAbility, spawnSize, parentGenomeA, parentGenomeB);
    }
}

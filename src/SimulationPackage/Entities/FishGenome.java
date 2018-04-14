package SimulationPackage.Entities;

import utils.CountingRandom;

import java.util.Random;

public class FishGenome {
    private float size;
    private float speed;

    //Between 0 and 1
    private float herbivoreEfficiency;
    private float carnivoreEfficiency;

    //Between 0 and 1
    private float herbivoreTendency;
    private float predationTendency;
    private float scavengeTendency;

    private float attackAbility; //Amount of damage capable of doing to other fish.

    private float numSpawns; //Number of eggs laid
    private float spawnSize; //Size of fish at birth/hatch

    private FishGenome parentGenomeA, parentGenomeB;

    public FishGenome(float size, float speed, float herbivoreEfficiency, float carnivoreEfficiency, float herbivoreTendency, float predationTendency, float scavengeTendency, float attackAbility, int numSpawns, float spawnSize, FishGenome parentGenomeA, FishGenome parentGenomeB) {
        this.size = size;
        this.speed = speed;
        this.herbivoreEfficiency = herbivoreEfficiency;
        this.carnivoreEfficiency = carnivoreEfficiency;
        this.herbivoreTendency = herbivoreTendency;
        this.predationTendency = predationTendency;
        this.scavengeTendency = scavengeTendency;
        this.attackAbility = attackAbility;
        this.numSpawns = numSpawns;
        this.spawnSize = spawnSize;
        this.parentGenomeA = parentGenomeA;
        this.parentGenomeB = parentGenomeB;
    }

    public FishGenome(float[] genome, FishGenome parentGenomeA, FishGenome parentGenomeB) {
        if (genome.length < 10) {
            throw new IllegalArgumentException("Not enough genes provided.");
        }

        this.size = genome[0];
        this.speed = genome[1];
        this.herbivoreEfficiency = genome[2];
        this.carnivoreEfficiency = genome[3];
        this.herbivoreTendency = genome[4];
        this.predationTendency = genome[5];
        this.scavengeTendency = genome[6];
        this.attackAbility = genome[7];
        this.numSpawns = genome[8];
        this.spawnSize = genome[9];
        this.parentGenomeA = parentGenomeA;
        this.parentGenomeB = parentGenomeB;
    }

    //Copies other genome.
    public FishGenome(FishGenome other) {
        this.size = other.size;
        this.speed = other.speed;
        this.herbivoreEfficiency = other.herbivoreEfficiency;
        this.carnivoreEfficiency = other.carnivoreEfficiency;
        this.herbivoreTendency = other.herbivoreTendency;
        this.predationTendency = other.predationTendency;
        this.scavengeTendency = other.scavengeTendency;
        this.attackAbility = other.attackAbility;
        this.numSpawns = other.numSpawns;
        this.spawnSize = other.spawnSize;
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

            if (chance < 0.4) {
                genomeResultArray[i] = genomeArrayA[i];
            }
            else if (chance < 0.45) {
                genomeResultArray[i] = genomeArrayAA[i];
            }
            else if (chance < 0.5) {
                genomeResultArray[i] = genomeArrayAB[i];
            }
            else if (chance < 0.9){
                genomeResultArray[i] = genomeArrayB[i];
            }
            else if (chance < 0.95) {
                genomeResultArray[i] = genomeArrayBA[i];
            }
            else {
                genomeResultArray[i] = genomeArrayBB[i];
            }
        }

        this.size = genomeResultArray[0];
        this.speed = genomeResultArray[1];
        this.herbivoreEfficiency = genomeResultArray[2];
        this.carnivoreEfficiency = genomeResultArray[3];
        this.herbivoreTendency = genomeResultArray[4];
        this.predationTendency = genomeResultArray[5];
        this.scavengeTendency = genomeResultArray[6];
        this.attackAbility = genomeResultArray[7];
        this.numSpawns = genomeResultArray[8];
        this.spawnSize = genomeResultArray[9];
        this.parentGenomeA = genomeA;
        this.parentGenomeB = genomeB;
    }

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
        this.spawnSize = spawnSize - this.size < 0 ? spawnSize / 4 : this.size / 4;
    }

    //TODO: relevant values as arguments (How much to mutate)
    public void mutate(float rate) {
        //Skal tage højde for forældres forældres gener
        Random r = CountingRandom.getInstance();

        this.size += (r.nextFloat() - 0.5) * rate;
        this.speed += (r.nextFloat() - 0.5) * rate;
    }

    public float calculateSimilarity(FishGenome other) {
        float distance = 0;

        distance += Math.pow(this.size - other.size, 2);
        distance += Math.pow(this.speed - other.speed, 2);
        distance += Math.pow(this.herbivoreEfficiency - other.herbivoreEfficiency, 2);
        distance += Math.pow(this.carnivoreEfficiency - other.carnivoreEfficiency, 2);
        distance += Math.pow(this.herbivoreTendency - other.herbivoreTendency, 2);
        distance += Math.pow(this.predationTendency - other.predationTendency, 2);
        distance += Math.pow(this.scavengeTendency - other.scavengeTendency, 2);
        distance += Math.pow(this.attackAbility - other.attackAbility, 2);
        distance += Math.pow(this.numSpawns - other.numSpawns, 2);
        distance += Math.pow(this.spawnSize - other.spawnSize, 2);

        distance = (float) Math.sqrt(distance);

        distance = 1 - distance / (float) Math.sqrt(10);

        return distance;
    }

    private float[] getArray() {
        return new float[] {
                this.size,
                this.speed,
                this.herbivoreEfficiency,
                this.carnivoreEfficiency,
                this.herbivoreTendency,
                this.predationTendency,
                this.scavengeTendency,
                this.attackAbility,
                this.numSpawns,
                this.spawnSize
        };
    }

    public void print() {
        System.out.println("Size: " + size);
        System.out.println("Speed: " + speed);
        System.out.println("HerbivoreEfficiency: " + herbivoreEfficiency);
        System.out.println("CarnivoreEfficiency: " + carnivoreEfficiency);
        System.out.println("HerbivoreTendency: " + herbivoreTendency);
        System.out.println("PredationTendency: " + predationTendency);
        System.out.println("ScavengeTendency: " + scavengeTendency);
        System.out.println("AttackAbility: " + attackAbility);
        System.out.println("NumSpawns: " + numSpawns);
        System.out.println("SpawnSize: " + spawnSize);
    }


    //Getters

}

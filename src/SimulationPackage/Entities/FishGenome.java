package SimulationPackage.Entities;

public class FishGenome {
    private float size;
    private float speed;

    //Between 0 and 1
    private float herbivoreEfficiency;
    private float carnivoreEfficiency;

    private float herbivoreTendency;
    private float predationTendency;
    private float scavengeTendency;

    private float attackAbility; //Amount of damage capable of doing to other fish.

    private int numSpawns; //Number of eggs laid
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

    public FishGenome(FishGenome genomeA, FishGenome genomeB) {
        //Mix the two genes somehow.
    }

    //TODO: relevant values as arguments (How much to mutate)
    public void mutate() {
        //Skal tage højde for forældres forældres gener
    }

    public float calculateSimilarity(FishGenome other) {
        return 1;
    }
}

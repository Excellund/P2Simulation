package Simulation;

public class Fish {

    private float health;
    private float energy;
    private float size;
    private float speed;
    private FishGenome genome;

    public Fish(FishGenome genome) {
        this.genome = genome;
    }

    //How compatible a fish is with another. This determines the likelyhood of them mating.
    public float getCompatibility(Fish other) {
        float genomeSimilarity = genome.calculateSimilarity(other.genome);

        //Logistisk funktion
        //1/(1+e^(-50(x-0.85)))
        return (float) (1 / (1 + Math.pow((float) Math.E, -50 * (genomeSimilarity-0.85))));
    }

    //Call every time step
    public void update() {
        //Decrease energy
        //Decrease health if energy low
        //Increase health if energy high
        //Increase size if energy high and is able to grow due to genome
    }

    public void attack(Fish other) {

    }

    public Fish mate(Fish mate) {
        FishGenome childGenome = new FishGenome(this.genome, mate.getGenome());
        childGenome.mutate(0.1f);
        return new Fish(childGenome);
    }


    //Getters
    public float getHealth() {
        return health;
    }

    public float getEnergy() {
        return energy;
    }

    public float getSize() {
        return size;
    }

    public float getSpeed() {
        return speed;
    }

    public FishGenome getGenome() {
        return genome;
    }
}

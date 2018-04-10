package SimulationPackage.Entities;

public class Carcass {
    private int nutrition;

    public void consume(int amount) {
        nutrition -= amount;
    }

    public int getNutrition() {
        return nutrition;
    }
}

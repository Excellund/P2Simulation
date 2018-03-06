package SimulationPackage.Entities;

public class Fish implements FishInterface {
    // Fields:
    private double weight;
    private double length;
    private double bwd;
    private double age;

    // Constructor:
    public Fish(double weight, double length, double bwd, double age) {
        this.weight = weight;
        this.length = length;
        this.bwd = bwd;
        this.age = age;
    }

    // Methods:
    public double calculateMortalityRate() {
        //TODO: Implement the logic.
        return 0;
    }

    // Getters:
    public double getWeight() {
        return weight;
    }

    public double getLength() {
        return length;
    }

    public double getBwd() {
        return bwd;
    }

    public double getAge() {
        return age;
    }

    // Setters:
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setBwd(double bwd) {
        this.bwd = bwd;
    }

    public void setAge(double age) {
        this.age = age;
    }
}

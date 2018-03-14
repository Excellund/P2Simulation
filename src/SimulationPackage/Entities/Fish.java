package SimulationPackage.Entities;

public class Fish extends Field implements FishInterface {
    // Fields:
    private double weight;
    private double length;
    private double foodPerDay;
    private double age;

    // Constructor:
    public Fish(double weight, double length, double foodPerDay, double age) {
        this.weight = weight;
        this.length = length;
        this.foodPerDay = foodPerDay;
        this.age = age;
    }

    // Methods:
    public double calculateMortalityRate() {
        //TODO: Implement the logic.
        return 0;
    }

    public double calculateBwd() {
        return foodPerDay / weight;
    }

    // Getters:
    public double getWeight() {
        return weight;
    }

    public double getLength() {
        return length;
    }

    public double getFoodPerDay() {
        return foodPerDay;
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

    public void setFoodPerDay(double foodPerDay) {
        this.foodPerDay = foodPerDay;
    }

    public void setAge(double age) {
        this.age = age;
    }
}

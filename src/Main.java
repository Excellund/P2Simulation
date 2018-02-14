public class Main {
    public static void main(String[] args) {
        Display display = new Display("Test", 1920, 1080);
        Simulation sim = new Simulation(display, 60, 2);
        Thread thread = new Thread(sim);
        thread.start();
        //sim.stop();
    }
}

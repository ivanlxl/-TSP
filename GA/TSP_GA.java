import java.io.FileWriter;
import java.io.IOException;

public class TSP_GA {

    public static void main(String[] args) {
        // start the timer
        long startTime = System.nanoTime();
        readCityData();

        // Initialize population
        Population pop = new Population(50, true);
        //System.out.println("Initial distance: " + pop.getFittest().getDistance());
        //System.out.println(pop.getFittest());

        // Evolve population for 100 generations
        pop = GA.evolvePopulation(pop);
        for (int i = 0; i < 100; i++) {
            pop = GA.evolvePopulation(pop);
        }

        // end the timer
        long endTime = System.nanoTime();
        // Print final results
        System.out.println("Final distance: " + pop.getFittest().getDistance());
        System.out.println("Shortest Route: " + pop.getFittest());
        
        double totalTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Time Taken: " + totalTimeSeconds);
        System.out.println("Time Taken per iteration: " + totalTimeSeconds/100);

        // Save the solution to a new file
        try {
            FileWriter writer = new FileWriter("C:\\Users\\ivanl\\OneDrive\\Desktop\\UNM\\Year 2\\2023 Spring Sem\\2039 AIM\\Coursework\\GA.txt");
            writer.write(pop.getFittest().toString());
            writer.close();
            System.out.println("Solution saved to file.");
        } catch (IOException e) {
            System.out.println("Error saving solution to file.");
            e.printStackTrace();
        }
    }

    public static void readCityData() {
        try {
            java.io.File file = new java.io.File("TSP_107.txt");
            java.util.Scanner input = new java.util.Scanner(file);
            while (input.hasNext()) {
                int id = input.nextInt();
                int x = input.nextInt();
                int y = input.nextInt();
                City city = new City(id, x, y); // create a new City object
                TourManager.addCity(city); // add the City object to the TourManager
            }
            input.close();
        } catch (java.io.FileNotFoundException ex) {
            System.out.println("File not found.");
        }
    }
}
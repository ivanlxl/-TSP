import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class TSP2Opt {

	
	//City class used to store city node, x coordinate, and y coordinate
    static class City {
        int id;
        int x;
        int y;

        public City(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
    	// Read the city from file, specify file path
    	// MAKE SURE ONLY THE CITYNODE X COORDINATE, AND Y COORDINATE ARE IN THE FILE
        ArrayList<City> cities = readCityDataFromFile("C:\\Users\\ivanl\\OneDrive\\Desktop\\UNM\\Year 2\\2023 Spring Sem\\2039 AIM\\Coursework\\TSP_107.txt");

        // Shuffle the cities, this creates a random initial tour
        Collections.shuffle(cities);

        // Choose the starting city as the first city after shuffling
        City startCity = cities.get(0);

        // Set the number of iterations
        int iterations = 100;

        // Initialize variables to keep track of the shortest route found and its distance
        ArrayList<City> shortestRoute = null;
        double shortestDistance = Double.MAX_VALUE;

        long startTime = System.nanoTime(); //Used to calculate time
        // Run the algorithm for the specified number of iterations
        for (int i = 0; i < iterations; i++) {
            ArrayList<City> currentRoute = twoOptAlgorithm(cities, startCity); // Apply the 2-opt algorithm to find the shortest route

            double currentDistance = calculateDistance(currentRoute, startCity); // Calculate the distance of the current route

            // Check if the current route is the shortest one found so far
            if (currentDistance < shortestDistance) {
                shortestRoute = currentRoute;
                shortestDistance = currentDistance;
            }

            // Shuffle the cities for the next iteration
            Collections.shuffle(cities);
        }
        long endTime = System.nanoTime();

        // Print the shortest route and total distance
        System.out.println("Shortest Route:");
        for (City city : shortestRoute) {
            System.out.print(city.id + " ");
        }
        System.out.println("\nTotal Distance: " + shortestDistance);

        double totalTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Time Taken: " + totalTimeSeconds);
        System.out.println("Time Taken per iteration: " + totalTimeSeconds/100);

        // Print the shortest route to a file
        printShortestRouteToFile(shortestRoute, "C:\\Users\\ivanl\\OneDrive\\Desktop\\UNM\\Year 2\\2023 Spring Sem\\2039 AIM\\Coursework\\2Opt.txt");
    }
   
public static ArrayList<City> readCityDataFromFile(String filename) {
        ArrayList<City> cities = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File(filename));

            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(" ");
                int id = Integer.parseInt(parts[0]);
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                City city = new City(id, x, y);
                cities.add(city);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        }

        return cities;
    }

    public static ArrayList<City> twoOptAlgorithm(ArrayList<City> cities, City startCity) {
        // Remove the starting city from the list
        cities.remove(startCity);

        ArrayList<City> bestRoute = new ArrayList<>(cities);
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 1; i < cities.size() - 1; i++) {
                for (int j = i + 1; j < cities.size(); j++) {
                    ArrayList<City> newRoute = twoOptSwap(bestRoute, i, j);
                    double bestDistance = calculateDistance(bestRoute, startCity);
                    double newDistance = calculateDistance(newRoute, startCity);
                    if (newDistance < bestDistance) {
                        bestRoute = newRoute;
                        improved = true;
                    }
                }
            }
        }

        // Add the starting city to the beginning and end of the route
        bestRoute.add(0, startCity);
        bestRoute.add(startCity);

        return bestRoute;
    }

    public static ArrayList<City> twoOptSwap(ArrayList<City> route, int i, int j) {
        ArrayList<City> newRoute = new ArrayList<>();
        for (int k = 0; k <= i - 1; k++) {
            newRoute.add(route.get(k));
        }
        for (int k = j; k >= i; k--) {
            newRoute.add(route.get(k));
        }
        for (int k = j + 1; k < route.size(); k++) {
            newRoute.add(route.get(k));
        }
        return newRoute;
    }


    public static double calculateDistance(ArrayList<City> route, City startCity) {
        double totalDistance = 0;
        City prevCity = startCity;
        for (City city : route) {
            double xDistance = Math.abs(prevCity.x - city.x);
            double yDistance = Math.abs(prevCity.y - city.y);
            double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
            totalDistance += distance;
            prevCity = city;
        }
        double xDistance = Math.abs(prevCity.x - startCity.x);
        double yDistance = Math.abs(prevCity.y - startCity.y);
        double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
        totalDistance += distance;
        return totalDistance;
    }
    
    public static void printShortestRouteToFile(ArrayList<City> shortestRoute, String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            for (City city : shortestRoute) {
                writer.write(city.id + "," + city.x + "," + city.y + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error printing shortest route to file: " + filename);
        }
    }


}

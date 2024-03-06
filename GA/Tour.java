
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tour {

    // Holds our tour of cities
    private ArrayList tour = new ArrayList<City>();
    // Cache
    private double fitness = 0;
    private int distance = 0;

    // Constructs a blank tour
    public Tour() {
        for (int i = 0; i < TourManager.numberOfCities(); i++) {
            tour.add(null);
        }
    }

    public Tour(ArrayList tour) {
        this.tour = tour;
    }

    public void generateIndividual() {
        // Set the first city as the starting and ending point of the tour
        setCity(0, TourManager.getCity(0));
        // setCity(TourManager.numberOfCities() - 1, TourManager.getCity(0));

        // Create a list of the remaining cities
        List<City> remainingCities = new ArrayList<>();
        for (int cityIndex = 1; cityIndex < TourManager.numberOfCities(); cityIndex++) {
            remainingCities.add(TourManager.getCity(cityIndex));
        }

        // Shuffle the list of remaining cities
        Collections.shuffle(remainingCities);

        // Add the shuffled cities to the tour
        for (int cityIndex = 1; cityIndex < TourManager.numberOfCities(); cityIndex++) {
            setCity(cityIndex, remainingCities.get(cityIndex - 1));
        }
    }

    // Gets a city from the tour
    public City getCity(int tourPosition) {
        return (City) tour.get(tourPosition);
    }

    // Sets a city in a certain position within a tour
    public void setCity(int tourPosition, City city) {
        tour.set(tourPosition, city);
        // If the tours been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
    }

    // Gets the tours fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1 / (double) getDistance();
        }
        return fitness;
    }

    // Gets the total distance of the tour
    public int getDistance() {
        if (distance == 0) {
            int tourDistance = 0;
            // Loop through our tour's cities
            for (int cityIndex = 0; cityIndex < tourSize(); cityIndex++) {
                // Get city we're travelling from
                City fromCity = getCity(cityIndex);
                // City we're travelling to
                City destinationCity;
                // Check we're not on our tour's last city, if we are set our
                // tour's final destination city to our starting city
                if (cityIndex + 1 < tourSize()) {
                    destinationCity = getCity(cityIndex + 1);
                } else {
                    destinationCity = getCity(0);
                }
                // Get the distance between the two cities
                tourDistance += fromCity.distanceTo(destinationCity);
            }
            // Add the distance from the last city to the first city
            tourDistance += getCity(tourSize() - 1).distanceTo(getCity(0));
            distance = tourDistance;
        }
        return distance;
    }

    // Get number of cities on our tour
    public int tourSize() {
        return tour.size();
    }

    // Check if the tour contains a city
    public boolean containsCity(City city) {
        return tour.contains(city);
    }

    @Override
    public String toString() {
        String geneString = "";
        // Add the first city's X and Y coordinates to the gene string
        geneString += getCity(0).getid() + " ";
        // Loop through the remaining cities and add their X and Y coordinates to the
        // gene string
        for (int i = 1; i < tourSize() - 1; i++) {
            geneString += getCity(i).getid() + " ";
        }
        // Add the first city's X and Y coordinates again to the end of the gene string
        geneString += getCity(0).getid() + " ";
        return geneString;
    }


    public City[] getCities() {
        return null;
    }

}
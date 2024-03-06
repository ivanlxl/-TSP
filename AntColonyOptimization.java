import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

/*
 * default
 * private double c = 1.0;             //number of trails
 * private double alpha = 1;           //pheromone importance
 * private double beta = 5;            //distance priority
 * private double evaporation = 0.5;
 * private double Q = 500;             //pheromone left on trail per ant
 * private double antFactor = 0.8;     //no of ants per node
 * private double randomFactor = 0.01; //introducing randomness
 * private int maxIterations = 1000;
 */

public class AntColonyOptimization
{
    private final double noOfTrials;             //number of trails
    private final double alpha;           //pheromone importance
    private final double beta;            //distance priority
    private final double evaporationRate;     // Evaporation rate between 0 and 1. Higher Value means evaporate faster
    private final double remainingPheromone;             //pheromone left on trail per ant
    private final double randomFactor; //introducing randomness

    private final int maxIterations;

    private static ArrayList<Point2D> cities; // Array list to hold coordinates of cities

    private final int numberOfCities;
    private final int numberOfAnts;
    private final double[][] distanceMatrix;
    private final double[][] trails;
    private final List<Ant> ants = new ArrayList<>();
    private final Random random = new Random();
    private final double[] probabilities;

    private int currentIndex;

    private int[] bestTourOrder;
    private double bestTourLength;

    public AntColonyOptimization(double noOfTrials, double alpha, double beta, double evaporationRate,
                                 double remainingPheromone, double antFactor, double randomFactor, int maxIterations, int noOfCities)
    {
        this.noOfTrials=noOfTrials;
        this.alpha=alpha;
        this.beta=beta;
        this.evaporationRate=evaporationRate;
        this.remainingPheromone=remainingPheromone;
        this.randomFactor=randomFactor;
        this.maxIterations=maxIterations;
        this.numberOfCities = noOfCities;

        distanceMatrix = generateDistanceMatrix(numberOfCities);
        numberOfAnts = (int) (numberOfCities * antFactor);

        trails = new double[numberOfCities][numberOfCities];
        probabilities = new double[numberOfCities];

        for(int i=0;i<numberOfAnts;i++)
            ants.add(new Ant(numberOfCities));
    }

    /**
     * Generate initial solution
     */
    public double[][] generateDistanceMatrix(int n) {
        double[][] randomMatrix = new double[n][n];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                if(i==j)
                    randomMatrix[i][j]=0;
                else
                    randomMatrix[i][j] = distance(cities.get(i), cities.get(j));
            }
        }

        return randomMatrix;
    }

    /**
     *
     * Read Cities and Coordinates from file
     */
    public static void loadCities(String fileName) throws IOException {
        ArrayList<Point2D> result = new ArrayList<>();
        BufferedReader br = null;
        try {
            String currentLine;
            br = new BufferedReader(new FileReader(fileName));
            while((currentLine = br.readLine()) != null) {
                String[] tokens = currentLine.split(" ");
                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                Point2D point = new Point2D.Double(x,y);
                result.add(point);
            }
        } finally {
            if(br != null) {br.close();}
        }

        cities = result;
    }

    private static double distance(Point2D p1, Point2D p2) {
        double xDist = p1.getX() - p2.getX();
        double yDist = p1.getY() - p2.getY();
        return Math.sqrt((xDist * xDist) + (yDist * yDist));
    }

    /**
     * Perform ant optimization
     */
    public void startAntOptimization(int attemptNumber) {
        long startTime = System.nanoTime();

        // Run Main Algorithm
        for(int i=1;i<=attemptNumber;i++)
        {
            solve(); // Run the main Algorithm
        }

        long endTime = System.nanoTime();
        System.out.println("Tour length: " + (bestTourLength - numberOfCities));
        System.out.println("Tour order: " + Arrays.toString(bestTourOrder));
        double totalTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Time Taken: " + totalTimeSeconds);
        System.out.println("Time Taken per iteration: " + totalTimeSeconds/100);


        // Try-Catch Block to Write best route to text file
        try {
            // Save file path
            FileWriter file = new FileWriter("C:\\Users\\ivanl\\OneDrive\\Desktop\\UNM\\Year 2\\2023 Spring Sem\\2039 AIM\\Coursework\\AntColony.txt");

            // Loop through array and write city order and coordinates
            for(int i=0 ; i<numberOfCities+1 ; i++) {
                double xCoordinate = cities.get(bestTourOrder[i]).getX();
                double yCoordinate = cities.get(bestTourOrder[i]).getY();
                file.write((bestTourOrder[i]+1) + "," + xCoordinate + "," + yCoordinate + "\n");
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Use this method to run the main logic
     */
    public void solve() {
        setupAnts();
        clearTrails();
        for(int i=0;i<maxIterations;i++)
        {
            moveAnts();
            updateTrails();
            updateBest();
        }
        bestTourOrder[numberOfCities] = bestTourOrder[0];
        bestTourOrder.clone();
    }

    /**
     * Prepare ants for the simulation
     */
    private void setupAnts() {
        for(int i=0;i<numberOfAnts;i++)
        {
            for(Ant ant:ants)
            {
                ant.clear();
                ant.visitCity(-1, random.nextInt(numberOfCities));
            }
        }
        currentIndex = 0;
    }

    /**
     * At each iteration, move ants
     */
    private void moveAnts() {
        for(int i=currentIndex;i<numberOfCities-1;i++)
        {
            for(Ant ant:ants)
            {
                ant.visitCity(currentIndex,selectNextCity(ant));
            }
            currentIndex++;
        }
    }

    /**
     * Select next city for each ant
     */
    private int selectNextCity(Ant ant) {
        int t = random.nextInt(numberOfCities - currentIndex);
        if (random.nextDouble() < randomFactor) // Randomly execute if statement
        {
            int cityIndex=-999;
            for(int i=0;i<numberOfCities;i++)
            {
                if(i==t && !ant.visited(i))
                {
                    cityIndex=i;
                    break;
                }
            }
            if(cityIndex!=-999)
                return cityIndex;
        }
        calculateProbabilities(ant);
        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < numberOfCities; i++)
        {
            total += probabilities[i];
            if (total >= r)
                return i;
        }

        //System.out.println(cityIndex);
        throw new RuntimeException("There are no other cities");
    }

    /**
     * Calculate the next city picks probabilities
     */
    public void calculateProbabilities(Ant ant) {
        int i = ant.trail[currentIndex];
        double pheromone = 0.0;
        for (int l = 0; l < numberOfCities; l++)
        {
            if (!ant.visited(l))
                pheromone += Math.pow(trails[i][l], alpha) * Math.pow(1.0 / distanceMatrix[i][l], beta);
        }
        for (int j = 0; j < numberOfCities; j++)
        {
            if (ant.visited(j))
                probabilities[j] = 0.0;
            else
            {
                double numerator = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / distanceMatrix[i][j], beta);
                probabilities[j] = numerator / pheromone;
            }
        }
    }

    /**
     * Update trails that ants used
     */
    private void updateTrails() {
        for (int i = 0; i < numberOfCities; i++)
        {
            for (int j = 0; j < numberOfCities; j++)
                trails[i][j] *= evaporationRate;
        }
        for (Ant a : ants)
        {
            double contribution = remainingPheromone / a.trailLength(distanceMatrix);
            for (int i = 0; i < numberOfCities - 1; i++)
                trails[a.trail[i]][a.trail[i + 1]] += contribution;
            trails[a.trail[numberOfCities - 1]][a.trail[0]] += contribution;
        }
    }

    /**
     * Update the best solution
     */
    private void updateBest() {
        if (bestTourOrder == null)
        {
            bestTourOrder = ants.get(0).trail;
            bestTourLength = ants.get(0).trailLength(distanceMatrix);
        }

        for (Ant a : ants)
        {
            if (a.trailLength(distanceMatrix) < bestTourLength)
            {
                bestTourLength = a.trailLength(distanceMatrix);
                bestTourOrder = a.trail.clone();
            }
        }
    }

    /**
     * Clear trails after simulation
     */
    private void clearTrails() {
        for(int i=0;i<numberOfCities;i++)
        {
            for(int j=0;j<numberOfCities;j++)
                trails[i][j]=noOfTrials;
        }
    }

    public static void main(String[] args) {
        String fileName = "C:\\Users\\ivanl\\OneDrive\\Desktop\\UNM\\Year 2\\2023 Spring Sem\\2039 AIM\\Coursework\\TSP_107.txt";

        try {
            loadCities(fileName); // Load city coordinates into ArrayList
            AntColonyOptimization solver = new AntColonyOptimization(1.0, 1, 5,0.8, 500, 0.8, 0.01, 1000, cities.size());

            solver.startAntOptimization(100);

        } catch(IOException e) {
            System.out.println("Error loading cities: " + e.getMessage());
        }
    }
}

public class City {
    int id;
    int x;
    int y;

    // Constructs a randomly placed city
    public City() {
        this.x = (int) (Math.random() * 16425);
        this.y = (int) (Math.random() * 11500);
    }

    // Constructs a city at chosen x, y location
    public City(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    // get city's id
    public int getid() {
        return id;
    }

    // Gets city's x coordinate
    public int getX() {
        return this.x;
    }

    // Gets city's y coordinate
    public int getY() {
        return this.y;
    }

    // Gets the distance to given city
    public double distanceTo(City city) {
        int xDistance = Math.abs(getX() - city.getX());
        int yDistance = Math.abs(getY() - city.getY());
        double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

        return distance;
    }

    @Override
    public String toString() {
        return getX() + ", " + getY();
    }
}
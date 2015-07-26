package sdc.avoidingproblems.jung;

/**
 *
 * @author Vitor Enes (vitorenesduarte ~at~ gmail ~dot~ com)
 */
public class Position {

    private double x;
    private double y;
    private int level;
    private boolean occupied;

    public Position(double x, double y, boolean occupied) {
        this.x = x;
        this.y = y;
        this.level = 0;
        this.occupied = occupied;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + ", occ : " + occupied + "}";
    }

}

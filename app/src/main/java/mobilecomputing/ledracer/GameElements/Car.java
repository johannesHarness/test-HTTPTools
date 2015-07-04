package mobilecomputing.ledracer.GameElements;

/**
 * Created by Johannes on 02.03.2015.
 */
public class Car {

    public static final byte COLOR = (byte)255;
    public static final byte[] IMG = {Car.COLOR, Car.COLOR, Car.COLOR, Car.COLOR, Car.COLOR, Car.COLOR};
    public static int HEIGHT() { return 3; }
    public static int WIDTH() { return IMG.length / HEIGHT(); }

    //top, left edge
    private int left = 0;
    private int top = 0;

    public Car() { }

    public int getLeft() { return left; }
    public int getTop() { return top; }

    public void setLeft(int left) { this.left = left; }
    public void setRight(int right) { this.left = right - Car.WIDTH(); }
    public void setTop(int top) { this.top = top; }
    public void setBot(int bot) { this.top = bot - Car.HEIGHT(); }

    public void changeX(int dX, int minX, int maxX) {
        this.left = Math.max(minX, Math.min(this.left + dX, maxX - Car.WIDTH()));
    }
    public void changeY(int dY, int minY, int maxY) {
        this.top = Math.max(minY, Math.min(this.top + dY, maxY - Car.HEIGHT()));
    }
}

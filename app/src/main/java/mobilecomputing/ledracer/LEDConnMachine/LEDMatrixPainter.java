package mobilecomputing.ledracer.LEDConnMachine;

/**
 * Created by Johannes on 01.03.2015.
 */
public interface LEDMatrixPainter {
    void update(byte[] image, int width, int height, long updateCount);
}

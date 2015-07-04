package mobilecomputing.ledracer.LEDConnMachine;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by Johannes on 01.03.2015.
 */
public class LEDMatrixRemote {

    private LEDMatrixBTConn conn;
    private int sendDelay; //in ms
    private int minDelay = 100; //in ms
    private volatile boolean expired = true;

    private byte[] image;

    private LEDMatrixPainter painter = null;
    private boolean usePainter = false;


    public LEDMatrixRemote(LEDMatrixBTConn conn) {
        this.conn = conn;
        this.image = new byte[this.getWidth() * this.getHeight()];
    }

    public LEDMatrixRemote(LEDMatrixBTConn conn, int minDelay) {
        this.conn = conn;
        this.image = new byte[this.getWidth() * this.getHeight()];
        this.minDelay = Math.max(1, minDelay);
    }

    public boolean isUsingPainter() { return this.usePainter; }

    public void setUsingPainter(boolean val) {
        this.usePainter = this.painter == null ? false : val;
    }

    public void setPainter(LEDMatrixPainter painter) {
        if(painter == null) throw new NullPointerException("Undefined painter!");

        synchronized (this.image) {
            this.painter = painter;
        }
    }

    public boolean isExpired() { return this.expired; }

    public int getWidth() { return this.conn.getXSize(); }

    public int getHeight() { return this.conn.getYSize(); }


    public boolean startConnection() {
        if(!this.isExpired()) return true;
        else {
            this.expired = !this.conn.connect();
            if(!this.isExpired()) {
                this.sendDelay = Math.max(minDelay, 1000 / Math.max(1, conn.getMaxFPS()));
                this.clearImage();

                this.startSending();
            }
        }

        return !this.expired;
    }

    public void stopConnection() {
        this.expired = true;
    }

    private void startSending() {
        Thread sender = new Thread() {
            private long updateCount = 0;

            public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                while (!isExpired()) {

                    synchronized (image) {
                        if(usePainter) painter.update(image, getWidth(), getHeight(), this.updateCount);
                        if (!conn.write(image)) expired = true;
                    }

                    try {
                        Thread.sleep(sendDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    this.updateCount = (this.updateCount + 1) % (Integer.MAX_VALUE);
                }

                conn.closeConnection();
            }
        };

        sender.start();
    }

    public void clearImage() {
        if(expired) return;

        synchronized (this.image) {
            Arrays.fill(image, (byte) 0);
        }
    }

    public void setImage(byte[] img) {
        if(expired) return;

        if(img == null) throw new NullPointerException("Undefined image");
        if(img.length != this.image.length) throw new ArrayIndexOutOfBoundsException("Wrong image dimensions!");

        synchronized (this.image) {
            for (int i = 0; i < this.image.length; i++) this.image[i] = img[i];
        }

    }

    public int convertCoordinates(int y, int x) {
        if(y < 0 || y > this.getHeight() - 1) throw new ArrayIndexOutOfBoundsException("Wrong y-coordinate: 0 <= y <= height-1");
        if(x < 0 || x > this.getWidth() - 1) throw new ArrayIndexOutOfBoundsException("Wrong x-coordinate: 0 <= x <= width - 1");

        return (y * this.getWidth()) + x;
    }

    public void setPixel(int x, int y, byte value) {
        this.setPixel(this.convertCoordinates(x, y), value);
    }

    public void setPixel(int pos, byte value) {
        if(expired) return;
        if(0 > pos || pos > this.image.length - 1) throw new ArrayIndexOutOfBoundsException("Wrong position: 0 <= pos <= (height * width) - 1");

        synchronized (this.image) {
            this.image[pos] = value;
        }
    }
}

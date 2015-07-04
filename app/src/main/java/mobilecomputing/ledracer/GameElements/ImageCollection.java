package mobilecomputing.ledracer.GameElements;

import java.util.Arrays;

/**
 * Created by Johannes on 03.03.2015.
 */
public class ImageCollection {

    private static void SET_COLOR(byte[] img, byte color) {
        for(int i = 0; i < img.length; i++) img[i] *= color;
    }


    //digits, nums ------------------------------------------------------------------------------------------------------------------------------------------------------
    public static final int DIGIT_WIDTH = 3;
    public static final int DIGIT_HEIGHT = 5;

    public static final byte[] ZERO = {(byte)1,(byte)1,(byte)1, (byte)1,(byte)0,(byte)1, (byte)1,(byte)0,(byte)1, (byte)1,(byte)0,(byte)1, (byte)1,(byte)1,(byte)1};
    public static final byte[] ONE = {(byte)0,(byte)0,(byte)1, (byte)0,(byte)1,(byte)1, (byte)0,(byte)0,(byte)1, (byte)0,(byte)0,(byte)1, (byte)0,(byte)0,(byte)1};
    public static final byte[] TWO = {(byte)1,(byte)1,(byte)1, (byte)0,(byte)0,(byte)1, (byte)0,(byte)1,(byte)0, (byte)1,(byte)0,(byte)0, (byte)1,(byte)1,(byte)1};
    public static final byte[] THREE = {(byte)1,(byte)1,(byte)1, (byte)0,(byte)0,(byte)1, (byte)0,(byte)1,(byte)1, (byte)0,(byte)0,(byte)1, (byte)1,(byte)1,(byte)1};
    public static final byte[] FOUR = {(byte)1,(byte)0,(byte)1, (byte)1,(byte)0,(byte)1, (byte)1,(byte)1,(byte)1, (byte)0,(byte)0,(byte)1, (byte)0,(byte)0,(byte)1};
    public static final byte[] FIVE = {(byte)1,(byte)1,(byte)1, (byte)1,(byte)0,(byte)0, (byte)1,(byte)1,(byte)1, (byte)0,(byte)0,(byte)1, (byte)1,(byte)1,(byte)1};
    public static final byte[] SIX = {(byte)1,(byte)1,(byte)1, (byte)1,(byte)0,(byte)0, (byte)1,(byte)1,(byte)1, (byte)1,(byte)0,(byte)1, (byte)1,(byte)1,(byte)1};
    public static final byte[] SEVEN = {(byte)1,(byte)1,(byte)1, (byte)0,(byte)0,(byte)1, (byte)0,(byte)0,(byte)1, (byte)0,(byte)0,(byte)1, (byte)0,(byte)0,(byte)1};
    public static final byte[] EIGHT= {(byte)1,(byte)1,(byte)1, (byte)1,(byte)0,(byte)1, (byte)1,(byte)1,(byte)1, (byte)1,(byte)0,(byte)1, (byte)1,(byte)1,(byte)1};
    public static final byte[] NINE = {(byte)1,(byte)1,(byte)1, (byte)1,(byte)0,(byte)1, (byte)1,(byte)1,(byte)1, (byte)0,(byte)0,(byte)1, (byte)1,(byte)1,(byte)1};

    private static final byte[][] NUMS = { ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE };


    public static byte[] GET_DIGIT_IMG(int digit, byte color) {
        if(digit < 0 ||digit > 9) throw new IllegalArgumentException("0 <= number <= 9!!");

        byte[] res = Arrays.copyOf(NUMS[digit], NUMS[digit].length);
        SET_COLOR(res, color);

        return res;
    }

    public static int CALC_LENGTH(int number) {
        int l = 0;

        do {
            l++;
            number /= 10;
        } while(number != 0);

        return l;
    }


    //Text: SCORE -------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static final int SCORE_WIDTH = 15 + 5 + 1;//5 chars + 5 spaces + double point
    public static final int SCORE_HEIGHT = 5;

    private static final byte[] SCORE = {(byte)1,(byte)1,(byte)1,(byte)0,(byte)1,(byte)1,(byte)1,(byte)0,(byte)1,(byte)1,(byte)1,(byte)0,(byte)1,(byte)1,(byte)1,(byte)0,(byte)1,(byte)1,(byte)1,(byte)0,(byte)0
                                        ,(byte)1,(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)1,(byte)0,(byte)1,(byte)0,(byte)1,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)1
                                        ,(byte)1,(byte)1,(byte)1,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)1,(byte)0,(byte)1,(byte)1,(byte)1,(byte)0,(byte)1,(byte)1,(byte)1,(byte)0,(byte)0
                                        ,(byte)0,(byte)0,(byte)1,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)1,(byte)0,(byte)1,(byte)1,(byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)1
                                        ,(byte)1,(byte)1,(byte)1,(byte)0,(byte)1,(byte)1,(byte)1,(byte)0,(byte)1,(byte)1,(byte)1,(byte)0,(byte)1,(byte)0,(byte)1,(byte)0,(byte)1,(byte)1,(byte)1,(byte)0,(byte)0};

    public static final byte[] GET_SCORE_IMAGE(byte color) {
        byte[] res = Arrays.copyOf(SCORE, SCORE.length);
        SET_COLOR(res, color);

        return res;
    }

    private ImageCollection() { }
}

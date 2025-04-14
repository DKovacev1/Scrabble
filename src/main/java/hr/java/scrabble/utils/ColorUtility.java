package hr.java.scrabble.utils;

import javafx.scene.paint.Color;

public class ColorUtility {

    private ColorUtility(){}

    public static String getHex(Color color) {
        // Multiply by 255 and round to get the integer representation of the color components
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        int a = (int) Math.round(color.getOpacity() * 255); // Calculate alpha component

        // Format the RGBA values as hexadecimal and concatenate them
        return String.format("#%02x%02x%02x%02x", r, g, b, a);
    }

}

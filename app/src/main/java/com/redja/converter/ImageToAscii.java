package com.redja.converter;

public class ImageToAscii {
    private ImageProcessor imageProcessor;
    private static final String ASCII_CHARS = "`^\",:;Il!i~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$";

    public ImageToAscii(String imageFullPath) {
        imageProcessor = new ImageProcessor(imageFullPath);
    }

    public void printAsciiMatrix() {
        char[][] pixels = setAsciiMatrix(imageProcessor.getBrightnessMatrix());
        for (int i=0; i<pixels.length; i++) {
            for (int j=0; j<pixels[0].length; j++) {
                System.out.println(pixels[i][j]);
            }

            System.out.println();
        }
    }

    private static char[][] setAsciiMatrix(int[][] brightnessMatrix) {
        char[][] asciiMatrix = new char[brightnessMatrix.length][];
        int rowCount = 0;
        for (int[] row : brightnessMatrix) {
            char[] asciiRow = new char[row.length];
            int cellCount = 0;
            for (int cell : row) {
                asciiRow[cellCount] = convertToAscii(cell);
                cellCount++;
            }
        }

        return asciiMatrix;
    }

    private static char convertToAscii(int value) {
        char asciiValue;
        int asciiIndex;
        // Map the brightness value to an index in the ASCII characters string
        asciiIndex = (int) ((ASCII_CHARS.length() - 1) * (value / 255.0));
        // Get the ASCII character from the string using the index
        asciiValue = ASCII_CHARS.charAt(asciiIndex);
        return asciiValue;
    }
}

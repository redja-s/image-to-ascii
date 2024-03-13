package com.redja.converter;

import com.redja.ImageType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;

import static java.util.Objects.requireNonNull;

public class ImageProcessor {
    private ImageType imageType;
    private String imagePath;
    public ImageProcessor(String imageFullPath) {
        this.imagePath = imageFullPath;
        this.imageType = new ImageType(this.imagePath);
    }

    public int[][] getBrightnessMatrix() {
        BufferedImage image = getImage();
        return getPixelsFromBufferedImage(image);
    }

    /**
     * Get the buffered image found at `imagePath`
     * @return a BufferedImage object
     */
    private BufferedImage getImage() {
        requireNonNull(this.imagePath);
        BufferedImage bufferedImage = null;
        try {
            if (!this.imageType.isFileValidType()) {
                return null;
            }
            bufferedImage = ImageIO.read(new File(this.imagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    /**
     * Gets the brightness matrix from a buffered image
     * @param bufferedImage the BufferedImage object to retrieve pixels for
     * @return a 2D array of averaged pixels
     */
    private int[][] getPixelsFromBufferedImage(BufferedImage bufferedImage) {
        requireNonNull(bufferedImage);

        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();
        int[][] pixels = new int[h][w];
        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {
                int currentPixel = bufferedImage.getRGB(i, j);

                //  A pixel is a combination of 3 colour components (RGB)
                // The intensity of each colour is stored as an 8-bit value
                // The range therefore is between 0-255
                int r = (currentPixel>>16) & 0xff;
                int g = (currentPixel>>8) & 0xff;
                int b = (currentPixel) & 0xff;
                pixels[j][i] = (r+g+b) / 3;
            }
        }

        return pixels;
    }
}

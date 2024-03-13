package com.redja.converter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import static java.util.Objects.requireNonNull;

public class ImageToAscii {
    private static final String ASCII_CHARS = "`^\",:;Il!i~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$";
    private static final String IMAGE_EXT_JPG = "jpg";
    private static final String IMAGE_EXT_JPEG = "jpeg";
    private static final String IMAGE_EXT_PNG = "png";
    private static final String IMAGE_EXT_GIF = "gif";

    private String imageFullPath;
    private String imageOutputPath;

    public void start() throws IOException {
        if (imageOutputPath == null || imageOutputPath.isBlank()) {
            imageOutputPath = "ascii" + Instant.now().getEpochSecond() + ".txt";
        }

        final BufferedImage bufferedImage = getImage();
        final Path output = Paths.get(imageOutputPath);
        Files.createFile(output);

        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        int scale = 10;
        int newWidth = width / scale;
        int newHeight = height / scale;
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.getGraphics().drawImage(bufferedImage, 0, 0, newWidth, newHeight, null);


        for (int i=0; i<newHeight; i++) {
            StringBuilder line = new StringBuilder();
            for (int j=0; j<newWidth; j++) {
                int rgb = resizedImage.getRGB(j,i);
                int brightness = calculateBrightness(rgb);
                int asciiIndex = (int) ((ASCII_CHARS.length() - 1) * (brightness / 255.0));
                char asciiValue = ASCII_CHARS.charAt(asciiIndex);

                line.append(asciiValue);
            }

            line.append(System.lineSeparator());
            Files.write(output, line.toString().getBytes(), StandardOpenOption.APPEND);
        }
    }

    public ImageToAscii(String imageFullPath, String imageOutputPath) {
        this.imageFullPath = imageFullPath;
        this.imageOutputPath = imageOutputPath;
    }

    /**
     * Checks that a given file is a supported type
     * @param path the Path object to the file
     * @return whether the file is a supported type
     */
    public boolean isFileValidType(Path path) {
        final String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf(".");
        String ext = fileName.substring(dotIndex + 1).toLowerCase();
        return ext.equalsIgnoreCase(IMAGE_EXT_GIF) ||
                ext.equalsIgnoreCase(IMAGE_EXT_JPEG) ||
                ext.equalsIgnoreCase(IMAGE_EXT_PNG) ||
                ext.equalsIgnoreCase(IMAGE_EXT_JPG);
    }

    /**
     * Returns the average of the given RGB value
     * @param rgb The integer-value for the pixel
     * @return The average of the given rgb value
     */
    private int calculateBrightness(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb) & 0xFF;

        return (r + g + b) / 3;
    }

    /**
     * Get the buffered image found at `imagePath`
     * @return a BufferedImage object
     */
    private BufferedImage getImage() {
        Path path = Paths.get(imageFullPath);
        if (!Files.isRegularFile(path) || !Files.exists(path)) {
            throw new RuntimeException("The given file must be valid");
        }

        if (!isFileValidType(path)) {
            throw new RuntimeException("The given file must be one of the following types: .jpg .jpeg .png .gif");
        }

        try {
            return ImageIO.read(Files.newInputStream(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

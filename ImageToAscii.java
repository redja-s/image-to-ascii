import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class ImageToAscii {
    private static final String ASCII_CHARS = "`^\",:;Il!i~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$";
    private static final String IMAGE_EXT_JPG = "jpg";
    private static final String IMAGE_EXT_JPEG = "jpeg";
    private static final String IMAGE_EXT_PNG = "png";
    private static final String IMAGE_EXT_GIF = "gif";
    private static final int DEFAULT_WIDTH = 150;
    private static final int DEFAULT_HEIGHT = 150;

    private final String imageFullPath;
    private String imageOutputPath;

    public static void main(String[] args) throws IOException {
        ImageToAscii imageToAscii;
        if (args.length == 0) {
            throw new RuntimeException("Please provide a file");
        } else if (args.length == 1) {
            imageToAscii = new ImageToAscii(args[0], null);
        } else {
            imageToAscii = new ImageToAscii(args[0], args[1]);
        }

        imageToAscii.start();
    }

    public ImageToAscii(String imageFullPath, String imageOutputPath) {
        this.imageFullPath = imageFullPath;
        this.imageOutputPath = imageOutputPath;
    }

    public void start() throws IOException {
        Path path = Paths.get(imageFullPath);
        if (imageOutputPath == null || imageOutputPath.isBlank()) {
            int dotIndex = path.getFileName().toString().lastIndexOf(".");
            imageOutputPath = "output/" + path.getFileName().toString().substring(0, dotIndex) + "-" + Instant.now().getEpochSecond() + ".txt";
            System.out.println("No output path found. Outputting to " + imageOutputPath);
        }

        final BufferedImage bufferedImage = getImage();
        final Path output = Paths.get(imageOutputPath);
        Files.createFile(output);

        BufferedImage resizedImage = resize(bufferedImage, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        int newHeight = resizedImage.getHeight();
        int newWidth = resizedImage.getWidth();

        for (int i = 0; i < newHeight; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < newWidth; j++) {
                int rgb = resizedImage.getRGB(j, i);
                int brightness = calculateBrightness(rgb);
                int asciiIndex = (int) ((ASCII_CHARS.length() - 1) * (brightness / 255.0));
                char asciiValue = ASCII_CHARS.charAt(asciiIndex);

                line.append(asciiValue);
            }

            line.append(System.lineSeparator());
            Files.write(output, line.toString().getBytes(), StandardOpenOption.APPEND);
        }
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

    /**
     * Takes the original image and uses bi-linear interpolation to maintain image quality while re-sizing
     * @param original     The original BufferedImage
     * @param targetWidth  The required width
     * @param targetHeight The required height
     * @return The re-sized BufferedImage
     */
    private BufferedImage resize(BufferedImage original, int targetWidth, int targetHeight) {
        double aspectRatio = (double) original.getWidth() / original.getHeight();

        // Calculate new dimensions based on aspect ratio
        if (aspectRatio > 1) {
            targetHeight = (int) (targetWidth / aspectRatio);
        } else {
            targetWidth = (int) (targetHeight * aspectRatio);
        }

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}

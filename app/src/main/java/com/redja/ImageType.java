package com.redja;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class ImageType {
    private final String imageFullPath;
    private static final String IMAGE_EXT_JPG = "jpg";
    private static final String IMAGE_EXT_JPEG = "jpeg";
    private static final String IMAGE_EXT_PNG = "png";
    private static final String IMAGE_EXT_GIF = "gif";

    public ImageType(String imageFullPath) {
        this.imageFullPath = imageFullPath;
    }

    public boolean isFileValidType() {
        requireNonNull(imageFullPath);

        File image = new File(imageFullPath);
        String ext = image.getName().substring(imageFullPath.lastIndexOf(".") + 1).toLowerCase();

        return ext.equalsIgnoreCase(IMAGE_EXT_GIF) || ext.equalsIgnoreCase(IMAGE_EXT_JPEG) || ext.equalsIgnoreCase(IMAGE_EXT_PNG) || ext.equalsIgnoreCase(IMAGE_EXT_JPG);
    }
}

package com.redja;

import com.redja.converter.ImageToAscii;

import java.io.IOException;

public class Main {
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
}

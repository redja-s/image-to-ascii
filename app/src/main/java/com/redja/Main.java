package com.redja;

import com.redja.converter.ImageToAscii;

public class Main {
    public static void main(String[] args) {
        String imageFullPath = args[0];
        ImageToAscii asciiImage = new ImageToAscii(args[0]);
        asciiImage.printAsciiMatrix();
    }
}

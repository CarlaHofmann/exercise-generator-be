package com.frauas.exercisegenerator.converters;

import org.springframework.util.Base64Utils;

public class ImageBase64Converter {

    public static byte[] decodeToImage(String imageString) {
        String imageData = imageString.split("base64,")[1];
        return Base64Utils.decode(imageData.getBytes());
    }

    public static String encodeToString(byte[] imageBytes) {
        return Base64Utils.encodeToString(imageBytes);
    }
}

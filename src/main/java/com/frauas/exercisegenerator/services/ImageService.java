package com.frauas.exercisegenerator.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import com.frauas.exercisegenerator.converters.ImageBase64Converter;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class ImageService {
    private final String WORKING_IMAGE_DIR = System.getProperty("user.dir") + "/images/";

    public String saveImage(String imageString) throws IOException {
        LocalDateTime time = LocalDateTime.now();
        String formattedTime = time.toString().replace(":", "-");

        String uuid = UUID.randomUUID().toString();

        String imageType = getImageType(imageString);
        if (!(imageType.equals("png") || imageType.equals("jpeg") || imageType.equals("jpg"))) {
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
        }

        String imageUrl = WORKING_IMAGE_DIR + formattedTime + "-" + uuid + "." + imageType;
        File imageFile = new File(imageUrl);

        FileUtils.writeByteArrayToFile(imageFile, ImageBase64Converter.decodeToImage(imageString));

        return imageUrl;
    }

    public static String getImageType(String imageString) {
        return imageString.split("image/")[1]
                .split(";")[0];
    }

    public String getImage() throws IOException {
        // T0D0 : durch richtige Implementierung ersetzen

        File folder = new File(WORKING_IMAGE_DIR);

        File[] listOfImages = Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(file -> {
                    String[] fileParts = file.getName().split("\\.");
                    String fileType = fileParts[fileParts.length - 1];
                    return fileType.equals("png") || fileType.equals("jpeg") || fileType.equals("jpg");
                }).toArray(File[]::new);

        int random = (int) (Math.random() * (listOfImages.length - 1));
        File randomImage = listOfImages[random];
        String[] imageParts = randomImage.getName().split("\\.");
        String fileType = imageParts[imageParts.length - 1];

        return "data:image/" + fileType + ";base64,"
                + ImageBase64Converter.encodeToString(FileUtils.readFileToByteArray(randomImage));
    }
}

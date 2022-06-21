package com.frauas.exercisegenerator.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.frauas.exercisegenerator.converters.ImageBase64Converter;
import com.frauas.exercisegenerator.documents.Image;
import com.frauas.exercisegenerator.repositories.ImageRepository;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    private final String WORKING_IMAGE_DIR = System.getProperty("user.dir") + "/images/";

    public Image saveImage(String imageString, String reference) throws IOException {
        LocalDateTime time = LocalDateTime.now();
        String formattedTime = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));

        String uuid = UUID.randomUUID().toString();

        String imageType = getImageType(imageString);

        if (!(imageType.equals("png") || imageType.equals("jpeg") || imageType.equals("jpg"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String imagePath = WORKING_IMAGE_DIR + formattedTime + "_" + uuid + "." + imageType;
        File imageFile = new File(imagePath);

        FileUtils.writeByteArrayToFile(imageFile, ImageBase64Converter.decodeToImage(imageString));

        Image image = Image.builder()
                .reference(reference)
                .filepath(imageFile.getAbsolutePath())
                .build();

        return imageRepository.save(image);
    }

    public static String getImageType(String imageString) {
        return imageString.split("image/")[1]
                .split(";")[0];
    }
}

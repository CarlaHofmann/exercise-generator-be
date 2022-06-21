package com.frauas.exercisegenerator.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.frauas.exercisegenerator.converters.ImageBase64Converter;
import com.frauas.exercisegenerator.documents.Image;
import com.frauas.exercisegenerator.dtos.ImageDto;

@Service
public class ImageService {

    private final String WORKING_IMAGE_DIR = System.getProperty("user.dir") + "/images/";

    public Image saveImage(ImageDto imageDto) throws IOException {
        LocalDateTime time = LocalDateTime.now();
        String formattedTime = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));

        String uuid = UUID.randomUUID().toString();

        String imageType = getImageType(imageDto.getContent());

        if (!(imageType.equals("png") || imageType.equals("jpeg") || imageType.equals("jpg"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String imagePath = WORKING_IMAGE_DIR + formattedTime + "_" + uuid + "." + imageType;
        File imageFile = new File(imagePath);

        FileUtils.writeByteArrayToFile(imageFile, ImageBase64Converter.decodeToImage(imageDto.getContent()));

        Image image = Image.builder()
                .reference(imageDto.getReference())
                .filepath(imageFile.getAbsolutePath())
                .build();

        return image;
    }

    public static String getImageType(String imageString) {
        return imageString.split("image/")[1]
                .split(";")[0];
    }
}

package com.frauas.exercisegenerator.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.frauas.exercisegenerator.converters.ImageBase64Converter;
import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Image;
import com.frauas.exercisegenerator.dtos.ImageDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                .content(imageDto.getContent())
                .build();

        return image;
    }

    public void deleteImageFile(Image image) {
        File imageFile = new File(image.getFilepath());

        imageFile.delete();
    }

    public String getImageContent(Image image) throws IOException {
        File imageFile = new File(image.getFilepath());

        String[] imageParts = imageFile.getName().split("\\.");
        String fileType = imageParts[imageParts.length - 1];

        return "data:image/" + fileType + ";base64,"
                + ImageBase64Converter.encodeToString(FileUtils.readFileToByteArray(imageFile));
    }

    public void hydrateExerciseImageContent(Exercise exercise) {
        List<Image> images = exercise.getImages();

        if (images != null) {
            images.forEach(image -> {
                try {
                    image.setContent(getImageContent(image));
                } catch (IOException e) {
                    log.warn("Error during image content hydration for exercise", e);
                }
            });
        }
    }

    public static String getImageType(String imageString) {
        return imageString.split("image/")[1]
                .split(";")[0];
    }
}

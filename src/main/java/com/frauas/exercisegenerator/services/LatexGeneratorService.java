package com.frauas.exercisegenerator.services;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Image;
import com.frauas.exercisegenerator.documents.Sheet;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.buildobjects.process.ExternalProcessFailureException;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.StartupException;
import org.buildobjects.process.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class LatexGeneratorService {

    @Autowired
    private Handlebars handlebars;

    private final String TMP_FOLDER = System.getProperty("user.dir") + "/tmp/";

    public byte[] createSheetPdf(Sheet sheet) {
        byte[] content;

        try {
            Template sheetTemplate = handlebars.compile("sheet-template");
            String fileContent = sheetTemplate.apply(sheet);
            fileContent = replaceImageReferences(fileContent, sheet);

            String filename = writeContentFile(fileContent);
            content = renderLatexPdfContent(filename);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (TimeoutException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Timeout during LaTeX compilation!");
        }

        return content;
    }

    public byte[] createExercisePdf(Exercise exercise) {
        byte[] content;

        try {
            Template exerciseTemplate = handlebars.compile("exercise-template");
            String fileContent = exerciseTemplate.apply(exercise);
            fileContent = replaceImageReferences(fileContent, exercise);

            String filename = writeContentFile(fileContent);
            content = renderLatexPdfContent(filename);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (TimeoutException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Timeout during LaTeX compilation!");
        }

        return content;
    }

    private byte[] renderLatexPdfContent(String filename)
            throws IOException, StartupException, TimeoutException {
        ProcBuilder command = new ProcBuilder("latexmk")
                .withWorkingDirectory(new File(TMP_FOLDER))
                .withArg("-interaction=nonstopmode")
                .withArg("-pdf")
                .withArg("-silent")
                .withArg(filename)
                .withTimeoutMillis(20 * 1000);

        // Execute latex compilation process
        try {
            command.run();
        } catch (ExternalProcessFailureException e) {
            System.out.println("Error during LaTeX compilation, but can probably be ignored.");
            System.out.println(e.getMessage());
        }

        // Read contents of created pdf file
        byte[] contents = Files.readAllBytes(Paths.get(TMP_FOLDER, filename.replace(".tex", ".pdf")));

        // cleanup fragments from latex compilation
        cleanupLatexFragments(filename);

        // Delete generated content file
        Files.delete(Paths.get(TMP_FOLDER, filename));

        return contents;
    }

    private String writeContentFile(String contentString) throws IOException {
        UUID uuid = UUID.randomUUID();
        String filename = uuid + ".tex";

        BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(TMP_FOLDER, filename),
                StandardCharsets.UTF_8);
        writer.write(contentString);
        writer.close();

        return filename;
    }

    private void cleanupLatexFragments(String filename)
            throws StartupException, TimeoutException, ExternalProcessFailureException {
        ProcBuilder command = new ProcBuilder("latexmk")
                .withWorkingDirectory(Paths.get(TMP_FOLDER).toFile())
                .withArg("-C")
                .withArg("-r")
                .withArg("../.latexmkrc")
                .withArg(filename);

        command.run();
    }

    private String replaceImageReferences(String latexContent, Sheet sheet) {
        List<Exercise> exercises = sheet.getExercises();

        for (Exercise exercise : exercises) {
            latexContent = replaceImageReferences(latexContent, exercise);
        }

        return latexContent;
    }

    private String replaceImageReferences(String latexContent, Exercise exercise) {
        List<Image> images = exercise.getImages();

        for (Image image : images) {
            latexContent = latexContent.replaceAll(image.getReference(), image.getFilepath());
        }

        return latexContent;
    }
}

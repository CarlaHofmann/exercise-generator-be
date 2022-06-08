package com.frauas.exercisegenerator.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.buildobjects.process.ExternalProcessFailureException;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.buildobjects.process.StartupException;
import org.buildobjects.process.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.frauas.exercisegenerator.documents.Exercise;
import com.frauas.exercisegenerator.documents.Sheet;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

@Service
public class LatexGeneratorService {

    @Autowired
    private Handlebars handlebars;

    private final String TMP_FOLDER = "./tmp/";

    public byte[] createSheetPdf(Sheet sheet) {
        byte[] content = null;

        try {
            Template sheetTemplate = handlebars.compile("sheet-template");
            String fileContent = sheetTemplate.apply(sheet);
            String filename = writeContentFile(fileContent);
            content = renderLatexPdfContent(filename);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (ExternalProcessFailureException e) {
            System.err.println(e);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error during LaTeX compilation (Exit value: " + e.getExitValue() + ")");
        } catch (TimeoutException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Timeout during LaTeX compilation!");
        }

        return content;
    }

    public byte[] createExercisePdf(Exercise exercise) {
        byte[] content = null;

        try {
            Template exerciseTemplate = handlebars.compile("exercise-template");
            String fileContent = exerciseTemplate.apply(exercise);
            String filename = writeContentFile(fileContent);
            content = renderLatexPdfContent(filename);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (ExternalProcessFailureException e) {
            System.err.println(e);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error during LaTeX compilation (Exit value: " + e.getExitValue() + ")");
        } catch (TimeoutException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Timeout during LaTeX compilation!");
        }

        return content;
    }

    private byte[] renderLatexPdfContent(String filename)
            throws IOException, StartupException, TimeoutException, ExternalProcessFailureException {
        ProcBuilder builder = new ProcBuilder("latexmk")
                .withWorkingDirectory(Paths.get(TMP_FOLDER).toFile())
                .withArg("-interaction=nonstopmode")
                .withArg("-pdf")
                .withArg(filename)
                .withTimeoutMillis(10000);

        // Execute latex compilation process
        builder.run();

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
        String filename = uuid.toString() + ".tex";

        BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(TMP_FOLDER, filename)
                .toAbsolutePath()
                .toString()));
        writer.write(contentString);
        writer.close();

        return filename;
    }

    private ProcResult cleanupLatexFragments(String filename)
            throws StartupException, TimeoutException, ExternalProcessFailureException {
        ProcBuilder builder = new ProcBuilder("latexmk")
                .withWorkingDirectory(Paths.get(TMP_FOLDER).toFile())
                .withArg("-C")
                .withArg("-r")
                .withArg("../.latexmkrc")
                .withArg(filename);

        return builder.run();

    }
}

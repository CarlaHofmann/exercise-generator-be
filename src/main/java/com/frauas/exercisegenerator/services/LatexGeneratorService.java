package com.frauas.exercisegenerator.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.frauas.exercisegenerator.documents.Sheet;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import org.buildobjects.process.ExternalProcessFailureException;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.buildobjects.process.StartupException;
import org.buildobjects.process.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            writeContentFile(fileContent);
            content = renderLatexPdfContent();
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

    private byte[] renderLatexPdfContent()
            throws IOException, StartupException, TimeoutException, ExternalProcessFailureException {
        ProcBuilder builder = new ProcBuilder("latexmk")
                .withWorkingDirectory(Paths.get(TMP_FOLDER).toFile())
                .withArg("-interaction=nonstopmode")
                .withArg("-pdf")
                .withArg("content.tex")
                .withTimeoutMillis(10000);

        // Execute latex compilation process
        builder.run();

        // Read contents of created pdf file
        byte[] contents = Files.readAllBytes(Paths.get(TMP_FOLDER, "content.pdf"));

        // cleanup fragments from latex compilation
        cleanupLatexFragments();

        return contents;
    }

    private void writeContentFile(String contentString) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter(
                        Paths.get(TMP_FOLDER, "content.tex")
                                .toAbsolutePath()
                                .toString()));
        writer.write(contentString);
        writer.close();
    }

    private ProcResult cleanupLatexFragments()
            throws StartupException, TimeoutException, ExternalProcessFailureException {
        ProcBuilder builder = new ProcBuilder("latexmk")
                .withWorkingDirectory(Paths.get(TMP_FOLDER).toFile())
                .withArg("-C")
                .withArg("-r")
                .withArg("../.latexmkrc")
                .withArg("content.tex");

        return builder.run();

    }
}

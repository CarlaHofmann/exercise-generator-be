package com.frauas.exercisegenerator.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.repositories.SheetRepository;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LatexGeneratorService {

    @Autowired
    private Handlebars handlebars;

    @Autowired
    private SheetRepository sheetRepository;

    private String baseFolder = "./templates/";

    public byte[] getLatexFile() throws Exception {
        Template beamer = handlebars.compile("beamer");
        List<Sheet> sheets = sheetRepository.findAll();
        String content = beamer.apply(sheets.get(0));
        System.out.println(content);
        writeContentFile(content);
        return renderLatexPdfContent();
    }

    public byte[] renderLatexPdfContent() throws Exception {
        // this.writeContentFile(contentString);

        ProcBuilder builder = new ProcBuilder("latexmk")
                .withWorkingDirectory(Paths.get(baseFolder).toFile())
                .withArg("-interaction=nonstopmode")
                .withArg("-pdf")
                .withArg("test.tex")
                .withTimeoutMillis(10000);

        ProcResult result = builder.run();

        if (result.getExitValue() != 0) {
            throw new Exception("Command execution failed!");
        }

        byte[] contents = Files.readAllBytes(Paths.get(baseFolder, "test.pdf"));

        this.cleanupLatexFragments();

        return contents;
    }

    public void writeContentFile(String contentString) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter(Paths.get(baseFolder, "test.tex").toAbsolutePath().toString()));
        writer.write(contentString);
        writer.close();
    }

    public ProcResult cleanupLatexFragments() {
        ProcBuilder builder = new ProcBuilder("latexmk")
                .withWorkingDirectory(Paths.get(baseFolder).toFile())
                .withArg("-C")
                .withArg("-r")
                .withArg("../.latexmkrc")
                .withArg("test.tex");

        return builder.run();

    }
}

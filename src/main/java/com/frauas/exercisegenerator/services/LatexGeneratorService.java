package com.frauas.exercisegenerator.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.springframework.stereotype.Service;

@Service
public class LatexGeneratorService {

    public byte[] renderLatexPdfContent(String contentString) throws Exception {
        this.writeContentFile(contentString);

        ProcBuilder builder = new ProcBuilder("latexmk")
                .withArg("-interaction=nonstopmode")
                .withArg("-pdf")
                .withArg("base.tex")
                .withTimeoutMillis(10000);

        ProcResult result = builder.run();

        if (result.getExitValue() != 0) {
            throw new Exception("Command execution failed!");
        }

        byte[] contents = Files.readAllBytes(Paths.get("base.pdf"));

        this.cleanupLatexFragments();

        return contents;
    }

    public void writeContentFile(String contentString) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("content.tex"));
        writer.write(contentString);
        writer.close();
    }

    public ProcResult cleanupLatexFragments() {
        ProcBuilder builder = new ProcBuilder("latexmk")
                .withArg("-c")
                .withArg("base.tex");

        return builder.run();

    }
}

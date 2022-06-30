package com.frauas.exercisegenerator.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.dtos.SheetDto;
import com.frauas.exercisegenerator.helpers.StringHelper;
import com.frauas.exercisegenerator.services.LatexGeneratorService;
import com.frauas.exercisegenerator.services.SheetService;

@RestController
@RequestMapping("/sheet")
public class SheetController {

    @Autowired
    private SheetService sheetService;

    @Autowired
    private LatexGeneratorService latexGeneratorService;

    @GetMapping
    public List<Sheet> getAllSheets() {
        return sheetService.getSheets();
    }

    @GetMapping("/{id}")
    public Sheet getSheetById(@PathVariable String id) {
        return sheetService.getSheetById(id);
    }

    @GetMapping(path = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getSheetPdf(@PathVariable String id) {
        Sheet sheet = sheetService.getSheetById(id);

        byte[] contents = latexGeneratorService.createSheetPdf(sheet);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = StringHelper.toSnakeCase(sheet.getTitle() + ".pdf");
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename(filename)
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }

    @PostMapping
    public Sheet createSheet(HttpServletRequest request, @RequestBody SheetDto createSheetDto) {
        return sheetService.createSheet(request, createSheetDto);
    }

    @PostMapping(path = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getSheetPdf(HttpServletRequest request, @RequestBody SheetDto sheetDto) {
        Sheet sheet = sheetService.prepareSheet(request, sheetDto);

        byte[] contents = latexGeneratorService.createSheetPdf(sheet);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = StringHelper.toSnakeCase(sheet.getTitle() + ".pdf");
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename(filename)
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }

    @PutMapping("/{id}")
    public Sheet updateSheet(@PathVariable String id, @RequestBody SheetDto createSheetDto) {
        return sheetService.updateSheetById(id, createSheetDto);
    }

    @DeleteMapping("/{id}")
    public void deleteSheet(@PathVariable String id) {
        sheetService.deleteSheetById(id);
    }
}

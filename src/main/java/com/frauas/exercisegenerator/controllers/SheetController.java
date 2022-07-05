package com.frauas.exercisegenerator.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.dtos.SheetDto;
import com.frauas.exercisegenerator.helpers.StringHelper;
import com.frauas.exercisegenerator.services.LatexGeneratorService;
import com.frauas.exercisegenerator.services.SheetService;

import com.frauas.exercisegenerator.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/sheet")
public class SheetController {

    @Autowired
    private SheetService sheetService;

    @Autowired
    TokenUtil tokenUtil;

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
    public ResponseEntity<byte[]> previewSheetDto(@RequestBody SheetDto sheetDto) {
        Sheet sheet = sheetService.prepareSheet(sheetDto);

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
    public Sheet updateSheet(HttpServletRequest request, HttpServletResponse response, @PathVariable String id, @RequestBody SheetDto createSheetDto) throws IOException
    {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        String givenUsername = tokenUtil.getUsernameFromToken(token);
        String actualUsername = sheetService.getSheetById(id).getAuthor().getUsername();

        if(givenUsername.equals(actualUsername) == false){
            response.sendError(UNAUTHORIZED.value());
        }

        return sheetService.updateSheetById(id, createSheetDto);
    }

    @DeleteMapping("/{id}")
    public void deleteSheet(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws IOException
    {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        String givenUsername = tokenUtil.getUsernameFromToken(token);
        String actualUsername = sheetService.getSheetById(id).getAuthor().getUsername();

        if(givenUsername.equals(actualUsername) == false){
            response.sendError(UNAUTHORIZED.value());
        }

        sheetService.deleteSheetById(id);
    }
}

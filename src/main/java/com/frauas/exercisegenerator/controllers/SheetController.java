package com.frauas.exercisegenerator.controllers;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.server.ResponseStatusException;

import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.dtos.SheetDto;
import com.frauas.exercisegenerator.helpers.StringHelper;
import com.frauas.exercisegenerator.services.LatexGeneratorService;
import com.frauas.exercisegenerator.services.SheetService;
import com.frauas.exercisegenerator.util.TokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
    @Operation(security = { @SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "") })
    public List<Sheet> getAllSheets(HttpServletRequest request) {
        List<Sheet> allSheets = sheetService.getSheets();
        List<Sheet> allowedSheets = new ArrayList<>();

        for (Sheet s : allSheets) {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring("Bearer ".length());
                tokenUtil.validateToken(token);

                String username = tokenUtil.getUsernameFromToken(token);

                if (username.equals(s.getAuthor().getUsername())) {
                    allowedSheets.add(s);
                }
            } else {
                if (s.getIsPublished()) {
                    allowedSheets.add(s);
                }
            }
        }

        return allowedSheets;
    }

    @GetMapping("/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "") })
    public Sheet getSheetById(HttpServletRequest request, @PathVariable String id) {
        Sheet sheet = sheetService.getSheetById(id);

        if (!sheet.getIsPublished()) {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring("Bearer ".length());
                tokenUtil.validateToken(token);

                String username = tokenUtil.getUsernameFromToken(token);

                if (!username.equals(sheet.getAuthor().getUsername())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Users may only see their own unpublished sheets");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Unauthorized users may not see unpublished sheets");
            }

        }

        return sheet;
    }

    @GetMapping(path = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(security = { @SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "") })
    public ResponseEntity<byte[]> getSheetPdf(HttpServletRequest request, @PathVariable String id) {
        Sheet sheet = sheetService.getSheetById(id);

        if (!sheet.getIsPublished()) {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Unauthorized users may not generate pdf files for unpublished sheets");
            }

            String token = authorizationHeader.substring("Bearer ".length());
            tokenUtil.validateToken(token);

            String username = tokenUtil.getUsernameFromToken(token);

            if (!username.equals(sheet.getAuthor().getUsername())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Users may only generate pdf files of unpublished sheets for their own exercises");
            }
        }

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
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
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
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public Sheet updateSheet(HttpServletRequest request, HttpServletResponse response, @PathVariable String id,
            @RequestBody SheetDto createSheetDto) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        String givenUsername = tokenUtil.getUsernameFromToken(token);
        String actualUsername = sheetService.getSheetById(id).getAuthor().getUsername();

        if (givenUsername.equals(actualUsername) == false) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Users may only update sheets which they've authored themselves");
        }

        return sheetService.updateSheetById(id, createSheetDto);
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public void deleteSheet(HttpServletRequest request, HttpServletResponse response, @PathVariable String id)
            throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());
        String givenUsername = tokenUtil.getUsernameFromToken(token);
        String actualUsername = sheetService.getSheetById(id).getAuthor().getUsername();

        if (givenUsername.equals(actualUsername) == false) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Users may only delete sheets which they've authored themselves");
        }

        sheetService.deleteSheetById(id);
    }
}

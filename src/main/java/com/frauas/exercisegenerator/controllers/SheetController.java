package com.frauas.exercisegenerator.controllers;

import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.dtos.CreateSheetDto;
import com.frauas.exercisegenerator.services.SheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/sheet")
public class SheetController {

    @Autowired
    private SheetService sheetService;

    @GetMapping
    public List<Sheet> getAllSheets() {
        return sheetService.getSheets();
    }

    @GetMapping("/{id}")
    public Sheet getSheetById(@PathVariable String id) {
        return sheetService.getSheetById(id);
    }

    @PostMapping
    public Sheet createSheet(HttpServletRequest request, @RequestBody CreateSheetDto createSheetDto) {
        return sheetService.createSheet(request, createSheetDto);
    }

    @DeleteMapping("/{id}")
    public void deleteSheet(@PathVariable String id) {
        sheetService.deleteSheetById(id);
    }
}

package com.frauas.exercisegenerator.controllers;

import java.util.List;
import java.util.Optional;

import com.frauas.exercisegenerator.documents.Sheet;
import com.frauas.exercisegenerator.dtos.CreateSheetDto;
import com.frauas.exercisegenerator.services.SheetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Optional<Sheet> getSheetById(@PathVariable String id) {
        return sheetService.getSheetById(id);
    }

    @PostMapping
    public Sheet createSheet(@RequestBody CreateSheetDto createSheetDto) {
        return sheetService.createSheet(createSheetDto);
    }

    @DeleteMapping("/{id}")
    public void deleteSheet(@PathVariable String id) {
        sheetService.deleteSheetById(id);
    }
}

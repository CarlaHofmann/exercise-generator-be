package com.frauas.exercisegenerator.services;

import org.openapitools.model.Book;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String test(){
        return "Hallo Welt";
    }

    public Book getBook(int bookNumber){
        return new Book().title("Die Nummer " + bookNumber)
                .subtitle("und das Geheimnis")
                .language("DE");
    }
}

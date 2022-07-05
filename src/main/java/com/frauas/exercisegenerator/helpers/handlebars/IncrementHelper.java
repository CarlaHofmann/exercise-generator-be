package com.frauas.exercisegenerator.helpers.handlebars;

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class IncrementHelper implements Helper<Integer> {
    public static final String NAME = "inc";

    @Override
    public Object apply(Integer context, Options options) throws IOException {
        return context + 1;
    }
}

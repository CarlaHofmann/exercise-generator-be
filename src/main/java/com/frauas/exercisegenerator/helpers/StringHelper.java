package com.frauas.exercisegenerator.helpers;

public class StringHelper {
    public static String toSnakeCase(String string) {
        String regex = "(.?) (.)";
        String replacement = "$1_$2";

        return string.replaceAll(regex, replacement)
                .toLowerCase();
    }
}

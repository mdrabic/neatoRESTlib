package com.mdrabic.rest.util;

/**
 * {WRITE A DESCRIPTION!!!}
 *
 * @author mike
 */
public class Validate {

    private Validate(){};

    public static void notNull(Object toTest, String msg) {
        String errorMessage = "Null arguments are not allowed";
        if (msg != null) {
            errorMessage = msg;
        }
        if (toTest == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}

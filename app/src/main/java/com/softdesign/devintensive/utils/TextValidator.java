package com.softdesign.devintensive.utils;

/**
 * Interface for validation input text data.
 */
public interface TextValidator {

    /** Validates input text.
     * @param text text for validating
     * @return is valid input text or not */
    boolean validate(String text);
}

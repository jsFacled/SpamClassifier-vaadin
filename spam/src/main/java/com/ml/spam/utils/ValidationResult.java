package com.ml.spam.utils;

import java.util.Set;


public class ValidationResult {
    private final boolean isValidStructure;
    private final Set<String> duplicateWords;

    public ValidationResult(boolean isValidStructure, Set<String> duplicateWords) {
        this.isValidStructure = isValidStructure;
        this.duplicateWords = duplicateWords;
    }

    public boolean isValidStructure() {
        return isValidStructure;
    }

    public Set<String> getDuplicateWords() {
        return duplicateWords;
    }
}

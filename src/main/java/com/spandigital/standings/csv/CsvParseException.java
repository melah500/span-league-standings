package com.spandigital.standings.csv;

public final class CsvParseException extends RuntimeException {

    private final int lineNumber;

    public CsvParseException(int lineNumber, String reason, String rawLine) {
        super("line " + lineNumber + ": " + reason + ": \"" + rawLine + "\"");
        this.lineNumber = lineNumber;
    }

    public int lineNumber() {
        return lineNumber;
    }
}

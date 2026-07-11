package com.spandigital.standings.csv;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class MatchResultCsvParserRejectionTest {

    private final MatchResultCsvParser parser = new MatchResultCsvParser();

    private void assertRejects(String input, int expectedLine, String expectedReason) {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader(input)))
                .withMessageContaining("line " + expectedLine + ": " + expectedReason)
                .withMessageContaining("\"" + input.strip() + "\"")
                .satisfies(e -> org.assertj.core.api.Assertions.assertThat(e.lineNumber())
                        .isEqualTo(expectedLine));
    }

    @Test
    void rejectsLineWithNoComma() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions 3 Snakes 1")))
                .withMessageContaining("line 1")
                .withMessageContaining("expected exactly one comma");
    }

    @Test
    void rejectsLineWithTwoCommas() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions 3, Snakes, 1")))
                .withMessageContaining("line 1")
                .withMessageContaining("expected exactly one comma");
    }

    @Test
    void rejectsNonIntegerScore() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions abc, Snakes 1")))
                .withMessageContaining("line 1")
                .withMessageContaining("score must be an integer");
    }

    @Test
    void rejectsNegativeHomeScore() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions -1, Snakes 1")))
                .withMessageContaining("line 1")
                .withMessageContaining("score must not be negative");
    }

    @Test
    void rejectsNegativeAwayScore() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions 1, Snakes -2")))
                .withMessageContaining("line 1")
                .withMessageContaining("score must not be negative");
    }

    @Test
    void rejectsScoreThatOverflowsInt() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions 99999999999, Snakes 1")))
                .withMessageContaining("line 1")
                .withMessageContaining("score too large");
    }

    @Test
    void rejectsMissingNameOnHomeSide() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader(", Snakes 1")))
                .withMessageContaining("line 1");
    }

    @Test
    void rejectsMissingNameOnAwaySide() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions 3,")))
                .withMessageContaining("line 1");
    }

    @Test
    void rejectsSelfMatch() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions 3, Lions 1")))
                .withMessageContaining("line 1")
                .withMessageContaining("home and away team must differ");
    }

    @Test
    void reportsPhysicalLineNumberForBadLineAfterBlanks() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions 3, Snakes 1\n\nBad line")))
                .satisfies(e -> org.assertj.core.api.Assertions.assertThat(e.lineNumber())
                        .isEqualTo(3));
    }

    @Test
    void rawLineAppearsInMessageSoCallerCanShowContext() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions xyz, Snakes 1")))
                .withMessageContaining("\"Lions xyz, Snakes 1\"");
    }

    @Test
    void failsFastOnFirstBadLineDoesNotContinueParsing() throws IOException {
        assertThatExceptionOfType(CsvParseException.class)
                .isThrownBy(() -> parser.parse(new StringReader("Lions xyz, Snakes 1\nGood Team 1, Other Team 0")))
                .satisfies(e -> org.assertj.core.api.Assertions.assertThat(e.lineNumber())
                        .isEqualTo(1));
    }
}

package com.spandigital.standings;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppTest {

    private static final String VALID_INPUT = "Lions 3, Snakes 1\nTarantulas 1, FC Awesome 0\n";

    private App app(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        return new App(
                new ByteArrayInputStream(bytes),
                new PrintStream(out, true, StandardCharsets.UTF_8),
                new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private String out() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String err() {
        return err.toString(StandardCharsets.UTF_8);
    }

    @Test
    void noArgsReadsStdinWritesStdout() throws IOException {
        int exit = app(VALID_INPUT).run();

        assertThat(exit).isZero();
        assertThat(out()).contains("1. Tarantulas");
        assertThat(err()).isEmpty();
    }

    @Test
    void emptyStdinProducesEmptyOutputExitZero() throws IOException {
        int exit = app("").run();

        assertThat(exit).isZero();
        assertThat(out()).isEmpty();
        assertThat(err()).isEmpty();
    }

    @Test
    void malformedInputExitsOneWithStderrMessage() throws IOException {
        int exit = app("Lions xyz, Snakes 1").run();

        assertThat(exit).isEqualTo(1);
        assertThat(err()).startsWith("error: ");
        assertThat(out()).isEmpty();
    }

    @Test
    void oneArgReadsFileWritesStdout(@TempDir Path dir) throws IOException {
        Path input = dir.resolve("matches.csv");
        Files.writeString(input, VALID_INPUT);

        int exit = new App(
                        null,
                        new PrintStream(out, true, StandardCharsets.UTF_8),
                        new PrintStream(err, true, StandardCharsets.UTF_8))
                .run(input.toString());

        assertThat(exit).isZero();
        assertThat(out()).contains("1. Tarantulas");
    }

    @Test
    void twoArgsReadsFileWritesOutputFile(@TempDir Path dir) throws IOException {
        Path input = dir.resolve("matches.csv");
        Path output = dir.resolve("standings.csv");
        Files.writeString(input, VALID_INPUT);

        int exit = new App(
                        null,
                        new PrintStream(out, true, StandardCharsets.UTF_8),
                        new PrintStream(err, true, StandardCharsets.UTF_8))
                .run(input.toString(), output.toString());

        assertThat(exit).isZero();
        assertThat(output).exists();
        assertThat(Files.readString(output)).contains("1. Tarantulas");
        assertThat(out()).isEmpty();
    }

    @Test
    void missingInputFileExitsOneWithStderrMessage(@TempDir Path dir) throws IOException {
        int exit = new App(
                        null,
                        new PrintStream(out, true, StandardCharsets.UTF_8),
                        new PrintStream(err, true, StandardCharsets.UTF_8))
                .run(dir.resolve("nonexistent.csv").toString());

        assertThat(exit).isEqualTo(1);
        assertThat(err()).startsWith("error: ");
        assertThat(out()).isEmpty();
    }

    @Test
    void threeArgsExitsTwoWithUsageOnStderr() throws IOException {
        int exit = app("").run("a", "b", "c");

        assertThat(exit).isEqualTo(2);
        assertThat(err()).isNotEmpty();
        assertThat(out()).isEmpty();
    }

    @Test
    void helpFlagPrintsUsageToStdoutExitZero() throws IOException {
        int exit = app("").run("--help");

        assertThat(exit).isZero();
        assertThat(out()).isNotEmpty();
    }

    @Test
    void shortHelpFlagPrintsUsageToStdoutExitZero() throws IOException {
        int exit = app("").run("-h");

        assertThat(exit).isZero();
        assertThat(out()).isNotEmpty();
    }
}

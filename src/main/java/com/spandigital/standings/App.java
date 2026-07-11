package com.spandigital.standings;

import com.spandigital.standings.csv.CsvParseException;
import com.spandigital.standings.csv.MatchResultCsvParser;
import com.spandigital.standings.csv.StandingsCsvFormatter;
import com.spandigital.standings.domain.LeagueTable;
import com.spandigital.standings.domain.MatchResult;
import com.spandigital.standings.domain.StandingsCalculator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class App {

    private static final String USAGE = "Usage: standings [INPUT_FILE [OUTPUT_FILE]]\n"
            + "       standings -h|--help\n"
            + "\n"
            + "  INPUT_FILE   CSV file of match results (default: stdin)\n"
            + "  OUTPUT_FILE  destination for standings CSV (default: stdout)\n"
            + "\n"
            + "Exit codes: 0 success, 1 data/IO error, 2 usage error\n";

    private final InputStream stdin;
    private final PrintStream out;
    private final PrintStream err;
    private final MatchResultCsvParser parser = new MatchResultCsvParser();
    private final StandingsCsvFormatter formatter = new StandingsCsvFormatter();
    private final StandingsCalculator calculator = new StandingsCalculator();

    public App(InputStream stdin, PrintStream out, PrintStream err) {
        this.stdin = stdin;
        this.out = out;
        this.err = err;
    }

    public int run(String... args) {
        if (args.length > 2) {
            err.print(USAGE);
            return 2;
        }
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            out.print(USAGE);
            return 0;
        }
        for (String arg : args) {
            if (arg.startsWith("-")) {
                err.print(USAGE);
                return 2;
            }
        }

        List<MatchResult> matches;
        try {
            matches = readMatches(args);
        } catch (CsvParseException e) {
            err.println("error: " + e.getMessage());
            return 1;
        } catch (FileNotFoundException e) {
            err.println("error: " + e.getMessage());
            return 1;
        } catch (IOException e) {
            err.println("error: " + e.getMessage());
            return 1;
        }

        LeagueTable table = LeagueTable.from(calculator.calculate(matches));
        String output = formatter.format(table);

        try {
            writeOutput(output, args);
        } catch (IOException e) {
            err.println("error: " + e.getMessage());
            return 1;
        }

        return 0;
    }

    private List<MatchResult> readMatches(String[] args) throws IOException {
        if (args.length == 0) {
            return parser.parse(new InputStreamReader(stdin, StandardCharsets.UTF_8));
        }
        String content = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
        return parser.parse(new StringReader(content));
    }

    private void writeOutput(String output, String[] args) throws IOException {
        if (args.length < 2) {
            out.print(output);
        } else {
            Files.writeString(Path.of(args[1]), output, StandardCharsets.UTF_8);
        }
    }
}

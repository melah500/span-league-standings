package com.spandigital.standings.csv;

import com.spandigital.standings.domain.MatchResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MatchResultCsvParser {

    private static final Pattern HALF_PATTERN = Pattern.compile("^(.*\\S)\\s+(\\S+)$");

    public List<MatchResult> parse(Reader reader) throws IOException {
        List<MatchResult> results = new ArrayList<>();
        BufferedReader buffered = new BufferedReader(reader);
        String line;
        int lineNumber = 0;
        while ((line = buffered.readLine()) != null) {
            lineNumber++;
            String stripped = line.strip();
            if (stripped.isEmpty()) {
                continue;
            }
            results.add(parseLine(stripped, lineNumber));
        }
        return List.copyOf(results);
    }

    private static MatchResult parseLine(String line, int lineNumber) {
        int commaIndex = line.indexOf(',');
        if (commaIndex < 0 || line.indexOf(',', commaIndex + 1) >= 0) {
            throw new CsvParseException(lineNumber, "expected exactly one comma", line);
        }
        String homeHalf = line.substring(0, commaIndex).strip();
        String awayHalf = line.substring(commaIndex + 1).strip();
        String homeTeam = parseName(homeHalf, lineNumber, line);
        int homeScore = parseScore(lastToken(homeHalf), lineNumber, line);
        String awayTeam = parseName(awayHalf, lineNumber, line);
        int awayScore = parseScore(lastToken(awayHalf), lineNumber, line);
        if (homeTeam.equals(awayTeam)) {
            throw new CsvParseException(lineNumber, "home and away team must differ", line);
        }
        return new MatchResult(homeTeam, homeScore, awayTeam, awayScore);
    }

    private static String parseName(String half, int lineNumber, String raw) {
        Matcher m = HALF_PATTERN.matcher(half);
        if (!m.matches()) {
            throw new CsvParseException(lineNumber, "missing team name or score", raw);
        }
        return m.group(1);
    }

    private static String lastToken(String half) {
        Matcher m = HALF_PATTERN.matcher(half);
        if (!m.matches()) {
            return half;
        }
        return m.group(2);
    }

    private static int parseScore(String token, int lineNumber, String raw) {
        if (token.matches("-\\d+")) {
            throw new CsvParseException(lineNumber, "score must not be negative", raw);
        }
        if (!token.matches("\\d+")) {
            throw new CsvParseException(lineNumber, "score must be an integer", raw);
        }
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new CsvParseException(lineNumber, "score too large", raw);
        }
    }
}

package com.spandigital.standings.csv;

import static org.assertj.core.api.Assertions.assertThat;

import com.spandigital.standings.domain.MatchResult;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;

class MatchResultCsvParserTest {

    private final MatchResultCsvParser parser = new MatchResultCsvParser();

    private List<MatchResult> parse(String input) throws IOException {
        return parser.parse(new StringReader(input));
    }

    @Test
    void parsesASingleLine() throws IOException {
        assertThat(parse("Lions 3, Snakes 1")).containsExactly(new MatchResult("Lions", 3, "Snakes", 1));
    }

    @Test
    void parsesMultiWordTeamNames() throws IOException {
        assertThat(parse("Manchester City 2, West Ham United 1"))
                .containsExactly(new MatchResult("Manchester City", 2, "West Ham United", 1));
    }

    @Test
    void parsesZeroScores() throws IOException {
        assertThat(parse("Burnley 0, Leicester City 0"))
                .containsExactly(new MatchResult("Burnley", 0, "Leicester City", 0));
    }

    @Test
    void parsesMultipleLines() throws IOException {
        assertThat(parse("Lions 3, Snakes 1\nTarantulas 1, FC Awesome 0"))
                .containsExactly(
                        new MatchResult("Lions", 3, "Snakes", 1), new MatchResult("Tarantulas", 1, "FC Awesome", 0));
    }

    @Test
    void skipsBlankLines() throws IOException {
        assertThat(parse("Lions 3, Snakes 1\n\nTarantulas 1, FC Awesome 0"))
                .containsExactly(
                        new MatchResult("Lions", 3, "Snakes", 1), new MatchResult("Tarantulas", 1, "FC Awesome", 0));
    }

    @Test
    void skipsWhitespaceOnlyLines() throws IOException {
        assertThat(parse("Lions 3, Snakes 1\n   \nTarantulas 1, FC Awesome 0"))
                .containsExactly(
                        new MatchResult("Lions", 3, "Snakes", 1), new MatchResult("Tarantulas", 1, "FC Awesome", 0));
    }

    @Test
    void toleratesCrlfLineEndings() throws IOException {
        assertThat(parse("Lions 3, Snakes 1\r\nTarantulas 1, FC Awesome 0"))
                .containsExactly(
                        new MatchResult("Lions", 3, "Snakes", 1), new MatchResult("Tarantulas", 1, "FC Awesome", 0));
    }

    @Test
    void toleratesLeadingAndTrailingWhitespaceOnLine() throws IOException {
        assertThat(parse("  Lions 3, Snakes 1  ")).containsExactly(new MatchResult("Lions", 3, "Snakes", 1));
    }

    @Test
    void emptyInputProducesEmptyList() throws IOException {
        assertThat(parse("")).isEmpty();
    }

    @Test
    void preservesParseOrderMatchingInputOrder() throws IOException {
        String input = "Burnley 1, Leeds 2\nArsenal 0, Chelsea 0\nEverton 3, Liverpool 1";
        List<MatchResult> results = parse(input);
        assertThat(results).extracting(MatchResult::homeTeam).containsExactly("Burnley", "Arsenal", "Everton");
    }
}

package com.spandigital.standings;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Golden end-to-end test: the app, invoked exactly as a user would invoke it
 * (file argument), must reproduce the historical First Division table of
 * Saturday 28 September 1974 from the verified match data.
 *
 * <p>Expected values are transcribed from docs/expected-table-28-sep-1974.md
 * (Statto via Wayback Machine; independently cross-checked against 11v11 -
 * see that document). Every points group in the reference is ordered by goal
 * average alone, so this test also exercises the comparator against real data:
 * 13 pts Liverpool 17/8 over Everton 14/11 over Sheffield United 14/14, and
 * the four-team 6-pt group Leeds/Arsenal/Tottenham/QPR, among others.
 */
class Week10EndToEndTest {

    private static final String EXPECTED_TABLE =
            """
            1. Ipswich Town, 16 pts
            2. Manchester City, 14 pts
            3. Liverpool, 13 pts
            4. Everton, 13 pts
            5. Sheffield United, 13 pts
            6. Newcastle United, 12 pts
            7. Middlesbrough, 11 pts
            8. Derby County, 11 pts
            9. Stoke City, 11 pts
            10. Wolverhampton Wanderers, 11 pts
            11. Carlisle United, 10 pts
            12. West Ham United, 9 pts
            13. Burnley, 9 pts
            14. Birmingham City, 8 pts
            15. Coventry City, 8 pts
            16. Leicester City, 7 pts
            17. Luton Town, 7 pts
            18. Chelsea, 7 pts
            19. Leeds United, 6 pts
            20. Arsenal, 6 pts
            21. Tottenham Hotspur, 6 pts
            22. Queens Park Rangers, 6 pts
            """;

    @Test
    void reproducesTheHistorical28September1974Table() {
        Path input = Path.of("data", "input", "week-10-results.csv");
        assertThat(input).as("verified week-10 dataset must be present").exists();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        int exit = new App(
                        new ByteArrayInputStream(new byte[0]),
                        new PrintStream(out, true, StandardCharsets.UTF_8),
                        new PrintStream(err, true, StandardCharsets.UTF_8))
                .run(input.toString());

        assertThat(err.toString(StandardCharsets.UTF_8)).isEmpty();
        assertThat(exit).isZero();
        assertThat(out.toString(StandardCharsets.UTF_8)).isEqualTo(EXPECTED_TABLE);
    }

    @Test
    void datasetContainsExactly107MatchLines() throws Exception {
        long matchLines = Files.readAllLines(Path.of("data", "input", "week-10-results.csv")).stream()
                .filter(line -> !line.isBlank())
                .count();

        assertThat(matchLines).isEqualTo(107);
    }
}

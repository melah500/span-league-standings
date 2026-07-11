package com.spandigital.standings.csv;

import static org.assertj.core.api.Assertions.assertThat;

import com.spandigital.standings.domain.LeagueTable;
import com.spandigital.standings.domain.MatchResult;
import com.spandigital.standings.domain.StandingsCalculator;
import com.spandigital.standings.domain.TeamRecord;
import java.util.List;
import org.junit.jupiter.api.Test;

class StandingsCsvFormatterTest {

    private final StandingsCsvFormatter formatter = new StandingsCsvFormatter();

    @Test
    void emptyTableFormatsToEmptyString() {
        assertThat(formatter.format(LeagueTable.from(List.of()))).isEmpty();
    }

    @Test
    void singleEntryWithOnePtUsesSingular() {
        TeamRecord onePoint = new TeamRecord("Sole", 1, 0, 1, 0, 1, 1);

        assertThat(formatter.format(LeagueTable.from(List.of(onePoint)))).isEqualTo("1. Sole, 1 pt\n");
    }

    @Test
    void zeroPointsUsesPlural() {
        TeamRecord noPoints = new TeamRecord("None", 1, 0, 0, 1, 0, 1);

        assertThat(formatter.format(LeagueTable.from(List.of(noPoints)))).isEqualTo("1. None, 0 pts\n");
    }

    @Test
    void twoOrMorePointsUsesPlural() {
        TeamRecord twoPoints = new TeamRecord("Two", 1, 1, 0, 0, 1, 0);

        assertThat(formatter.format(LeagueTable.from(List.of(twoPoints)))).isEqualTo("1. Two, 2 pts\n");
    }

    @Test
    void multipleEntriesJoinedWithNewlineAndTrailingNewline() {
        List<MatchResult> matches =
                List.of(new MatchResult("Lions", 3, "Snakes", 1), new MatchResult("Lions", 1, "Tarantulas", 1));

        String result = formatter.format(LeagueTable.from(new StandingsCalculator().calculate(matches)));

        assertThat(result)
                .isEqualTo(
                        """
                        1. Lions, 3 pts
                        2. Tarantulas, 1 pt
                        3. Snakes, 0 pts
                        """);
    }

    @Test
    void separatorIsAlwaysUnixNewlineNeverPlatformSeparator() {
        TeamRecord team = new TeamRecord("Team", 1, 1, 0, 0, 2, 0);
        assertThat(formatter.format(LeagueTable.from(List.of(team)))).doesNotContain("\r");
    }
}

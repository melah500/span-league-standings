package com.spandigital.standings.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Invariants from docs/rules-research.md section 9, checked over a small
 * fixture rather than single hand-picked examples.
 */
class StandingsInvariantsTest {

    private static final List<MatchResult> FIXTURE = List.of(
            new MatchResult("Luton Town", 1, "Liverpool", 2),
            new MatchResult("Manchester City", 4, "West Ham United", 0),
            new MatchResult("Everton", 0, "Derby County", 0),
            new MatchResult("Liverpool", 2, "Leicester City", 1),
            new MatchResult("West Ham United", 2, "Luton Town", 0),
            new MatchResult("Derby County", 1, "Manchester City", 1),
            new MatchResult("Leicester City", 0, "Everton", 3),
            new MatchResult("Liverpool", 5, "Everton", 2));

    private final StandingsCalculator calculator = new StandingsCalculator();

    @Test
    void playedEqualsWonPlusDrawnPlusLostForEveryTeam() {
        for (TeamRecord record : calculator.calculate(FIXTURE)) {
            assertThat(record.played())
                    .as("%s: played = won + drawn + lost", record.team())
                    .isEqualTo(record.won() + record.drawn() + record.lost());
        }
    }

    @Test
    void totalGoalsForEqualsTotalGoalsAgainstAcrossTheTable() {
        List<TeamRecord> standings = calculator.calculate(FIXTURE);

        int totalFor = standings.stream().mapToInt(TeamRecord::goalsFor).sum();
        int totalAgainst = standings.stream().mapToInt(TeamRecord::goalsAgainst).sum();

        assertThat(totalFor).isEqualTo(totalAgainst);
    }

    @Test
    void eachProcessedMatchRaisesTotalPlayedByExactlyTwo() {
        for (int prefix = 0; prefix <= FIXTURE.size(); prefix++) {
            List<TeamRecord> standings = calculator.calculate(FIXTURE.subList(0, prefix));

            int totalPlayed = standings.stream().mapToInt(TeamRecord::played).sum();

            assertThat(totalPlayed).as("after %d matches", prefix).isEqualTo(prefix * 2);
        }
    }
}

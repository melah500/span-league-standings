package com.spandigital.standings.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class LeagueTableTest {

    /**
     * Positions are strictly sequential (1, 2, 3, ...) with no shared places:
     * StandingsRanking is a total order - teams equal on points, goal average
     * and goals for are already separated deterministically by name - so a
     * "joint Nth" representation would never fire and is deliberately not
     * modelled.
     */
    @Test
    void assignsStrictlySequentialPositionsInComparatorOrder() {
        TeamRecord leader = new TeamRecord("Leader", 3, 3, 0, 0, 6, 1);
        TeamRecord twinA = new TeamRecord("TwinA", 3, 1, 1, 1, 4, 4);
        TeamRecord twinB = new TeamRecord("TwinB", 3, 1, 1, 1, 4, 4);

        LeagueTable table = LeagueTable.from(List.of(twinB, leader, twinA));

        assertThat(table.entries())
                .containsExactly(new TableEntry(1, leader), new TableEntry(2, twinA), new TableEntry(3, twinB));
    }

    @Test
    void ranksTheInvariantsFixtureIntoAFullyOrderedTable() {
        // Same fixture as StandingsInvariantsTest (cycle 4).
        List<MatchResult> matches = List.of(
                new MatchResult("Luton Town", 1, "Liverpool", 2),
                new MatchResult("Manchester City", 4, "West Ham United", 0),
                new MatchResult("Everton", 0, "Derby County", 0),
                new MatchResult("Liverpool", 2, "Leicester City", 1),
                new MatchResult("West Ham United", 2, "Luton Town", 0),
                new MatchResult("Derby County", 1, "Manchester City", 1),
                new MatchResult("Leicester City", 0, "Everton", 3),
                new MatchResult("Liverpool", 5, "Everton", 2));

        LeagueTable table = LeagueTable.from(new StandingsCalculator().calculate(matches));

        assertThat(table.entries()).hasSize(7);
        assertThat(table.entries().stream().map(TableEntry::position)).containsExactly(1, 2, 3, 4, 5, 6, 7);
        assertThat(table.entries().stream().map(entry -> entry.record().team()))
                .containsExactly(
                        "Liverpool", // 6 pts
                        "Manchester City", // 3 pts, GAve 5.000
                        "Everton", // 3 pts, GAve 1.000
                        "Derby County", // 2 pts, GAve 1.000
                        "West Ham United", // 2 pts, GAve 0.500
                        "Luton Town", // 0 pts, GAve 0.250
                        "Leicester City"); // 0 pts, GAve 0.200
    }
}

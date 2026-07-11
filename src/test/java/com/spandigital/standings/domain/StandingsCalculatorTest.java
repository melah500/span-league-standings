package com.spandigital.standings.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class StandingsCalculatorTest {

    @Test
    void singleMatchYieldsWinnerAndLoserRecords() {
        var calculator = new StandingsCalculator();

        List<TeamRecord> standings = calculator.calculate(List.of(new MatchResult("Liverpool", 3, "Arsenal", 1)));

        assertThat(standings)
                .containsExactlyInAnyOrder(
                        new TeamRecord("Liverpool", 1, 1, 0, 0, 3, 1), new TeamRecord("Arsenal", 1, 0, 0, 1, 1, 3));
        assertThat(byTeam(standings, "Liverpool").points()).isEqualTo(2);
        assertThat(byTeam(standings, "Arsenal").points()).isEqualTo(0);
    }

    private static TeamRecord byTeam(List<TeamRecord> standings, String team) {
        return standings.stream()
                .filter(record -> record.team().equals(team))
                .findFirst()
                .orElseThrow();
    }
}

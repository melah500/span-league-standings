package com.spandigital.standings.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
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

    @Test
    void drawnMatchAwardsOnePointToEachTeamWithMirroredGoals() {
        var calculator = new StandingsCalculator();

        List<TeamRecord> standings = calculator.calculate(List.of(new MatchResult("Everton", 2, "Derby County", 2)));

        assertThat(standings)
                .containsExactlyInAnyOrder(
                        new TeamRecord("Everton", 1, 0, 1, 0, 2, 2), new TeamRecord("Derby County", 1, 0, 1, 0, 2, 2));
        assertThat(byTeam(standings, "Everton").points()).isEqualTo(1);
        assertThat(byTeam(standings, "Derby County").points()).isEqualTo(1);
    }

    @Test
    void awayWinAwardsTwoPointsToAwayTeam() {
        var calculator = new StandingsCalculator();

        List<TeamRecord> standings = calculator.calculate(List.of(new MatchResult("Chelsea", 0, "Carlisle United", 2)));

        assertThat(byTeam(standings, "Carlisle United")).isEqualTo(new TeamRecord("Carlisle United", 1, 1, 0, 0, 2, 0));
        assertThat(byTeam(standings, "Carlisle United").points()).isEqualTo(2);
        assertThat(byTeam(standings, "Chelsea").points()).isEqualTo(0);
    }

    @Test
    void teamAppearingInMultipleMatchesAccumulatesIntoOneRecord() {
        var calculator = new StandingsCalculator();

        List<TeamRecord> standings = calculator.calculate(
                List.of(new MatchResult("Liverpool", 3, "Arsenal", 1), new MatchResult("Everton", 1, "Liverpool", 1)));

        assertThat(standings).hasSize(3);
        assertThat(byTeam(standings, "Liverpool")).isEqualTo(new TeamRecord("Liverpool", 2, 1, 1, 0, 4, 2));
        assertThat(byTeam(standings, "Liverpool").points()).isEqualTo(3);
    }

    @Test
    void totalsDoNotDependOnInputOrder() {
        var calculator = new StandingsCalculator();
        var matches = List.of(
                new MatchResult("Liverpool", 3, "Arsenal", 1),
                new MatchResult("Everton", 1, "Liverpool", 1),
                new MatchResult("Arsenal", 0, "Everton", 2));
        var reversed = new ArrayList<>(matches);
        Collections.reverse(reversed);

        assertThat(calculator.calculate(matches)).containsExactlyInAnyOrderElementsOf(calculator.calculate(reversed));
    }

    private static TeamRecord byTeam(List<TeamRecord> standings, String team) {
        return standings.stream()
                .filter(record -> record.team().equals(team))
                .findFirst()
                .orElseThrow();
    }
}

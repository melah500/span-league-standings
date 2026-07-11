package com.spandigital.standings.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Ranking spec from docs/rules-research.md sections 3-5 and 8:
 * points desc, goal average desc (GF/GA, never goal difference, never floats),
 * goals for desc, team name asc (documented application-level fallback).
 */
class StandingsRankingTest {

    private final StandingsRanking ranking = new StandingsRanking();

    @Test
    void higherPointsRanksFirstRegardlessOfGoalRecord() {
        TeamRecord scrappyWinner = record("Scrappy", 2, 0, 1, 2, 20);
        TeamRecord freeScoringDrawer = record("FreeScoring", 1, 1, 1, 30, 0);

        assertRanksAbove(scrappyWinner, freeScoringDrawer);
    }

    @Test
    void equalPointsHigherGoalAverageRanksFirstEvenWhenGoalDifferenceDisagrees() {
        // A: GAve 2.000, GD +10. B: GAve ~1.786, GD +11.
        // Goal difference would rank B first; the 1974/75 rule ranks A first.
        TeamRecord teamA = record("Team A", 5, 3, 2, 20, 10);
        TeamRecord teamB = record("Team B", 5, 3, 2, 25, 14);

        assertRanksAbove(teamA, teamB);
    }

    @Test
    void equalPointsAndGoalAverageHigherGoalsForRanksFirst() {
        // Identical average (2.000); 20 goals beat 10.
        TeamRecord prolific = record("Prolific", 5, 3, 2, 20, 10);
        TeamRecord frugal = record("Frugal", 5, 3, 2, 10, 5);

        assertRanksAbove(prolific, frugal);
    }

    @Test
    void equalOnAllThreeCriteriaRanksAlphabeticallyByTeamName() {
        TeamRecord arsenal = record("Arsenal", 5, 3, 2, 20, 10);
        TeamRecord burnley = record("Burnley", 5, 3, 2, 20, 10);

        assertRanksAbove(arsenal, burnley);
    }

    @Test
    void zeroGoalsAgainstWithGoalsScoredRanksAboveAnyFiniteGoalAverage() {
        TeamRecord unbreached = record("Unbreached", 5, 3, 2, 5, 0);
        TeamRecord highFiniteAverage = record("HighAverage", 5, 3, 2, 100, 1);

        assertRanksAbove(unbreached, highFiniteAverage);
    }

    @Test
    void betweenTwoZeroGoalsAgainstTeamsHigherGoalsForRanksFirst() {
        TeamRecord threeGoals = record("ThreeGoals", 5, 3, 2, 3, 0);
        TeamRecord oneGoal = record("OneGoal", 5, 3, 2, 1, 0);

        assertRanksAbove(threeGoals, oneGoal);
    }

    @Test
    void zeroZeroGoalRecordRanksBelowAnyPositiveGoalAverageAndDoesNotThrow() {
        // Documented placement: a 0/0 goal record is treated as goal average
        // zero - below every team that has scored, level with other zero
        // averages (then goals for, then name).
        TeamRecord goalless = record("Goalless", 0, 5, 0, 0, 0);
        TeamRecord modestScorer = record("Modest", 0, 5, 0, 1, 9);

        assertThatCode(() -> ranking.compare(goalless, modestScorer)).doesNotThrowAnyException();
        assertRanksAbove(modestScorer, goalless);
    }

    @Test
    void zeroZeroRecordTiesWithZeroAverageThenFallsToAlphabetical() {
        TeamRecord goallessUnplayedStyle = record("Aardvark", 0, 5, 0, 0, 0);
        TeamRecord zeroAverage = record("Zebra", 0, 5, 0, 0, 9);

        assertRanksAbove(goallessUnplayedStyle, zeroAverage);
    }

    @Test
    void goalAverageComparisonSurvivesValuesThatOverflowIntMultiplication() {
        // 46341 * 46341 overflows int; 46340 * 46340 does not. With int
        // arithmetic the overflow flips the sign and ranks Below first.
        // Unrealistic for football; required by the comparator contract.
        TeamRecord above = record("Above", 5, 3, 2, 46341, 46340);
        TeamRecord below = record("Below", 5, 3, 2, 46340, 46341);

        assertRanksAbove(above, below);
    }

    @Test
    void comparatorIsTransitiveWithEveryTieBreakLevelDecidingAtLeastOnePair() {
        TeamRecord byPoints = record("Points", 6, 2, 2, 10, 10);
        TeamRecord byAverage = record("Average", 5, 3, 2, 21, 10);
        TeamRecord byGoalsFor = record("GoalsFor", 5, 3, 2, 20, 10);
        TeamRecord byNameFirst = record("YFirst", 5, 3, 2, 10, 5);
        TeamRecord byNameLast = record("ZLast", 5, 3, 2, 10, 5);

        List<TeamRecord> expectedOrder = List.of(byPoints, byAverage, byGoalsFor, byNameFirst, byNameLast);
        for (int i = 0; i < expectedOrder.size(); i++) {
            for (int j = i + 1; j < expectedOrder.size(); j++) {
                assertRanksAbove(expectedOrder.get(i), expectedOrder.get(j));
            }
        }
    }

    @Test
    void comparatorIsTransitiveAcrossInfiniteFiniteAndZeroZeroAverageClasses() {
        TeamRecord infiniteAverage = record("Infinite", 5, 3, 2, 4, 0);
        TeamRecord finiteAverage = record("Finite", 5, 3, 2, 9, 3);
        TeamRecord zeroZero = record("ZeroZero", 0, 10, 0, 0, 0);
        TeamRecord samePointsAsZeroZero = record("ZeroZeroPeer", 0, 10, 0, 0, 0);

        assertRanksAbove(infiniteAverage, finiteAverage);
        assertRanksAbove(infiniteAverage, record("GoallessRival", 5, 3, 2, 0, 0));
        assertThat(ranking.compare(zeroZero, samePointsAsZeroZero))
                .isEqualTo(zeroZero.team().compareTo(samePointsAsZeroZero.team()));
    }

    @Test
    void ranksTheHistorical1975LiverpoolAboveIpswichExample() {
        // rules-research.md section 10: both finished on 51 points; Liverpool
        // 60/39 (~1.538) ranked above Ipswich 66/44 (1.500).
        TeamRecord liverpool = record("Liverpool", 20, 11, 11, 60, 39);
        TeamRecord ipswich = record("Ipswich Town", 23, 5, 14, 66, 44);

        assertThat(liverpool.points()).isEqualTo(51);
        assertThat(ipswich.points()).isEqualTo(51);
        assertRanksAbove(liverpool, ipswich);
    }

    @Test
    void comparingARecordWithItselfReturnsZero() {
        TeamRecord team = record("Reflexive", 5, 3, 2, 20, 10);

        assertThat(ranking.compare(team, team)).isZero();
    }

    private void assertRanksAbove(TeamRecord higher, TeamRecord lower) {
        assertThat(ranking.compare(higher, lower))
                .as("%s should rank above %s", higher.team(), lower.team())
                .isNegative();
        assertThat(ranking.compare(lower, higher))
                .as("%s should rank below %s", lower.team(), higher.team())
                .isPositive();
    }

    private static TeamRecord record(String team, int won, int drawn, int lost, int goalsFor, int goalsAgainst) {
        return new TeamRecord(team, won + drawn + lost, won, drawn, lost, goalsFor, goalsAgainst);
    }
}

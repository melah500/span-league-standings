package com.spandigital.standings.domain;

import java.util.Comparator;

/**
 * Ranking order of the 1974/75 English First Division (docs/rules-research.md
 * sections 3-5 and 8): points descending, then goal average (goals for divided
 * by goals against - not goal difference) descending, then goals for
 * descending, then team name ascending. The alphabetical fallback is an
 * application-level decision for deterministic output, not a historical rule.
 *
 * <p>Goal averages are compared by cross-multiplication in {@code long}
 * arithmetic; no floating point is used anywhere. Zero goals against is
 * handled explicitly: with goals scored it ranks above every finite average,
 * and a 0/0 goal record is treated as goal average zero.
 *
 * <p>Precondition: goal counts are non-negative. Cross-multiplication is not
 * a valid total order over negative values; input validation (rules-research
 * section 9) must reject negative scores before records reach this comparator.
 */
public final class StandingsRanking implements Comparator<TeamRecord> {

    @Override
    public int compare(TeamRecord left, TeamRecord right) {
        int byPoints = Integer.compare(right.points(), left.points());
        if (byPoints != 0) {
            return byPoints;
        }
        int byGoalAverage = compareByGoalAverageDescending(left, right);
        if (byGoalAverage != 0) {
            return byGoalAverage;
        }
        int byGoalsFor = Integer.compare(right.goalsFor(), left.goalsFor());
        if (byGoalsFor != 0) {
            return byGoalsFor;
        }
        return left.team().compareTo(right.team());
    }

    private static int compareByGoalAverageDescending(TeamRecord left, TeamRecord right) {
        boolean leftInfinite = isInfiniteAverage(left);
        boolean rightInfinite = isInfiniteAverage(right);
        if (leftInfinite || rightInfinite) {
            return Boolean.compare(rightInfinite, leftInfinite);
        }
        return Long.compare(crossProduct(right, left), crossProduct(left, right));
    }

    private static boolean isInfiniteAverage(TeamRecord record) {
        return record.goalsAgainst() == 0 && record.goalsFor() > 0;
    }

    /** left.goalsFor x right.goalsAgainst, with a 0/0 record normalised to 0/1. */
    private static long crossProduct(TeamRecord left, TeamRecord right) {
        long rightAgainst = right.goalsAgainst() == 0 ? 1 : right.goalsAgainst();
        return left.goalsFor() * rightAgainst;
    }
}

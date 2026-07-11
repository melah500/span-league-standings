package com.spandigital.standings.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StandingsCalculator {

    public List<TeamRecord> calculate(List<MatchResult> matches) {
        Map<String, TeamRecord> byTeam = new LinkedHashMap<>();
        for (MatchResult match : matches) {
            accumulate(byTeam, match.homeTeam(), match.homeScore(), match.awayScore());
            accumulate(byTeam, match.awayTeam(), match.awayScore(), match.homeScore());
        }
        return List.copyOf(byTeam.values());
    }

    private static void accumulate(Map<String, TeamRecord> byTeam, String team, int scored, int conceded) {
        byTeam.merge(team, singleMatchRecord(team, scored, conceded), StandingsCalculator::add);
    }

    private static TeamRecord singleMatchRecord(String team, int scored, int conceded) {
        return new TeamRecord(
                team,
                1,
                scored > conceded ? 1 : 0,
                scored == conceded ? 1 : 0,
                scored < conceded ? 1 : 0,
                scored,
                conceded);
    }

    private static TeamRecord add(TeamRecord total, TeamRecord single) {
        return new TeamRecord(
                total.team(),
                total.played() + single.played(),
                total.won() + single.won(),
                total.drawn() + single.drawn(),
                total.lost() + single.lost(),
                total.goalsFor() + single.goalsFor(),
                total.goalsAgainst() + single.goalsAgainst());
    }
}

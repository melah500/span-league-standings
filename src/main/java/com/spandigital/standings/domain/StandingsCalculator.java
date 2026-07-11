package com.spandigital.standings.domain;

import java.util.ArrayList;
import java.util.List;

public final class StandingsCalculator {

    public List<TeamRecord> calculate(List<MatchResult> matches) {
        List<TeamRecord> standings = new ArrayList<>();
        for (MatchResult match : matches) {
            standings.add(teamRecord(match.homeTeam(), match.homeScore(), match.awayScore()));
            standings.add(teamRecord(match.awayTeam(), match.awayScore(), match.homeScore()));
        }
        return List.copyOf(standings);
    }

    private static TeamRecord teamRecord(String team, int scored, int conceded) {
        return new TeamRecord(
                team,
                1,
                scored > conceded ? 1 : 0,
                scored == conceded ? 1 : 0,
                scored < conceded ? 1 : 0,
                scored,
                conceded);
    }
}

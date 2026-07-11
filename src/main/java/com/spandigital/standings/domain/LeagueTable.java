package com.spandigital.standings.domain;

import java.util.List;
import java.util.stream.IntStream;

/**
 * An ordered league table: team records ranked by {@link StandingsRanking}
 * with strictly sequential positions (1, 2, 3, ...). Positions are never
 * shared: the ranking is a total order, so teams level on every sporting
 * criterion are already separated deterministically by team name.
 */
public record LeagueTable(List<TableEntry> entries) {

    private static final StandingsRanking RANKING = new StandingsRanking();

    public LeagueTable {
        entries = List.copyOf(entries);
    }

    public static LeagueTable from(List<TeamRecord> records) {
        List<TeamRecord> ranked = records.stream().sorted(RANKING).toList();
        return new LeagueTable(IntStream.range(0, ranked.size())
                .mapToObj(index -> new TableEntry(index + 1, ranked.get(index)))
                .toList());
    }
}

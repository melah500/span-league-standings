package com.spandigital.standings.csv;

import com.spandigital.standings.domain.LeagueTable;
import com.spandigital.standings.domain.TableEntry;
import java.util.stream.Collectors;

public final class StandingsCsvFormatter {

    public String format(LeagueTable table) {
        if (table.entries().isEmpty()) {
            return "";
        }
        return table.entries().stream().map(this::formatEntry).collect(Collectors.joining("\n", "", "\n"));
    }

    private String formatEntry(TableEntry entry) {
        int pts = entry.record().points();
        String unit = pts == 1 ? "pt" : "pts";
        return entry.position() + ". " + entry.record().team() + ", " + pts + " " + unit;
    }
}

package com.spandigital.standings.domain;

public record TeamRecord(String team, int played, int won, int drawn, int lost, int goalsFor, int goalsAgainst) {

    public int points() {
        return won * 2 + drawn;
    }
}

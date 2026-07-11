#!/usr/bin/env python3
"""Throwaway auditor for data/input/week-10-results.csv.

Reports (never fixes):
  - row count
  - unique team names (expect exactly 22, one canonical spelling each)
  - duplicate home-away pairings (each fixture is played once per season)
  - self-matches and negative scores
  - per-team played counts (unequal is expected; wild outliers flagged)
  - aggregate P/W/D/L/GF/GA/Pts per team, compared against the Statto
    "table on 28 Sep 1974" page cached in data/raw/ — every discrepancy
    is reported.
"""

import re
import sys
from collections import Counter, defaultdict
from pathlib import Path

IN_PATH = Path("data/input/week-10-results.csv")
TABLE_PATH = Path("data/raw/statto-table-full-1974-09-28.html")

LINE_RE = re.compile(r"^(.*\S) (\d+), (.*\S) (\d+)$")

# Same normalisation as scripts/extract_results.py: the reference table is
# also from Statto, whose layout abbreviates this club's name.
NAME_NORMALISATIONS = {
    "Wolverhampton Wndrs": "Wolverhampton Wanderers",
}


def parse_matches():
    matches = []
    for n, line in enumerate(IN_PATH.read_text(encoding="utf-8").splitlines(), 1):
        if not line.strip():
            continue
        m = LINE_RE.match(line)
        if not m:
            print(f"MALFORMED line {n}: {line!r}")
            continue
        matches.append((n, m.group(1), int(m.group(2)), m.group(3), int(m.group(4))))
    return matches


def parse_reference_table():
    html = TABLE_PATH.read_text(encoding="utf-8", errors="replace")
    ref = {}
    for row in re.findall(r"<tr[^>]*>(.*?)</tr>", html, re.S):
        cells = [
            re.sub(r"<[^>]+>", "", c).strip()
            for c in re.findall(r"<td[^>]*>(.*?)</td>", row, re.S)
        ]
        if len(cells) >= 11 and cells[0].isdigit() and cells[2].isdigit():
            pos, team, p, w, d, l, f, a = cells[0], cells[1], *map(int, cells[2:8])
            team = NAME_NORMALISATIONS.get(team, team)
            pts = int(cells[10])
            ref[team] = dict(pos=int(pos), p=p, w=w, d=d, l=l, gf=f, ga=a, pts=pts)
    return ref


def main():
    matches = parse_matches()
    print(f"== row count ==\n{len(matches)} matches\n")

    teams = Counter()
    stats = defaultdict(lambda: dict(p=0, w=0, d=0, l=0, gf=0, ga=0, pts=0))
    pairings = Counter()
    problems = []

    for n, home, hs, away, as_, in matches:
        teams[home] += 1
        teams[away] += 1
        pairings[(home, away)] += 1
        if home == away:
            problems.append(f"line {n}: self-match {home!r}")
        if hs < 0 or as_ < 0:
            problems.append(f"line {n}: negative score")
        for team, gf, ga in ((home, hs, as_), (away, as_, hs)):
            s = stats[team]
            s["p"] += 1
            s["gf"] += gf
            s["ga"] += ga
            if gf > ga:
                s["w"] += 1
                s["pts"] += 2
            elif gf == ga:
                s["d"] += 1
                s["pts"] += 1
            else:
                s["l"] += 1

    print(f"== unique teams ==\n{len(teams)} teams")
    for t in sorted(teams):
        print(f"  {t} (appears {teams[t]}x)")
    print()

    dupes = {k: v for k, v in pairings.items() if v > 1}
    print("== duplicate home-away pairings ==")
    print("\n".join(f"  {k}: {v}x" for k, v in dupes.items()) or "  none")
    print("\n== self-matches / negative scores ==")
    print("\n".join(f"  {p}" for p in problems) or "  none")

    played = sorted((s["p"], t) for t, s in stats.items())
    print("\n== per-team played counts ==")
    for p, t in played:
        print(f"  {p}  {t}")

    print("\n== comparison vs Statto table on 28 Sep 1974 ==")
    ref = parse_reference_table()
    print(f"reference rows parsed: {len(ref)}")
    discrepancies = 0
    for team in sorted(set(ref) | set(stats)):
        if team not in ref:
            print(f"  DISCREPANCY: {team} in CSV but not in reference table")
            discrepancies += 1
            continue
        if team not in stats:
            print(f"  DISCREPANCY: {team} in reference table but not in CSV")
            discrepancies += 1
            continue
        r, s = ref[team], stats[team]
        for k in ("p", "w", "d", "l", "gf", "ga", "pts"):
            if r[k] != s[k]:
                print(f"  DISCREPANCY: {team} {k.upper()}: CSV={s[k]} ref={r[k]}")
                discrepancies += 1
    if discrepancies == 0:
        print("  all 7 aggregate columns match for every team")
    else:
        print(f"  TOTAL: {discrepancies} discrepancies")
        sys.exit(1)


if __name__ == "__main__":
    main()

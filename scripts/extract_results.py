#!/usr/bin/env python3
"""Throwaway extractor: Statto date pages -> data/input/week-10-results.csv.

Each data/raw/statto-results-<date>.html page lists ONLY First Division
matches for that date (the division filter is part of the Statto URL), so no
cross-division filtering is needed here. Match rows are <tr> elements whose
middle cell is a full-time score like "3-1".

Output uses the frozen input format from docs/requirements.md:
    <home team> <home score>, <away team> <away score>
one match per line, no header, in date order then page order.
"""

import glob
import re
import sys
from pathlib import Path

RAW_GLOB = "data/raw/statto-results-*.html"
OUT_PATH = Path("data/input/week-10-results.csv")

MONTHS = {
    "January": 1, "February": 2, "March": 3, "April": 4, "May": 5, "June": 6,
    "July": 7, "August": 8, "September": 9, "October": 10, "November": 11,
    "December": 12,
}

# Statto's layout abbreviates some club names; the dataset uses canonical
# club names (decision 2026-07-11, recorded in docs/expected-table-28-sep-1974.md).
NAME_NORMALISATIONS = {
    "Wolverhampton Wndrs": "Wolverhampton Wanderers",
}


def page_date(html: str, path: str) -> str:
    """Return the ISO date the page claims to show, from its <title>."""
    m = re.search(r"<title>[^<]*?(\d{1,2})(?:st|nd|rd|th) (\w+) (\d{4})", html)
    if not m:
        sys.exit(f"ERROR: no date in <title> of {path}")
    day, month, year = int(m.group(1)), MONTHS[m.group(2)], int(m.group(3))
    return f"{year:04d}-{month:02d}-{day:02d}"


def extract_matches(html: str) -> list[tuple[str, int, str, int]]:
    matches = []
    for row in re.findall(r"<tr[^>]*>(.*?)</tr>", html, re.S):
        cells = [
            re.sub(r"<[^>]+>", "", c).strip()
            for c in re.findall(r"<td[^>]*>(.*?)</td>", row, re.S)
        ]
        for i, cell in enumerate(cells):
            score = re.fullmatch(r"(\d+)-(\d+)", cell)
            if score and 0 < i < len(cells) - 1 and cells[i - 1] and cells[i + 1]:
                matches.append((
                    NAME_NORMALISATIONS.get(cells[i - 1], cells[i - 1]),
                    int(score.group(1)),
                    NAME_NORMALISATIONS.get(cells[i + 1], cells[i + 1]),
                    int(score.group(2)),
                ))
                break
    return matches


def main() -> None:
    pages = sorted(glob.glob(RAW_GLOB))
    if not pages:
        sys.exit(f"ERROR: no raw pages match {RAW_GLOB}; run scripts/fetch_results.sh")

    lines = []
    for path in pages:
        if "table" in path:
            continue
        html = open(path, encoding="utf-8", errors="replace").read()
        expected = re.search(r"(\d{4}-\d{2}-\d{2})", path).group(1)
        actual = page_date(html, path)
        if actual != expected:
            sys.exit(f"ERROR: {path} claims {actual}, expected {expected} "
                     "(Wayback may have redirected to a different page)")
        matches = extract_matches(html)
        if not matches:
            sys.exit(f"ERROR: no matches extracted from {path}")
        print(f"{expected}: {len(matches)} matches")
        lines += [f"{h} {hs}, {a} {as_}" for h, hs, a, as_ in matches]

    OUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    OUT_PATH.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"wrote {len(lines)} matches to {OUT_PATH}")


if __name__ == "__main__":
    main()

# Expected First Division table — Saturday 28 September 1974

Reference table used to verify `data/input/week-10-results.csv` (the frozen
"week 10" cutoff: all First Division matches played on or before Sat 28 Sep
1974, per `docs/requirements.md`).

## Source

- **Statto** (statto.com, site defunct since 2017), page titled
  *"English Division One (old) Table on Saturday 28th September 1974"*,
  via the Wayback Machine:
  `http://web.archive.org/web/20160401123205/http://www.statto.com/football/stats/england/division-one-old/1974-1975/table/full/1974-09-28`
- Accessed: 2026-07-11.

The originally planned source, `https://www.11v11.com/league-tables/league-division-one/28-September-1974/`,
was unreachable at access time: the live site returns HTTP 403 to
non-browser clients, that URL has no Wayback snapshot, and a save-page
request to the Wayback Machine failed (HTTP 520). See "Independent
cross-check" below for how 11v11 was still used.

## Table as shown by the source

| Pos | Team | P | W | D | L | GF | GA | GAvg | Pts |
|----:|------|--:|--:|--:|--:|---:|---:|-----:|----:|
| 1 | Ipswich Town | 10 | 8 | 0 | 2 | 18 | 6 | 3.00 | 16 |
| 2 | Manchester City | 10 | 6 | 2 | 2 | 14 | 11 | 1.27 | 14 |
| 3 | Liverpool | 10 | 6 | 1 | 3 | 17 | 8 | 2.12 | 13 |
| 4 | Everton | 10 | 4 | 5 | 1 | 14 | 11 | 1.27 | 13 |
| 5 | Sheffield United | 10 | 5 | 3 | 2 | 14 | 14 | 1.00 | 13 |
| 6 | Newcastle United | 9 | 5 | 2 | 2 | 16 | 13 | 1.23 | 12 |
| 7 | Middlesbrough | 9 | 4 | 3 | 2 | 12 | 7 | 1.71 | 11 |
| 8 | Derby County | 10 | 3 | 5 | 2 | 16 | 13 | 1.23 | 11 |
| 9 | Stoke City | 10 | 4 | 3 | 3 | 13 | 11 | 1.18 | 11 |
| 10 | Wolverhampton Wndrs | 10 | 3 | 5 | 2 | 12 | 11 | 1.09 | 11 |
| 11 | Carlisle United | 10 | 4 | 2 | 4 | 8 | 8 | 1.00 | 10 |
| 12 | West Ham United | 10 | 4 | 1 | 5 | 20 | 18 | 1.11 | 9 |
| 13 | Burnley | 10 | 4 | 1 | 5 | 17 | 18 | 0.94 | 9 |
| 14 | Birmingham City | 10 | 3 | 2 | 5 | 12 | 17 | 0.71 | 8 |
| 15 | Coventry City | 10 | 2 | 4 | 4 | 11 | 17 | 0.65 | 8 |
| 16 | Leicester City | 9 | 2 | 3 | 4 | 13 | 17 | 0.77 | 7 |
| 17 | Luton Town | 10 | 1 | 5 | 4 | 11 | 16 | 0.69 | 7 |
| 18 | Chelsea | 10 | 2 | 3 | 5 | 10 | 18 | 0.56 | 7 |
| 19 | Leeds United | 9 | 2 | 2 | 5 | 12 | 14 | 0.86 | 6 |
| 20 | Arsenal | 9 | 2 | 2 | 5 | 9 | 12 | 0.75 | 6 |
| 21 | Tottenham Hotspur | 9 | 3 | 0 | 6 | 11 | 15 | 0.73 | 6 |
| 22 | Queens Park Rangers | 10 | 1 | 4 | 5 | 8 | 13 | 0.61 | 6 |

Unequal Played counts (9 vs 10) are correct: six clubs had a postponed or
not-yet-played fixture as of the cutoff.

## Verification performed (2026-07-11, `scripts/audit_results.py`)

1. **Aggregates vs this table**: P, W, D, L, GF, GA, Pts computed from
   `data/input/week-10-results.csv` match this table exactly — all 22 teams,
   all 7 columns, zero discrepancies.
2. **Independent cross-check (different site)**: because results and this
   table share a source (Statto), the dataset's prefix up to 7 Sep 1974
   (66 matches) was additionally compared against 11v11's archived table
   dated 7 September 1974
   (`http://web.archive.org/web/20201213215254/https://www.11v11.com/league-tables/league-division-one/07-september-1974/`,
   accessed 2026-07-11): exact match, all 22 teams, all 7 columns.
3. **Sanity anchors from prior research** (docs/assignment research):
   Ipswich Town top with 8 wins from 10, two points clear of Manchester
   City; QPR, Tottenham, Arsenal and Leeds level on 6 points at the
   bottom — all confirmed by this table.

## Naming note

Statto's layout abbreviates one club as "Wolverhampton Wndrs" (11v11:
"Wolverhampton Wanderers"). Decision (2026-07-11): the dataset uses the
club's canonical name **Wolverhampton Wanderers** — "Wndrs" is a display
abbreviation, not a name. The normalisation is applied as an explicit
mapping (`NAME_NORMALISATIONS`) in `scripts/extract_results.py`, and the
same mapping is applied to Statto's reference table when comparing in
`scripts/audit_results.py`. The table above is shown verbatim as the
source displays it, abbreviation included.

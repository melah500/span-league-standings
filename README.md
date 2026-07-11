# SPAN League Standings

[![build](https://github.com/melah500/span-league-standings/actions/workflows/build.yml/badge.svg)](https://github.com/melah500/span-league-standings/actions/workflows/build.yml)

A Java 21 command-line application that computes the league table of the
1974/75 English First Division from CSV match results, using the competition
rules that were actually in force that season.

## The problem and the "week 10" interpretation

Given a file of match results, produce the league standings. The dataset is
frozen at **all First Division matches played on or before Saturday
28 September 1974** — the season's tenth Saturday, by which every club had
played 9 or 10 matches (six clubs had a postponed fixture; the unequal counts
are historically correct). The cutoff decision and both I/O formats are frozen
in [`docs/requirements.md`](docs/requirements.md).

**Data provenance.** The primary source planned for results
(englishfootballleaguetables.co.uk) was dead at access time, and several live
sites (11v11, worldfootball, FBref) block non-browser clients. The dataset was
instead extracted from Statto.com's per-date archives via the Wayback Machine,
then verified two ways: against Statto's own table dated exactly 28 September
1974 (all 22 teams, all of P/W/D/L/GF/GA/Pts, zero discrepancies), and — to
guard against a single-source error — the 66-match prefix up to 7 September
was independently checked against 11v11's archived table for that date. The
full audit trail, source URLs with Wayback timestamps, and the one name
normalisation ("Wolverhampton Wndrs" → "Wolverhampton Wanderers") are in
[`docs/expected-table-28-sep-1974.md`](docs/expected-table-28-sep-1974.md).
The result: [`data/input/week-10-results.csv`](data/input/week-10-results.csv)
— 107 matches, 22 teams. The generated table is committed at
[`data/output/week-10-standings.csv`](data/output/week-10-standings.csv) and
pinned by an end-to-end test.

## Historical rules (1974/75)

Researched and sourced in [`docs/rules-research.md`](docs/rules-research.md):

1. **Points**: 2 for a win, 1 for a draw, 0 for a loss (three-points-for-a-win
   arrived in England only in 1981).
2. **Tie-break**: **goal average** — goals for *divided by* goals against —
   not goal difference (which replaced it in 1976/77). Averages are compared
   by integer cross-multiplication in `long` arithmetic; no floating point
   anywhere. A team with goals scored and none conceded ranks above every
   finite average; a 0/0 goal record counts as average zero.
3. Then **goals for**, descending.
4. Then **team name**, ascending — an application-level fallback for
   deterministic output, documented as *not* a historical rule (the Football
   League shared positions; a CLI needs a total order).

## Prerequisites

Java 21. Nothing else — the Maven Wrapper downloads Maven itself, and the
application has zero runtime dependencies.

## Build

```sh
./mvnw verify
```

Compiles, runs all tests, and enforces the quality gates (see below).

## Run

```sh
./mvnw -q package

# file -> stdout
java -jar target/league-standings-1.0.0-SNAPSHOT.jar data/input/week-10-results.csv

# file -> file
java -jar target/league-standings-1.0.0-SNAPSHOT.jar data/input/week-10-results.csv standings.csv

# stdin -> stdout
cat data/input/week-10-results.csv | java -jar target/league-standings-1.0.0-SNAPSHOT.jar

# usage
java -jar target/league-standings-1.0.0-SNAPSHOT.jar --help
```

## Input contract

One match per line: `<home team> <home score>, <away team> <away score>`.
Team names may contain spaces; names containing commas are unsupported
(documented decision — the comma is the field separator). Blank lines are
skipped; CRLF is tolerated.

```
Lions 3, Snakes 1
Tarantulas 1, FC Awesome 0
Lions 1, FC Awesome 1
```

Malformed input fails fast on the first bad line, with the physical line
number and the offending text on stderr:

```
error: line 3: score must not be negative: "Lions -1, Snakes 1"
```

Rejected: missing/extra commas, missing scores, non-integer or negative
scores, scores that overflow `int`, empty team names, and self-matches
(`Lions 3, Lions 1`). I/O failures (missing input file, unwritable output)
also exit 1 with `error: ` and the JDK's message, which is the offending path.

## Output contract

`<rank>. <team>, <points> pts` (`pt` when exactly 1), ranked by the rules
above, one trailing newline, always `\n` regardless of platform:

```
1. Tarantulas, 6 pts
2. Lions, 5 pts
3. FC Awesome, 1 pt
```

Empty input produces empty output and exit 0.

## Exit codes

| Code | Meaning |
|-----:|---------|
| 0 | Success (including empty input) |
| 1 | Data or I/O error: malformed CSV, unreadable input, unwritable output |
| 2 | Usage error: more than two arguments |

The only recognised flag is `-h`/`--help`; any other argument is taken to be
a file path (an unreadable one exits 1).

## Architecture

Three layers with a one-way dependency rule, enforced by review (and by the
domain package importing nothing outside `java.*`):

- `domain` — `StandingsCalculator` (accumulation), `StandingsRanking`
  (the comparator above), `LeagueTable` (positions). Knows nothing of CSV or
  CLI.
- `csv` — `MatchResultCsvParser`, `StandingsCsvFormatter`,
  `CsvParseException` (carries the line number). Depends only on `domain`.
- `App` — argument handling, streams, exit codes. `Main` is a two-line
  untested wrapper around it, so the whole CLI is testable in-JVM.

No CSV library: the input is not RFC 4180 (no quoting, no escaping; fields
are `name score` halves split on a single comma), so a real CSV parser would
add a dependency while still needing all the same custom validation.
Deliberately out of scope (see [issue #2](https://github.com/melah500/span-league-standings/issues/2)):
commas in team names, duplicate-fixture detection in the CLI (the calculator
is a generic accumulator; duplicates are caught by the dataset audit script),
atomic output writes (a single ~600-byte `writeString`), error accumulation
beyond the first bad line, and any flags beyond `--help`.

## Quality gates

All bound to `./mvnw verify`, all enforced in CI on every push:

- **Spotless** (palantir-java-format) — needs the `--add-exports` flags in
  [`.mvn/jvm.config`](.mvn/jvm.config) on JDK 21, without which the formatter
  fails with `IllegalAccessError`.
- **Checkstyle** (10.18.2, pinned — the plugin's bundled default is stale)
  with the pragmatic rule set in [`config/checkstyle.xml`](config/checkstyle.xml).
- **JaCoCo** — report plus an enforced **80% line-coverage floor** (actual
  coverage at the time the floor was set: 93.8%; the untested code is the
  two-line `Main` wrapper and JDK-exception plumbing in `App`).
- **GitHub Actions** — `./mvnw verify` on Temurin 21 for every push and PR.

## AI collaboration

This assessment was built with Claude Code, with the process itself under
version control:

- [`CLAUDE.md`](CLAUDE.md) — standing instructions: mandatory TDD, the
  frozen reference documents, the architecture rule, git discipline.
- [`.claude/`](.claude/) — project slash commands (`/red` for the failing-test
  step, `/review` for a fresh-context diff review) and a `PostToolUse` hook
  that runs `spotless:apply` on every edited `.java` file.
- [`.mcp.json`](.mcp.json) — project-scoped GitHub MCP server used for
  checking CI status and filing the punch-list issues; its token is injected
  via the `GITHUB_TOKEN` environment variable and never committed.
- [`AI_REFLECTION.md`](AI_REFLECTION.md) — where the AI went wrong and how
  the process caught it.
- `ai/` — the exported session history, added as the final pre-submission
  step so it captures the complete record.

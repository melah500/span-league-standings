# Requirements Inventory — SPAN League Standings

Sources: `docs/assignment.pdf`, `docs/rules-research.md`. Status: **approved** (2026-07-11) with amendments.

## Frozen decisions

- **Language/stack: Java 21** — decided pre-session (candidate's daily stack and interview subject). Build: **Maven Wrapper** (`mvnw`) for a reproducible build with quality gates. Tests: **JUnit 5 + AssertJ**. Not open for revision.
- **"10th week" cutoff:** all First Division matches played **on or before Saturday 28 September 1974**. To be documented in README with sources. Consequence: unequal Played counts are expected and permitted (rules-research §1).
- **Input CSV row format (SPAN conventional):** one match per line —
  `Lions 3, Snakes 1` i.e. `<home team> <home score>, <away team> <away score>`.
  Team names may contain spaces; the score is the last token of each half.
- **Output format (SPAN conventional):** `1. Team Name, 13 pts` — rank, team, points, with correct pluralisation (`1 pt` / `n pts`).

## 1. Requirements by RFC 2119 keyword

### MUST / MUST NOT
- "Throughout this document, we use terms defined in RFC-2119; you MUST follow these definitions when completing this assignment."
- Input/output files: "You MUST include these files in your submission."
- "At SPAN we create automated tests you MUST include them in your submission."
- AI collaboration artifacts: "These artifacts are mandatory. Submissions missing any of these artifacts are incomplete and will not proceed past the coding test phase." — `CLAUDE.md`, `.claude/`, `./ai/` conversation history, `AI_REFLECTION.md`.
- "if you use some other AI you must submit equivalent artifacts" — N/A (using Claude Code).

### SHOULD / SHOULD NOT
- "The input and output SHOULD be text csv files. Either using stdin/stdout or taking filenames on the command."
- "The input SHOULD contain the results of games, one per line."
- "The output should be a league standing table in the conventional format." (lowercase — treated as SHOULD)
- "The rules for calculating league standings should follow those used by the English First Division in the season in question."
- "The solution should be written in one of the following languages: java, python, golang, C# (.Net Core on Linux) or scala."
- "If you use libraries installed by a common package manager (e.g pip), you SHOULD NOT commit the installed packages."

### MAY / prose expectations
- "you may need to do some research."
- Runs in a unix-ish environment (OS X); "Please use platform-agnostic constructs where possible."
- Document any complicated setup steps. "Be prepared to explain your solution during a review."
- "production-ready, command-line application"; AI-assistant collaboration is expected and evaluated.

## 2. SHOULD compliance — all comply, no deviations

| SHOULD | Decision & rationale |
|---|---|
| CSV in/out, stdin/stdout or filenames | Comply — filename args with stdin/stdout fallback; low cost, reviewer convenience |
| One game per line | Comply — SPAN conventional row format (frozen above) |
| Conventional table output | Comply — SPAN conventional `rank. Team, pts` format (frozen above); see open items for full-statistics variant |
| 1974/75 First Division rules | Comply — win 2 / draw 1 / loss 0; tie-break by **goal average** (GF/GA) via integer cross-multiplication (never floats), then goals for, then team name ascending — the alphabetical fallback documented as application-level, not historical (rules-research §8) |
| Listed language | Comply — Java 21 (frozen) |
| No committed packages | Comply — .gitignore excludes build output and dependencies; Maven Wrapper JAR is the standard, committed exception |

## 3. Assumptions (to document in README)

1. **Week-10 cutoff** = matches on or before Sat 28 Sep 1974 (frozen above), with sources cited.
2. **Input parsing:** no header row expected; blank lines skipped; malformed rows **fail fast with line numbers** in the error message.
3. **Goal-average handling:** ranking uses exact integer cross-multiplication; GA=0 with GF>0 ranks as positive infinity; 0/0 handled explicitly (rules-research §5). Display formatting is a separate concern from ranking.
4. Team names are the canonical 1974/75 First Division names; a team never plays itself; scores are non-negative (rules-research §9 validation rules enforced).

## 4. Open items

- **Output detail level:** conventional `rank. Team, pts` vs. fuller table (P W D L GF GA Pts). Default: SPAN conventional format, with full statistics computed and available. To be finalised when the CSV contracts are defined.

## 5. Deliverables checklist

- [ ] `README.md` — setup/run instructions, cutoff rationale + data sources
- [ ] `CLAUDE.md` — project root
- [ ] `AI_REFLECTION.md` — project root
- [ ] `.claude/` — committed (verify not gitignored)
- [ ] `ai/` — copy of `~/.claude/projects/<this-project>/`, done as the **final** step before submission
- [ ] Java 21 source + CLI entry point; Maven Wrapper committed
- [ ] Automated tests (JUnit 5 + AssertJ), incl. rules-research §9 validation rules and the Liverpool 60/39 vs Ipswich 66/44 goal-average example
- [ ] **Input CSV** (real week-10 1974/75 results) and **output CSV** (generated standings), both committed
- [ ] No committed dependencies (build output, local repo)

## 6. Risks, most likely to cost time first

1. **Sourcing/transcribing accurate week-10 1974 results** (~50–90 matches) — cross-check at least two sources.
2. **Forgetting the `ai/` export** — must be the last step; missing it makes the submission incomplete.
3. **`.claude/` accidentally gitignored** — verify early.
4. **Goal-average edge cases** (GA=0, floats) corrupting order — mitigated by cross-multiplication + tests.
5. **Over-engineering "production-ready"** — timebox; it means validation, clear errors, tests, docs — not frameworks.
6. **24h session/context drift** — commit early and often; record decisions in `CLAUDE.md`.
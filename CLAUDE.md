# SPAN League Standings — Project Instructions

## Objective
Production-ready Java 21 CLI that computes 1974/75 English First Division league
standings from CSV match results. This is a timed coding assessment: quality of
process (TDD, small commits, documented decisions) is evaluated alongside the code.

## Authoritative references
- `docs/requirements.md` — frozen decisions (stack, cutoff date, CSV formats).
- `docs/rules-research.md` — historical 1974/75 rules (points, goal average, validation).

Never contradict these documents. Never fabricate historical rules or match data —
if a historical fact is unknown, say so and ask.

## Stack
Java 21, Maven Wrapper (`./mvnw`), JUnit 5 + AssertJ.
No Spring, no Lombok, no speculative dependencies.

## Process
- **TDD is mandatory**: red → green → refactor. Never write production code without
  a failing test. Never delete or weaken a test to get to green.
- Run `./mvnw verify` after meaningful changes and before declaring any milestone complete.

## Architecture
- Domain layer (standings calculation, ranking) must not depend on CSV parsing or CLI code.
- Ranking: points desc → goal average desc (integer cross-multiplication, never floats)
  → goals for desc → team name asc (documented application-level fallback, not a
  historical rule).
- Output is deterministic, always. Validation failures report line numbers.

## Git
- Never commit without explicit approval. Never rewrite history.
- Never commit secrets or absolute local paths.

## Documentation
- Update `docs/` when contracts (CSV formats, CLI interface, ranking) change.

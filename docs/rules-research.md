ENGLISH FIRST DIVISION 1974/75 LEAGUE RULES
================================================

Purpose
-------
These rules are intended for calculating a football league table for the
English First Division during the 1974/75 season.

1. COMPETITION FORMAT
---------------------
- The division contained 22 clubs.
- Each club played every other club twice:
  - Once at home
  - Once away
- Each club therefore played 42 league matches in a complete season.

For a partial-season table, such as the table after week 10, calculate the
standings only from the match results supplied. Do not assume that every team
has played the same number of matches.

2. POINTS SYSTEM
----------------
The 1974/75 First Division used the following points system:

- Win:  2 points
- Draw: 1 point
- Loss: 0 points

Formula:

Points = (Wins x 2) + Draws

Example:

Played: 10
Won:     5
Drawn:   3
Lost:    2

Points = (5 x 2) + 3
       = 13

3. GOAL AVERAGE
---------------
Teams level on points were separated using goal average, not goal difference.

Goal average formula:

Goal Average = Goals For / Goals Against

The team with the higher goal average ranks above the other team.

Example:

Team A:
Goals For:     20
Goals Against: 10
Goal Average:  20 / 10 = 2.000

Team B:
Goals For:     25
Goals Against: 14
Goal Average:  25 / 14 = 1.786

Team A ranks above Team B because 2.000 is greater than 1.786.

This differs from modern goal difference:

Goal Difference = Goals For - Goals Against

In the example above:

Team A goal difference = +10
Team B goal difference = +11

Under modern rules, Team B would rank higher. Under the 1974/75 goal-average
rule, Team A ranks higher.

4. SAFE GOAL-AVERAGE COMPARISON
-------------------------------
Avoid using floating-point values when comparing goal averages because rounding
can produce incorrect ordering.

To compare:

Team A = GoalsForA / GoalsAgainstA
Team B = GoalsForB / GoalsAgainstB

Cross-multiply:

GoalsForA x GoalsAgainstB

and compare it with:

GoalsForB x GoalsAgainstA

Example:

Team A = 20 / 10
Team B = 25 / 14

20 x 14 = 280
25 x 10 = 250

Because 280 is greater than 250, Team A has the better goal average.

Use a sufficiently large numeric type, such as long in Java, when multiplying.

5. ZERO GOALS AGAINST
---------------------
A partial-season table may contain a team that has conceded zero goals.

Recommended handling:

- If Goals For > 0 and Goals Against = 0:
  Treat the goal average as positive infinity for ranking purposes.

- If Goals For = 0 and Goals Against = 0:
  Handle this case explicitly as a zero-goal record.

- Otherwise:
  Compare Goals For / Goals Against normally.

Keep ranking logic separate from display formatting. The assignment's output
specification should determine how such a value is shown in the CSV.

6. TABLE COLUMNS
----------------
A calculated league table will normally contain:

- Position
- Team
- Played
- Won
- Drawn
- Lost
- Goals For
- Goals Against
- Points

Useful abbreviations include:

- P   = Played
- W   = Won
- D   = Drawn
- L   = Lost
- GF  = Goals For
- GA  = Goals Against
- Pts = Points

7. UPDATING THE TABLE FROM A MATCH
----------------------------------
Example result:

Liverpool 3-1 Arsenal

Liverpool receives:

- Played:       +1
- Won:          +1
- Goals For:    +3
- Goals Against:+1
- Points:       +2

Arsenal receives:

- Played:       +1
- Lost:         +1
- Goals For:    +1
- Goals Against:+3
- Points:       +0

For a draw, both teams receive:

- Played: +1
- Drawn:  +1
- Points: +1

8. RANKING ORDER
----------------
The main ranking order should be:

1. Points, descending
2. Goal average, descending (compare via cross-multiplication, never rounded floats)
3. Goals For, descending
4. Team name, ascending

Points, goal average and goals scored were the official classification
criteria for the 1974/75 First Division. The alphabetical fallback is an
application-level decision for stable, deterministic output — it is not a
historical sporting rule and should be documented as such.

9. VALIDATION RULES
-------------------
The following checks are useful for automated tests:

For each team:

Played = Won + Drawn + Lost

Across the complete table:

Total Goals For = Total Goals Against

For all processed matches:

Sum of Played across all teams = Number of Matches x 2

Also verify that:

- Scores are not negative.
- Team names are not blank.
- A team is not recorded as playing against itself.
- Malformed CSV rows are rejected or handled according to the specification.

10. HISTORICAL EXAMPLE
----------------------
Derby County won the 1974/75 English First Division.

Liverpool and Ipswich Town both finished with 51 points. Liverpool ranked above
Ipswich because Liverpool had the higher goal average.

Liverpool:

60 / 39 = approximately 1.538

Ipswich Town:

66 / 44 = 1.500

This is a useful historical example of the goal-average tie-break being applied.

11. SUMMARY
-----------
Scoring:

- Win  = 2 points
- Draw = 1 point
- Loss = 0 points

Ranking:

1. Points
2. Goal average
3. Goals scored
4. Alphabetical (documented application-level fallback)

Goal average:

Goals For / Goals Against

Do not use modern three-points-for-a-win or goal-difference rules when
calculating the 1974/75 English First Division standings.

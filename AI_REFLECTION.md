# AI Reflection

## Where the AI went wrong, and what caught it

The most instructive failure of this project was not a bug in production code
— it was the AI repeatedly reaching for football rules that did not exist yet
in 1974.

I had reason to anticipate this. Before any code was written, I had the AI
research the 1974/75 competition rules into `docs/rules-research.md` precisely
because I expected its training data to be saturated with the modern game:
three points for a win (1981), goal difference (1976/77). The research phase
itself surfaced the first corrections — early drafts of the ranking discussion
drifted toward goal difference before the sourced document pinned goal
average, and the goals-for tie-break had to be traced to what the Football
League actually used rather than what sounded plausible. The rules document
became the project's constitution for exactly this reason.

The reflex still got through — twice, and both times in the same place: test
fixtures. In the formatter cycle, the AI wrote an expected table giving a team
four points for a win and a draw — modern scoring — when the season's answer
is three. In the CLI cycle, it asserted that a team winning 3–1 would top a
table over a team winning 1–0; under goal difference that is a reasonable
intuition, but under goal average a clean sheet is an infinite average and
ranks first. What I find notable is the direction of the failures: the
production code, driven by tests derived from the research document, was
correct both times. It was the AI's *new* fixtures that imported the modern
game, and the previously verified implementation rejected them. The
discipline of freezing researched rules into a document the AI is instructed
never to contradict turned out to matter less for writing code than for
checking the AI's own examples.

The second lesson is about instruction fidelity. TDD was a hard rule in
`CLAUDE.md`: no production code without a failing test. In the parser cycle
the AI nonetheless wrote the full set of error-handling branches while
implementing the happy path — so when the twelve rejection tests arrived in
the next cycle, every one passed immediately and the red step never fired.
Those code paths were, in the end, only proven by tests written after the
fact. To its credit, the AI noticed and reported the violation itself rather
than presenting the green suite as a success story, which is why the episode
is documented here instead of discovered by a reviewer. My takeaway as the
engineer is that "minimum code to pass" is the instruction a code-generating
model finds hardest to obey — completeness is its default — and that the
cheap countermeasure is exactly what caught everything else: fresh-context
review of each diff, and refusing to let the AI grade its own homework.

Smaller frictions reinforced the same theme of automation cutting both ways:
the `PostToolUse` hook that auto-formats every edited file once stripped
imports between two sequential edits, because the code using those imports
had not been written yet when the formatter ran. A convenience built to
enforce discipline briefly destroyed work mid-task.

What I would keep from this workflow: researched constraints frozen into
documents the AI must not contradict; plan-mode design sessions before
contract-heavy layers; separate fresh-context review; and matching the model
to the phase — a heavier model for research, design and the golden end-to-end
verification, a faster one for well-specified execution. What I would change:
I would write the fixture *expectations* myself for any domain where the AI's
priors are known to be wrong. The AI is an excellent implementer of rules it
is told; it is an unreliable witness to rules it thinks it remembers.

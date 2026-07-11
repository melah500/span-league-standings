---
name: project-ai-reflection-notes
description: Candidate moments for AI_REFLECTION.md — instances where AI took a wrong direction or exhibited interesting behaviour worth documenting
metadata: 
  node_type: memory
  type: project
  originSessionId: 812652eb-b465-468c-a0be-a36a70b06b43
---

Candidate moments for AI_REFLECTION.md:

1. **Spotless hook stripping imports** (earlier session): PostToolUse hook ran `spotless:apply` after a first Edit while the second Edit (that would use those imports) hadn't been written yet — Spotless removed the temporarily-unused imports. Required manual re-add. Illustrates that automated formatting hooks need to account for multi-step edits.

2. **Fixture awarded 2 pts for a draw** (cycle 1 formatter test, 2026-07-11): The multi-entry test fixture expected `Lions, 4 pts` from a win + draw — the modern-rules reflex (win=3, draw=1 → 4 pts). The 1974/75 rule is win=2, draw=1, so Lions should have 3 pts. The rule docs in docs/rules-research.md exist precisely to guard against this. AI caught and self-corrected before green, but the error appeared in the first draft of the test.

3. **Transitivity fixture not actually testing average level** (cycle 5, earlier session): All records in the first transitivity test had goal average 2.000, so the average tier never decided any pair. The test name was misleading. Fresh-context review agent caught this; fixture was fixed to use 21/10 so average actually decides at least one pair.

4. **Cycle 3 rejection tests had no RED phase** (2026-07-11): When implementing the parser in cycle 2 (happy path), AI wrote all error-handling branches upfront rather than limiting scope to the minimum for green. When cycle 3 rejection tests were written, all 12 passed immediately — the RED step never fired. This is a TDD discipline failure: production error-handling code was written ahead of its tests, meaning those code paths were never proven to be needed by a failing test.

**Why:** Document these in AI_REFLECTION.md as evidence of where AI collaboration added risk or corrected itself, and how the TDD + review process caught errors.

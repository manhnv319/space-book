# Flow-diagram requirement for pull requests

## Purpose

Make behavior-changing work reviewable through one validated, interactive diagram instead of relying on prose alone. This requirement applies to both human-authored and agent-authored pull requests.

## When a diagram is required

A pull request MUST include an Archify diagram when it:

- introduces a feature with a user, system, data, deployment, asynchronous, authorization, error, or state-transition flow;
- changes an existing flow or its ordering, decision points, handoffs, trust boundaries, external calls (e.g. VNPay, Web Push, SMTP), or persistent state (MongoDB documents, Redis cache/denylist); or
- changes an architecture boundary that affects how a request or event moves through the system (e.g. Hexagonal ports, REST endpoints, Spring Security filter chain).

A diagram is not required for isolated copy, styling, dependency, test-only, or documentation-only changes with no behavior-flow impact. The PR description MUST state:
```text
Diagram: N/A — no behavior-flow impact
```

## Required procedure

1. Read `.claude/skills/archify/SKILL.md` and choose the diagram type that makes the changed behavior easiest to review:
   - `workflow`: CI/CD pipelines, release approvals, or multi-actor feature business paths;
   - `sequence`: ordered request/response interactions (e.g. VNPay callback, login JWT refresh);
   - `architecture`: component and trust-boundary changes (e.g. Frontend $\rightarrow$ Backend $\rightarrow$ Mongo / Redis);
   - `dataflow`: data movement, transactions, and event streaming; or
   - `lifecycle`: entity states, retries, cancellation, and terminal outcomes (e.g. Order / Rental status transitions).
2. Create or update a stable source file in `docs/architecture/` named `<scope>.<type>.json`. Keep the rendered HTML beside it as `<scope>.<type>.html` and retain a desktop light-theme PNG preview as `<scope>.<type>.png`.
3. Keep the diagram bounded to the changed flow: primary path first, decision or failure branches only when they affect the change, and no unrelated topology.
4. Validate and deliver the artifact with Archify at `showcase` quality:
   ```bash
   node .claude/skills/archify/bin/archify.mjs deliver <type> docs/architecture/<scope>.<type>.json docs/architecture/<scope>.<type>.html --quality showcase
   ```
5. Run Archify `visual-check` against the delivered HTML, retain its 1440×900 light-theme capture as the PNG preview, then visually review the delivered HTML at desktop size:
   ```bash
   node .claude/skills/archify/bin/archify.mjs visual-check docs/architecture/<scope>.<type>.html
   ```
   Resolve every reported validation error before creating or updating the PR.

## PR description contract

A flow-affecting PR MUST contain this section in its description:

```markdown
## Flow diagram

![<scope> flow diagram](https://github.com/manhnv319/space-book/raw/<head-sha>/docs/architecture/<scope>.<type>.png)

- **Type:** `<workflow | sequence | architecture | dataflow | lifecycle>`
- **Scope:** `<the behavior changed by this PR>`
- **Interactive:** [Open HTML diagram](https://github.com/manhnv319/space-book/blob/<head-sha>/docs/architecture/<scope>.<type>.html)
- **Source:** [JSON specification](https://github.com/manhnv319/space-book/blob/<head-sha>/docs/architecture/<scope>.<type>.json)
- **Verification:** Archify `showcase` validation and `visual-check` passed; rendered HTML visually reviewed.
```

Use the PR head SHA in the links so reviewers see the exact diagram revision being reviewed. Update the description whenever a later commit changes the diagram or the modeled flow.

## Review gate

Reviewers (both humans and code review agents) MUST request changes when a flow-affecting PR lacks the embedded PNG and section above, links an unvalidated artifact, or the diagram contradicts the implementation. The diagram supports review; it does not replace changed-contract tests or the repository CI gates.

## Summary
<!-- Brief description of the changes introduced in this PR -->
- 

## Flow diagram
<!--
REQUIRED for all flow-affecting PRs per docs/architecture/pr-flow-diagrams.md.
If this PR has NO behavior-flow impact (copy, styling, dependency, or test-only changes), replace this section with:
Diagram: N/A — no behavior-flow impact
-->

![<scope> flow diagram](https://github.com/manhnv319/space-book/raw/<head-sha>/docs/architecture/<scope>.<type>.png)

- **Type:** `<workflow | sequence | architecture | dataflow | lifecycle>`
- **Scope:** `<the behavior changed by this PR>`
- **Interactive:** [Open HTML diagram](https://github.com/manhnv319/space-book/blob/<head-sha>/docs/architecture/<scope>.<type>.html)
- **Source:** [JSON specification](https://github.com/manhnv319/space-book/blob/<head-sha>/docs/architecture/<scope>.<type>.json)
- **Verification:** Archify `showcase` validation and `visual-check` passed; rendered HTML visually reviewed.

## Test plan
- [ ] Unit and architecture tests pass (`./mvnw test` for backend, `npm run test:unit` for frontend)
- [ ] Lint and type checks pass (`npm run lint && npm run typecheck`)
- [ ] Production build succeeds (`next build` and `./mvnw package -DskipTests`)
- [ ] Archify diagram validated at `showcase` quality (if flow-affecting)

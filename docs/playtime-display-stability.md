# Playtime display and command stability

## Requirement and implementation

A single `/playtime <player>` request should return a result after a cold asynchronous read. Display consumers should retain last successfully loaded values while refreshes run, without publishing an obsolete read after invalidation. Failed SQL reads must remain distinguishable from successful empty results.

The command waits for its target's refresh on the server scheduler, with a bounded timeout and runtime/player validity checks. Player and leaderboard caches retain loaded values during invalidation; revisions and generations prevent stale refreshes from publishing, and strict repository reads preserve values on SQL failure. Pending write totals remain included in display reads.

## Verification and review

- Before implementation, the command integration test reproduced the cold-cache retry message, and the display regression showed an obsolete read replacing 5,000 minutes with 2.
- `mvn clean verify` on the parent main baseline passed 253 tests, with zero failures, errors, or skips, using Java 21 and the pinned EnthusiaStaff API.
- The command fix was installed on TEST and the user confirmed it works. The combined fix was installed on TEST, but hologram, TAB ordering, and nametag visual acceptance remains outstanding. TEST has existing duplicate EnthusiaDisplay plugin files; the server selected version 0.1.10.
- No production deployment of these changes has been performed. Initial cold cache loads still require an asynchronous read, and display values can temporarily be stale during a refresh or database outage.

# Numeral Discord roles: current evidence and test plan

The Google task-list entry is under the Playtime section of the Enthusia SMP master task list.

## Local evidence

- Requirements NR-01 through NR-06 recorded before implementation.
- `NumeralRolePolicyTest`, `NumeralRoleSyncServiceTest`, and `NumeralDiscordConfigTest` each failed to compile before their respective implementation was added, then passed.
- `DiscordSrvNumeralGateway` compiles against DiscordSRV 1.28.0 as a provided soft dependency.
- The config is disabled by default and rejects incomplete role ID mappings when enabled.
- A clean `mvn verify` completed with 204 tests, 0 failures, 0 errors, and 0 skipped. The existing tier-initialization race test was made to exercise its retry path under suite load; the same fix is already present in the separate `/seen` work.

## Remaining implementation

- Connect the gateway and sync service to DiscordSRV link/unlink events, accepted active-minute tier changes, plugin startup/reload, and a bounded retry loop.
- Ensure failed unlink revocations survive restart or are repaired by a safe authoritative reconciliation.
- Verify the role policy and provisioning choice, then activate only with actual configured role IDs.

## Test server checks

- Link and unlink Java and Floodgate accounts; confirm role ownership follows UUID rather than username.
- Advance active minutes across numeral thresholds; check highest-only or cumulative behavior as chosen.
- Restart during queued role changes and Discord outage; confirm eventual convergence without removing unrelated roles.
- Check missing role IDs, bot role hierarchy, Discord member absence, and rate limits.

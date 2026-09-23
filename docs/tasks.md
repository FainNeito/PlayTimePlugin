# SPEAR work: numeral Discord roles

1. **Spec:** Verify the active-playtime tier source, DiscordSRV account-link API, role ownership, and unresolved role policy. Requirements NR-01 through NR-06.
2. **Prove:** Write focused tests for pure tier-to-role selection, link/unlink identity, stale-role cleanup, failed reads, and retry behavior before runtime wiring.
3. **Engine:** Add an opt-in DiscordSRV gateway and role reconciler; use current configured numeral thresholds and UUID-based links.
4. **Arch:** Wire tier advancement, account-link events, startup/reload reconciliation, and a bounded retry queue without running Discord API calls on the server thread.
5. **Refine:** Run focused tests, full clean build, packaging, and test-server checks with actual DiscordSRV, JDA permissions, Java and Bedrock links, restart, unlink, and outage recovery.

Current state: Spec, red/green tests for tier policy, link reconciliation, unlink cleanup, failed reads, and configuration, plus an opt-in DiscordSRV/JDA gateway are implemented on an isolated branch. The role selection and provisioning choices were requested from the server owner; highest-only and configured role IDs are provisional defaults. Lifecycle wiring, periodic retry/recovery, live validation, and artifact delivery remain open.

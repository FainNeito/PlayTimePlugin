# Playtime numeral Discord roles (SPEAR)

- **NR-01:** WHEN an authoritative active-playtime total establishes a numeral tier, THE SYSTEM SHALL derive the Discord numeral role entitlement from the same configured tier catalog used by the in-game numeral display. Idle, AFK, and total connected minutes SHALL NOT advance this entitlement.
- **NR-02:** WHEN a Minecraft UUID is linked to a Discord account through the configured account-link provider, THE SYSTEM SHALL reconcile the Discord member's managed numeral roles to the current entitlement. A username SHALL NOT be used as the account key.
- **NR-03:** WHEN a link is removed, THE SYSTEM SHALL revoke only the numeral roles managed by this integration from the Discord account captured by the unlink event, even after the provider's UUID-to-Discord mapping is gone.
- **NR-04:** WHEN a player advances, links, joins, restarts, or the integration recovers, THE SYSTEM SHALL eventually reconcile managed numeral roles without creating duplicate grants or affecting unrelated Discord roles.
- **NR-05:** IF authoritative playtime, the account-link provider, Discord, role permissions, or role configuration is unavailable, THE SYSTEM SHALL retain pending work or retry later and SHALL NOT infer zero playtime or remove roles based on a failed read.
- **NR-06:** WHEN account linking is absent or role configuration is incomplete, THE SYSTEM SHALL leave numeral role synchronization disabled without affecting playtime accrual or the existing numeral display and announcements.

Role selection policy (highest-only versus cumulative) and role provisioning (configured IDs versus automatic creation) are awaiting the server owner's choice. Implementation must keep both choices explicit and avoid publishing a default before that decision.

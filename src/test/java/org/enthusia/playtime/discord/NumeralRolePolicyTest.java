package org.enthusia.playtime.discord;

import org.enthusia.playtime.util.NumeralTierCatalog;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NumeralRolePolicyTest {
    private final NumeralTierCatalog catalog = new NumeralTierCatalog(
            java.util.List.of(new NumeralTierCatalog.Tier("I", 60, "gray"),
                    new NumeralTierCatalog.Tier("II", 480, "white"),
                    new NumeralTierCatalog.Tier("III", 1200, "green")));
    private final Map<String, String> roleIds = Map.of("I", "101", "II", "102", "III", "103");

    @Test void highestOnlyUsesAuthoritativeActiveMinuteThreshold() {
        NumeralRolePolicy policy = new NumeralRolePolicy(catalog, roleIds, NumeralRolePolicy.Mode.HIGHEST_ONLY);
        assertEquals(Set.of(), policy.desiredRoles(59));
        assertEquals(Set.of("101"), policy.desiredRoles(60));
        assertEquals(Set.of("102"), policy.desiredRoles(480));
        assertEquals(Set.of("103"), policy.desiredRoles(1200));
    }

    @Test void cumulativePolicyKeepsAllEarnedTiers() {
        NumeralRolePolicy policy = new NumeralRolePolicy(catalog, roleIds, NumeralRolePolicy.Mode.CUMULATIVE);
        assertEquals(Set.of("101", "102"), policy.desiredRoles(480));
    }

    @Test void reconciliationChangesOnlyManagedRoles() {
        NumeralRolePolicy policy = new NumeralRolePolicy(catalog, roleIds, NumeralRolePolicy.Mode.HIGHEST_ONLY);
        NumeralRolePolicy.Change change = policy.reconcile(Set.of("101", "unrelated"), 480);
        assertEquals(Set.of("102"), change.grant());
        assertEquals(Set.of("101"), change.revoke());
        assertFalse(change.revoke().contains("unrelated"));
    }

    @Test void incompleteOrDuplicateRoleMappingIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new NumeralRolePolicy(catalog, Map.of("I", "101"), NumeralRolePolicy.Mode.HIGHEST_ONLY));
        assertThrows(IllegalArgumentException.class, () -> new NumeralRolePolicy(catalog,
                Map.of("I", "101", "II", "101", "III", "103"), NumeralRolePolicy.Mode.HIGHEST_ONLY));
    }
}

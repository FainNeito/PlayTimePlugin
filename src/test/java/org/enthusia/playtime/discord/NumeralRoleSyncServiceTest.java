package org.enthusia.playtime.discord;

import org.enthusia.playtime.util.NumeralTierCatalog;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class NumeralRoleSyncServiceTest {
    private final UUID player = UUID.randomUUID();
    private final NumeralRolePolicy policy = new NumeralRolePolicy(
            new NumeralTierCatalog(java.util.List.of(new NumeralTierCatalog.Tier("I", 60, "gray"),
                    new NumeralTierCatalog.Tier("II", 480, "white"))),
            Map.of("I", "101", "II", "102"), NumeralRolePolicy.Mode.HIGHEST_ONLY);

    @Test void linkedPlayerGetsCurrentTierWithoutTouchingUnrelatedRoles() {
        FakeRoles roles = new FakeRoles(Set.of("101", "staff"));
        NumeralRoleSyncService service = new NumeralRoleSyncService(policy, uuid -> "discord-1", uuid -> 480L, roles);
        service.reconcile(player).join();
        assertEquals(Set.of("102", "staff"), roles.roles);
    }

    @Test void failedAuthoritativeReadDoesNotRemoveRoles() {
        FakeRoles roles = new FakeRoles(Set.of("101"));
        NumeralRoleSyncService service = new NumeralRoleSyncService(policy, uuid -> "discord-1", uuid -> { throw new IllegalStateException("DB down"); }, roles);
        assertThrows(Exception.class, () -> service.reconcile(player).join());
        assertEquals(Set.of("101"), roles.roles);
    }

    @Test void unlinkRevokesCapturedDiscordIdentityAfterMappingIsGone() {
        FakeRoles roles = new FakeRoles(Set.of("101", "staff"));
        NumeralRoleSyncService service = new NumeralRoleSyncService(policy, uuid -> null, uuid -> 999L, roles);
        service.unlink("discord-1").join();
        assertEquals(Set.of("staff"), roles.roles);
    }

    private static final class FakeRoles implements NumeralRoleSyncService.RoleGateway {
        final Set<String> roles;
        FakeRoles(Set<String> initial) { roles = new HashSet<>(initial); }
        public CompletableFuture<Set<String>> currentRoles(String discordId) { return CompletableFuture.completedFuture(Set.copyOf(roles)); }
        public CompletableFuture<Void> grant(String discordId, String roleId) { roles.add(roleId); return CompletableFuture.completedFuture(null); }
        public CompletableFuture<Void> revoke(String discordId, String roleId) { roles.remove(roleId); return CompletableFuture.completedFuture(null); }
    }
}

package org.enthusia.playtime.discord;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Reconciles a Discord member from an authoritative playtime snapshot. */
public final class NumeralRoleSyncService {
    @FunctionalInterface public interface LinkProvider { String discordId(UUID uuid) throws Exception; }
    @FunctionalInterface public interface ActiveMinutes { long read(UUID uuid) throws Exception; }
    public interface RoleGateway {
        CompletableFuture<Set<String>> currentRoles(String discordId);
        CompletableFuture<Void> grant(String discordId, String roleId);
        CompletableFuture<Void> revoke(String discordId, String roleId);
    }

    private final NumeralRolePolicy policy;
    private final LinkProvider links;
    private final ActiveMinutes playtime;
    private final RoleGateway roles;

    public NumeralRoleSyncService(NumeralRolePolicy policy, LinkProvider links,
                                  ActiveMinutes playtime, RoleGateway roles) {
        this.policy = Objects.requireNonNull(policy);
        this.links = Objects.requireNonNull(links);
        this.playtime = Objects.requireNonNull(playtime);
        this.roles = Objects.requireNonNull(roles);
    }

    public CompletableFuture<Void> reconcile(UUID uuid) {
        try {
            String discordId = links.discordId(uuid);
            if (discordId == null) return CompletableFuture.completedFuture(null);
            long active = playtime.read(uuid);
            if (active < 0) throw new IllegalStateException("Authoritative active playtime is unavailable");
            return roles.currentRoles(discordId).thenCompose(current -> apply(discordId, policy.reconcile(
                    Objects.requireNonNull(current, "Discord member roles unavailable"), active)));
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    /** The captured Discord ID survives removal of the UUID-to-Discord link. */
    public CompletableFuture<Void> unlink(String discordId) {
        if (discordId == null || discordId.isBlank()) return CompletableFuture.completedFuture(null);
        return roles.currentRoles(discordId).thenCompose(current -> apply(discordId,
                policy.reconcile(Objects.requireNonNull(current, "Discord member roles unavailable"), 0)));
    }

    private CompletableFuture<Void> apply(String discordId, NumeralRolePolicy.Change change) {
        CompletableFuture<Void> work = CompletableFuture.completedFuture(null);
        for (String roleId : change.revoke()) {
            work = work.thenCompose(ignored -> roles.revoke(discordId, roleId));
        }
        for (String roleId : change.grant()) {
            work = work.thenCompose(ignored -> roles.grant(discordId, roleId));
        }
        return work;
    }
}

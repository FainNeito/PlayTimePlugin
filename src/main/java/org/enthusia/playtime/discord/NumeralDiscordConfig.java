package org.enthusia.playtime.discord;

import org.bukkit.configuration.ConfigurationSection;
import org.enthusia.playtime.util.NumeralTierCatalog;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public record NumeralDiscordConfig(NumeralRolePolicy policy, NumeralRolePolicy.Mode mode) {
    public static Optional<NumeralDiscordConfig> load(ConfigurationSection config, NumeralTierCatalog catalog) {
        String base = "numerals.discord-roles";
        if (!config.getBoolean(base + ".enabled", false)) return Optional.empty();
        String configuredMode = config.getString(base + ".mode", "highest-only").toLowerCase(Locale.ROOT);
        NumeralRolePolicy.Mode mode = switch (configuredMode) {
            case "highest-only" -> NumeralRolePolicy.Mode.HIGHEST_ONLY;
            case "cumulative" -> NumeralRolePolicy.Mode.CUMULATIVE;
            default -> throw new IllegalArgumentException("Unknown numeral Discord role mode: " + configuredMode);
        };
        Map<String, String> ids = new HashMap<>();
        for (NumeralTierCatalog.Tier tier : catalog.tiers()) {
            ids.put(tier.label(), config.getString(base + ".role-ids." + tier.label(), "").trim());
        }
        return Optional.of(new NumeralDiscordConfig(new NumeralRolePolicy(catalog, ids, mode), mode));
    }
}

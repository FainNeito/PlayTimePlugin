package org.enthusia.playtime.discord;

import org.bukkit.configuration.ConfigurationSection;
import org.enthusia.playtime.util.NumeralTierCatalog;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public record NumeralDiscordConfig(NumeralRolePolicy policy) {
    public static Optional<NumeralDiscordConfig> load(ConfigurationSection config, NumeralTierCatalog catalog) {
        String base = "numerals.discord-roles";
        if (!config.getBoolean(base + ".enabled", false)) return Optional.empty();
        String configuredMode = config.getString(base + ".mode", "highest-only").toLowerCase(Locale.ROOT);
        if (!configuredMode.equals("highest-only")) {
            throw new IllegalArgumentException("Numeral Discord roles require highest-only mode: " + configuredMode);
        }
        Map<String, String> ids = new HashMap<>();
        for (NumeralTierCatalog.Tier tier : catalog.tiers()) {
            ids.put(tier.label(), config.getString(base + ".role-ids." + tier.label(), "").trim());
        }
        return Optional.of(new NumeralDiscordConfig(new NumeralRolePolicy(catalog, ids)));
    }
}

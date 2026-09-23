package org.enthusia.playtime.discord;

import org.bukkit.configuration.file.YamlConfiguration;
import org.enthusia.playtime.util.NumeralTierCatalog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumeralDiscordConfigTest {
    @Test void disabledByDefaultAndRejectsIncompleteEnablement() {
        YamlConfiguration yaml = new YamlConfiguration();
        assertTrue(NumeralDiscordConfig.load(yaml, new NumeralTierCatalog(NumeralTierCatalog.defaultTiers())).isEmpty());
        yaml.set("numerals.discord-roles.enabled", true);
        assertThrows(IllegalArgumentException.class,
                () -> NumeralDiscordConfig.load(yaml, new NumeralTierCatalog(NumeralTierCatalog.defaultTiers())));
    }

    @Test void loadsExplicitModeAndRoleIds() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("numerals.discord-roles.enabled", true);
        yaml.set("numerals.discord-roles.mode", "cumulative");
        yaml.set("numerals.discord-roles.role-ids.I", "101");
        NumeralTierCatalog catalog = new NumeralTierCatalog(java.util.List.of(new NumeralTierCatalog.Tier("I", 60, "gray")));
        assertEquals(NumeralRolePolicy.Mode.CUMULATIVE, NumeralDiscordConfig.load(yaml, catalog).orElseThrow().mode());
    }
}

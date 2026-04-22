package com.magiccarpet;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public record CarpetConfig(
        int radius,
        Material edgeMaterial,
        Material centerMaterial,
        boolean useCenterBlock,
        double descendPerTick,
        double ascendPerJump,
        int ascendTicksPerJump,
        boolean onlyAirPlacement,
        String enabledMessage,
        String disabledMessage,
        String noPermissionMessage) {

    public static CarpetConfig from(FileConfiguration config) {
        int size = Math.max(1, config.getInt("carpet.size", 5));
        if (size % 2 == 0) {
            size++;
        }

        Material edgeMaterial = readMaterial(config.getString("carpet.edge-material"), Material.LIGHT_BLUE_STAINED_GLASS);
        Material centerMaterial = readMaterial(config.getString("carpet.center-material"), Material.GLOWSTONE);

        return new CarpetConfig(
                size / 2,
                edgeMaterial,
                centerMaterial,
                config.getBoolean("carpet.use-center-block", true),
                Math.max(0.05D, config.getDouble("movement.descend-per-tick", 0.2D)),
                Math.max(0.1D, config.getDouble("movement.ascend-per-jump", 1.0D)),
                Math.max(1, config.getInt("movement.ascend-ticks-per-jump", 2)),
                config.getBoolean("placement.only-air", true),
                colorize(config.getString("messages.enabled", "&bMagic Carpet enabled.")),
                colorize(config.getString("messages.disabled", "&cMagic Carpet disabled.")),
                colorize(config.getString("messages.no-permission", "&cYou do not have permission to use this.")));
    }

    private static Material readMaterial(String name, Material fallback) {
        if (name == null || name.isBlank()) {
            return fallback;
        }

        Material material = Material.matchMaterial(name);
        return material == null ? fallback : material;
    }

    private static String colorize(String text) {
        return text == null ? "" : text.replace('&', '\u00A7');
    }
}

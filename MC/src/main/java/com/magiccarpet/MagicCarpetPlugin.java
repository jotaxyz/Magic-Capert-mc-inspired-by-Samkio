package com.magiccarpet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class MagicCarpetPlugin extends JavaPlugin {
    private final Map<UUID, CarpetSession> activeCarpets = new HashMap<>();
    private final Map<Location, UUID> carpetOwners = new HashMap<>();
    private BukkitTask movementTask;
    private CarpetConfig carpetConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPluginConfig();

        PluginCommand command = getCommand("mc");
        if (command == null) {
            throw new IllegalStateException("Command mc is missing from plugin.yml");
        }

        command.setExecutor(new MagicCarpetCommand(this));
        Bukkit.getPluginManager().registerEvents(new MagicCarpetListener(this), this);
        movementTask = Bukkit.getScheduler().runTaskTimer(this, this::tickCarpets, 1L, 1L);
    }

    @Override
    public void onDisable() {
        if (movementTask != null) {
            movementTask.cancel();
        }

        for (UUID uuid : new ArrayList<>(activeCarpets.keySet())) {
            disableCarpet(uuid);
        }
        carpetOwners.clear();
    }

    public boolean toggleCarpet(Player player) {
        UUID uuid = player.getUniqueId();
        if (activeCarpets.containsKey(uuid)) {
            disableCarpet(uuid);
            return false;
        }

        activeCarpets.put(uuid, new CarpetSession());
        refresh(uuid);
        return true;
    }

    public void disableCarpet(UUID uuid) {
        CarpetSession session = activeCarpets.remove(uuid);
        if (session != null) {
            restoreTrackedBlocks(uuid, session);
        }
    }

    public boolean isActive(UUID uuid) {
        return activeCarpets.containsKey(uuid);
    }

    public void queueAscend(UUID uuid, int ticks) {
        CarpetSession session = activeCarpets.get(uuid);
        if (session != null) {
            session.queueAscendTicks(ticks);
        }
    }

    public boolean isCarpetBlock(Block block) {
        return carpetOwners.containsKey(block.getLocation());
    }

    public CarpetConfig getCarpetConfig() {
        return carpetConfig;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        carpetConfig = CarpetConfig.from(getConfig());
    }

    public UUID getOwner(Block block) {
        return carpetOwners.get(block.getLocation());
    }

    public void refresh(UUID uuid) {
        CarpetSession session = activeCarpets.get(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (session == null || player == null || !player.isOnline()) {
            disableCarpet(uuid);
            return;
        }

        restoreTrackedBlocks(uuid, session);
        placeCarpet(player, session);
    }

    private void tickCarpets() {
        for (UUID uuid : new ArrayList<>(activeCarpets.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            CarpetSession session = activeCarpets.get(uuid);
            if (player == null || !player.isOnline() || session == null) {
                disableCarpet(uuid);
                continue;
            }

            if (player.isOnGround()) {
                session.setJumpHandledInAir(false);
            } else if (!session.jumpHandledInAir() && player.getVelocity().getY() > 0.15D) {
                session.queueAscendTicks(carpetConfig.ascendTicksPerJump());
                session.setJumpHandledInAir(true);
            }

            double verticalOffset = 0.0D;
            if (session.consumeAscendTick()) {
                verticalOffset += carpetConfig.ascendPerJump();
            }
            if (player.isSneaking()) {
                verticalOffset -= carpetConfig.descendPerTick();
            }
            if (verticalOffset != 0.0D) {
                Location teleported = player.getLocation().clone().add(0.0D, verticalOffset, 0.0D);
                player.teleport(teleported);
            }

            restoreTrackedBlocks(uuid, session);
            placeCarpet(player, session);
        }
    }

    private void placeCarpet(Player player, CarpetSession session) {
        World world = player.getWorld();
        Location base = player.getLocation().clone().subtract(0.0D, 1.0D, 0.0D);
        int centerX = base.getBlockX();
        int baseY = base.getBlockY();
        int centerZ = base.getBlockZ();

        int carpetRadius = carpetConfig.radius();
        int carpetSize = (carpetRadius * 2) + 1;
        List<CarpetBlockState> newTracked = new ArrayList<>(carpetSize * carpetSize);
        for (int x = -carpetRadius; x <= carpetRadius; x++) {
            for (int z = -carpetRadius; z <= carpetRadius; z++) {
                Block block = world.getBlockAt(centerX + x, baseY, centerZ + z);
                if (!canPlace(block)) {
                    continue;
                }

                boolean center = x == 0 && z == 0;
                BlockData previousData = block.getBlockData().clone();
                Material material = center && carpetConfig.useCenterBlock()
                        ? carpetConfig.centerMaterial()
                        : carpetConfig.edgeMaterial();
                block.setType(material, false);
                CarpetBlockState tracked = new CarpetBlockState(block.getLocation(), previousData, center);
                newTracked.add(tracked);
                carpetOwners.put(block.getLocation(), player.getUniqueId());
            }
        }

        session.trackedBlocks().clear();
        session.trackedBlocks().addAll(newTracked);
    }

    private void restoreTrackedBlocks(UUID owner, CarpetSession session) {
        for (CarpetBlockState tracked : session.trackedBlocks()) {
            carpetOwners.remove(tracked.location(), owner);
            Block block = tracked.location().getBlock();
            if (isTrackedMaterial(block.getType(), tracked.center())) {
                block.setBlockData(tracked.previousData(), false);
            }
        }
        session.trackedBlocks().clear();
    }

    private boolean canPlace(Block block) {
        return carpetConfig.onlyAirPlacement() ? block.getType().isAir() : block.isReplaceable();
    }

    private boolean isTrackedMaterial(Material material, boolean center) {
        if (center && carpetConfig.useCenterBlock()) {
            return material == carpetConfig.centerMaterial();
        }
        return material == carpetConfig.edgeMaterial();
    }
}

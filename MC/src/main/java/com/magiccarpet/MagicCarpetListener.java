package com.magiccarpet;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class MagicCarpetListener implements Listener {
    private final MagicCarpetPlugin plugin;

    public MagicCarpetListener(MagicCarpetPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.isCarpetBlock(event.getBlock())) {
            event.setDropItems(false);
            event.setExpToDrop(0);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (plugin.isCarpetBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked != null && plugin.isCarpetBlock(clicked)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Set<UUID> impactedOwners = new HashSet<>();
        event.blockList().removeIf(block -> {
            UUID owner = plugin.getOwner(block);
            if (owner == null) {
                return false;
            }
            impactedOwners.add(owner);
            return true;
        });

        for (UUID owner : impactedOwners) {
            plugin.refresh(owner);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.disableCarpet(event.getPlayer().getUniqueId());
    }
}

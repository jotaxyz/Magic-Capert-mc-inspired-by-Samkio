package com.magiccarpet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MagicCarpetCommand implements CommandExecutor {
    private final MagicCarpetPlugin plugin;

    public MagicCarpetCommand(MagicCarpetPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("magiccarpet.use")) {
            player.sendMessage(plugin.getCarpetConfig().noPermissionMessage());
            return true;
        }

        boolean enabled = plugin.toggleCarpet(player);
        player.sendMessage(enabled ? plugin.getCarpetConfig().enabledMessage() : plugin.getCarpetConfig().disabledMessage());
        return true;
    }
}

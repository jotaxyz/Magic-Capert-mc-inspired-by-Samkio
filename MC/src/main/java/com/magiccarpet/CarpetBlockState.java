package com.magiccarpet;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public record CarpetBlockState(Location location, BlockData previousData, boolean center) {
}

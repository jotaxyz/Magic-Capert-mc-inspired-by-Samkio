package com.magiccarpet;

import java.util.ArrayList;
import java.util.List;

public final class CarpetSession {
    private final List<CarpetBlockState> trackedBlocks = new ArrayList<>();
    private int pendingAscendTicks;
    private boolean jumpHandledInAir;

    public List<CarpetBlockState> trackedBlocks() {
        return trackedBlocks;
    }

    public int pendingAscendTicks() {
        return pendingAscendTicks;
    }

    public void queueAscendTicks(int ticks) {
        pendingAscendTicks += ticks;
    }

    public boolean consumeAscendTick() {
        if (pendingAscendTicks <= 0) {
            return false;
        }
        pendingAscendTicks--;
        return true;
    }

    public boolean jumpHandledInAir() {
        return jumpHandledInAir;
    }

    public void setJumpHandledInAir(boolean jumpHandledInAir) {
        this.jumpHandledInAir = jumpHandledInAir;
    }
}

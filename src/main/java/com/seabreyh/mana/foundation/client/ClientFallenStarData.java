package com.seabreyh.mana.foundation.client;

import net.minecraft.core.BlockPos;

public class ClientFallenStarData {
    private static BlockPos catcherPos;

    public static void setCatcherPos(BlockPos catcherPos) {
        ClientFallenStarData.catcherPos = catcherPos;
    }

    public static BlockPos getCatcherPos() {
        return catcherPos;
    }
}

package com.seabreyh.mana.content.blocks.decoration;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class CompactBlockOfAmethyst extends Block {

    public static final Properties PROPERTIES = Properties.of()
            .sound(SoundType.AMETHYST)
            .mapColor(MapColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F);

    public CompactBlockOfAmethyst(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
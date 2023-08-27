package com.seabreyh.mana.content.blocks.decoration;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class AmethystBlock extends Block {

    public static final Properties PROPERTIES = Properties.of()
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F);

    public AmethystBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
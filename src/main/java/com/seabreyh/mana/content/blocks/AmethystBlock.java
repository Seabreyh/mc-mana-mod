package com.seabreyh.mana.content.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class AmethystBlock extends Block {

    public AmethystBlock() {
        this(Properties.of()
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 6.0F));
    }

    public AmethystBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
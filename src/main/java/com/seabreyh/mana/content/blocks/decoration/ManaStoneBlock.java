package com.seabreyh.mana.content.blocks.decoration;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class ManaStoneBlock extends Block {

    public static final Properties PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F);

    public static final Properties CELESTIAL_PROPERTIES = BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
            .lightLevel(BlockState -> 7);

    public ManaStoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
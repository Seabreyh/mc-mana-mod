package com.seabreyh.mana.content.blocks.botany.flowers;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;

public class ManaFlower {

    public static final Properties FLOWER_PROPERTIES = Properties.copy(Blocks.DANDELION)
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS);

    public static final Properties FLOWER_POT_BLOCK_PROPERTIES = BlockBehaviour.Properties.of()
            .instabreak()
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY);

}

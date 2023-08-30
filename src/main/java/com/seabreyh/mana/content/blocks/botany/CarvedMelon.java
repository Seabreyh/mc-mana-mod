package com.seabreyh.mana.content.blocks.botany;

import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class CarvedMelon extends EquipableCarvedPumpkinBlock {

    public static final Properties PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN)
            .strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY);

    public CarvedMelon(Properties p_289677_) {
        super(p_289677_);
    }

}

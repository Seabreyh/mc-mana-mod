package com.seabreyh.mana.items.brewing;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ManaMobEffect extends MobEffect{


    // Effect constructor is not public for some reason...
    public ManaMobEffect(MobEffectCategory type, int liquidColor)
    {
        super(type, liquidColor);
    }

}

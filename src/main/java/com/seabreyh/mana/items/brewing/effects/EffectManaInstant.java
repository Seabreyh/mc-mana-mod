package com.seabreyh.mana.items.brewing.effects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import com.seabreyh.mana.ManaMod;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectManaInstant extends MobEffect {

    public EffectManaInstant() {
        super(MobEffectCategory.BENEFICIAL, 0X865337);
        this.setRegistryName(ManaMod.MOD_ID, "mana_instant");
        // this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,
        // "03C3C89D-7037-4B42-869F-B146BCB64D2F", 0.5D,
        // AttributeModifier.Operation.ADDITION);
    }

    public void applyEffectTick(LivingEntity LivingEntityIn, int amplifier) {
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "mana.potion.mana_instant";
    }

}
package com.seabreyh.mana.content.items.brewing.effects;
package com.seabreyh.mana.items.brewing.effects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

import com.seabreyh.mana.event.player.PlayerManaEvent;
import com.seabreyh.mana.registry.ManaParticles;

import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectManaInstant extends InstantenousMobEffect {

    public EffectManaInstant() {
        super(MobEffectCategory.BENEFICIAL, 0XD779E2);
        this.setRegistryName("mana:mana_instant");
    }

    public boolean isInstantenous() {
        return true;
    }

    public boolean isDurationEffectTick(int p_19444_, int p_19445_) {
        return p_19444_ >= 1;
    }

    public void applyInstantenousEffect(@Nullable Entity p_19462_, @Nullable Entity p_19463_, LivingEntity livingEntity,
            int p_19465_, double p_19466_) {
        if (!livingEntity.level.isClientSide) {
            if (livingEntity instanceof Player) {
                PlayerManaEvent.regenMana((Player) livingEntity, 10);
            }
        }
        livingEntity.level.addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
                livingEntity.getRandomX(1.0),
                livingEntity.getRandomY(), livingEntity.getRandomZ(1.0), 0, 0, 0);
    }

    public String getDescriptionId() {
        return "mana.effect.mana_instant";
    }

}
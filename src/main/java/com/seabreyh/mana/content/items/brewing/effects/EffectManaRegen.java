// package com.seabreyh.mana.items.brewing.effects;

// import net.minecraft.world.entity.LivingEntity;
// import net.minecraft.world.entity.player.Player;

// import com.seabreyh.mana.event.player.PlayerManaEvent;
// import com.seabreyh.mana.registry.ManaParticles;

// import net.minecraft.world.effect.MobEffect;
// import net.minecraft.world.effect.MobEffectCategory;

// public class EffectManaRegen extends MobEffect {

// public EffectManaRegen() {
// super(MobEffectCategory.BENEFICIAL, 0x562251);
// this.setRegistryName("mana:mana_regen");
// }

// public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
// if (!livingEntity.level.isClientSide) {
// if (livingEntity instanceof Player) {
// PlayerManaEvent.regenMana((Player) livingEntity, 1);
// }
// }
// livingEntity.level.addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
// livingEntity.getRandomX(1.0),
// livingEntity.getRandomY(), livingEntity.getRandomZ(1.0), 0, 0, 0);

// }

// public boolean isDurationEffectTick(int p_19455_, int p_19456_) {
// int k = 10 >> p_19456_;
// if (k > 0) {
// return p_19455_ % k == 0;
// } else {
// return true;
// }
// }

// public String getDescriptionId() {
// return "mana.effect.mana_regen";
// }

// }
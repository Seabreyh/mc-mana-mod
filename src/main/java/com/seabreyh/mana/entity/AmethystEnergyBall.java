package com.seabreyh.mana.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import com.seabreyh.mana.particle.ManaParticles;
import com.seabreyh.mana.registry.ManaEntities;

import javax.annotation.Nonnull;

public class AmethystEnergyBall extends ThrowableProjectile {
    private int life;

    public AmethystEnergyBall(Level world, LivingEntity player) {
        super(ManaEntities.AMETHYST_ENERGY_BALL.get(), player, world);
        double xPos = player.getX();
        double yPos = player.getEyeY() - 0.2D;
        double zPos = player.getZ();

        this.setPos(xPos, yPos, zPos);
        this.noPhysics = true;
        this.life = 0;
    }

    public AmethystEnergyBall(EntityType<? extends AmethystEnergyBall> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    protected AmethystEnergyBall(EntityType<? extends ThrowableProjectile> p_37456_, double p_37457_, double p_37458_,
            double p_37459_, Level p_37460_) {
        super(p_37456_, p_37457_, p_37458_, p_37459_, p_37460_);
    }

    protected AmethystEnergyBall(EntityType<? extends ThrowableProjectile> p_37462_, LivingEntity p_37463_,
            Level p_37464_) {
        super(p_37462_, p_37463_, p_37464_);
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void shoot(double p_37266_, double p_37267_, double p_37268_, float p_37269_, float p_37270_) {
        Vec3 vec3 = (new Vec3(p_37266_, p_37267_, p_37268_)).normalize()
                .add(this.random.nextGaussian() * (double) 0.0075F * (double) p_37270_,
                        this.random.nextGaussian() * (double) 0.0075F * (double) p_37270_,
                        this.random.nextGaussian() * (double) 0.0075F * (double) p_37270_)
                .scale((double) p_37269_);
        this.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public void shootFromRotation(Entity player, float p_37253_, float p_37254_, float p_37255_, float p_37256_,
            float p_37257_) {
        float f = -Mth.sin(p_37254_ * ((float) Math.PI / 180F)) * Mth.cos(p_37253_ * ((float) Math.PI / 180F));
        float f1 = -Mth.sin((p_37253_ + p_37255_) * ((float) Math.PI / 180F));
        float f2 = Mth.cos(p_37254_ * ((float) Math.PI / 180F)) * Mth.cos(p_37253_ * ((float) Math.PI / 180F));
        this.shoot((double) f, (double) f1, (double) f2, p_37256_, p_37257_);
        Vec3 vec3 = player.getDeltaMovement();
        Vec3 shootDelta = this.getDeltaMovement();
        float speedDampen = 2.0f;
        shootDelta = new Vec3(shootDelta.x / speedDampen, shootDelta.y / speedDampen, shootDelta.z / speedDampen);

        this.setDeltaMovement(shootDelta.add(vec3.x, player.isOnGround() ? 0.0D : vec3.y, vec3.z));
    }

    @Override
    protected void defineSynchedData() {
    }

    protected SoundEvent getPloofSound() {
        return SoundEvents.AMETHYST_BLOCK_STEP;
    }

    protected SoundEvent getHitSound() {
        return SoundEvents.ZOMBIE_VILLAGER_CURE;
    }

    @Override
    public void tick() {
        ++this.life;
        super.tick();
        if (this.life % 2 == 0) {
            this.playSound(this.getPloofSound(), 2F, 3F);
        }

        if (!this.level.isClientSide) {

            // Despawn after 5 seconds
            if (this.life > 20 * 5) {
                this.level.broadcastEntityEvent(this, (byte) 3);
                this.discard();
            }
        } else {
            for (int i = 0; i < 4; ++i) {
                this.level.addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE.get(), this.getX(),
                        this.getY(), this.getZ(),
                        this.random.nextGaussian() * 0.1D, this.random.nextGaussian() * 0.1D,
                        this.random.nextGaussian() * 0.1D);
            }
        }
    }

    protected void onHitBlock(BlockHitResult hitBlock) {
        super.onHitBlock(hitBlock);
        if (!this.level.isClientSide) { // qty spread velocity
            ((ServerLevel) this.level).sendParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 1, 0D,
                    0D, 0D, 0D);
            ((ServerLevel) this.level).sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 20,
                    1D, 1D, 1D, 0.3D);
        }
        this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        this.discard();
    }

    protected void onHitEntity(EntityHitResult hitEntity) {
        super.onHitEntity(hitEntity);
        Entity entity = hitEntity.getEntity();
        Entity entity1 = this.getOwner();
        LivingEntity livingentity = entity1 instanceof LivingEntity ? (LivingEntity) entity1 : null;
        boolean flag = entity.hurt(DamageSource.indirectMobAttack(this, livingentity).setProjectile(), 2F);
        if (flag) {
            this.doEnchantDamageEffects(livingentity, entity);
            this.playSound(this.getHitSound(), 1.2F, 1.5F);
        }
    }

}

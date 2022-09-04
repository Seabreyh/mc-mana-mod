package com.seabreyh.mana.entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.particle.ManaParticles;
import com.seabreyh.mana.registry.ManaEntities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nonnull;

public class AmethystEnergyBall extends ThrowableProjectile {
    private int life;
    private int explosionPower = 2;

    public AmethystEnergyBall(Level world, LivingEntity player) {
        super(ManaEntities.AMETHYST_ENERGY_BALL.get(), player, world);
        double xPos = player.getX();
        double yPos = player.getEyeY() - 0.1D;
        double zPos = player.getZ();
        this.setPos(xPos, yPos, zPos);
        this.noPhysics = true;
        this.life = 0;
        ManaMod.LOGGER.debug("### AMETHYST ENERGY BALL");
    }

    public AmethystEnergyBall(EntityType<? extends AmethystEnergyBall> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
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
        this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, 0.0F, vec3.z));
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

    protected float getWaterInertia() {
        return 0.99F;
    }

    public void tick() {
        super.tick();

        this.playSound(this.getPloofSound(), 2F, 3F);

        ++this.life;

        boolean flag = this.noPhysics;
        Vec3 vec3 = this.getDeltaMovement();
        // (double)3 changes the underwater speed.
        this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015D * (double) 3, this.getZ());
        if (this.level.isClientSide) {
            this.yOld = this.getY();
        }
        // (double)3 changes the underwater speed.
        double d0 = 0.05D * (double) 3;
        this.setDeltaMovement(this.getDeltaMovement().scale(0.75D).add(vec3.normalize().scale(d0)));

        if (!this.level.isClientSide) {
            // Despawn after 10 seconds
            if (this.life > 20 * 10) {
                this.level.broadcastEntityEvent(this, (byte) 3);
                this.discard();
            }
        } else {
            for (int i = 0; i < 4; ++i) {

                this.level.addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_DEFAULT.get(), this.getX(),
                        this.getY(), this.getZ(),
                        this.random.nextGaussian() * 0.1D, this.random.nextGaussian() * 0.1D,
                        this.random.nextGaussian() * 0.1D);

                vec3 = this.getDeltaMovement();
                double d5 = vec3.x;
                double d6 = vec3.y;
                double d1 = vec3.z;

                double d7 = this.getX() + d5;
                double d2 = this.getY() + d6;
                double d3 = this.getZ() + d1;
                double d4 = vec3.horizontalDistance();

                if (flag) {
                    this.setYRot((float) (Mth.atan2(-d5, -d1) * (double) (180F / (float) Math.PI)));
                } else {
                    this.setYRot((float) (Mth.atan2(d5, d1) * (double) (180F / (float) Math.PI)));
                }

                this.setXRot((float) (Mth.atan2(d6, d4) * (double) (180F / (float) Math.PI)));
                this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
                this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
                float f = 0.7F;

                if (this.isInWater()) {
                    f = this.getWaterInertia();
                }

                this.setDeltaMovement(vec3.scale((double) f));
                this.setPos(d7, d2, d3);
                this.checkInsideBlocks();
            }
        }
    }

    protected void onHitBlock(BlockHitResult hitBlock) {
        BlockState blockstate = this.level.getBlockState(hitBlock.getBlockPos().above());

        if (this.wasOnFire || blockstate.getBlock() == Blocks.FIRE
                || blockstate.getBlock() == Blocks.SOUL_FIRE) {
            if (!this.level.isClientSide) {
                boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level,
                        this.getOwner());
                this.level.explode((Entity) null, this.getX(), this.getY(), this.getZ(), (float) this.explosionPower,
                        flag, flag ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
                this.discard();
            }
        } else {
            this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
        }

        if (!this.level.isClientSide) { // qty spread velocity
            ((ServerLevel) this.level).sendParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 1, 0D,
                    0D, 0D, 0D);
            ((ServerLevel) this.level).sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 20,
                    1D, 1D, 1D, 0.3D);
        }

        super.onHitBlock(hitBlock);
        this.discard();
    }

    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
    }

    protected void onHitEntity(EntityHitResult hitEntity) {
        super.onHitEntity(hitEntity);
        Entity entity = hitEntity.getEntity();
        Entity entity1 = this.getOwner();
        LivingEntity livingentity = entity1 instanceof LivingEntity ? (LivingEntity) entity1 : null;
        boolean flag = entity.hurt(DamageSource.indirectMobAttack(this, livingentity).setProjectile(), 2F);

        if (flag && entity != entity1) {
            this.doEnchantDamageEffects(livingentity, entity);
            this.playSound(this.getHitSound(), 1.2F, 1.5F);

            if (!this.level.isClientSide) {
                ((ServerLevel) this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 20,
                        0.15D,
                        0.15D, 0.15D, 0.2D);
            }

            this.discard();
        }
    }
}

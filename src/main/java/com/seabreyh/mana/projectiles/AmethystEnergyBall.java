package com.seabreyh.mana.projectiles;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;

import com.mojang.logging.LogUtils;
import com.seabreyh.mana.init.ManaEntities;

import org.slf4j.Logger;
import javax.annotation.Nonnull;

public class AmethystEnergyBall extends ThrowableProjectile {
    private static final Logger LOGGER = LogUtils.getLogger();
    private int life;

    public AmethystEnergyBall(Level world, LivingEntity player) {
        super(ManaEntities.AMETHYST_ENERGY_BALL.get(), player, world);
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

    // If you forget to override this method, the default vanilla method will be
    // called.
    // This sends a vanilla spawn packet, which is then silently discarded when it
    // reaches the client.
    // Your entity will be present on the server and can cause effects, but the
    // client will not have a copy of the entity
    // and hence it will not render.
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
        shootDelta = new Vec3(shootDelta.x / 5.0F, shootDelta.y / 5.0F, shootDelta.z / 5.0F);

        this.setDeltaMovement(shootDelta.add(vec3.x, player.isOnGround() ? 0.0D : vec3.y, vec3.z));
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        ++this.life;
        super.tick();

        if (!this.level.isClientSide) {
            for (int i = 0; i < 4; ++i) {
                ((ServerLevel) this.level).sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(),
                        1, 0.0D, this.random.nextGaussian() * 0.02D, -this.getDeltaMovement().y * 0.2D,
                        this.random.nextGaussian() * 0.02D);
            }

            // Despawn after 5 seconds
            if (this.life > 20 * 5) {
                this.level.broadcastEntityEvent(this, (byte) 3);
                this.discard();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        Entity entity = hit.getEntity();
        entity.hurt(DamageSource.thrown(this, this.getOwner()), 1.0F);
    }

    @Override
    protected void onHit(HitResult hit) {
        super.onHit(hit);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}

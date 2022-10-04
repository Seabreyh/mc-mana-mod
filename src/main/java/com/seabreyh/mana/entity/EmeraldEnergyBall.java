package com.seabreyh.mana.entity;

import com.seabreyh.mana.particle.ManaParticles;
import com.seabreyh.mana.registry.ManaEntities;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class EmeraldEnergyBall extends ThrowableProjectile {
    private int life;
    private Vec3 shootDir = new Vec3(0.0D, 0.0D, 0.0D); // not null, to allow constant mob searching.
    private LivingEntity owner;
    private Mob target;

    public EmeraldEnergyBall(Level world, LivingEntity player) {
        super(ManaEntities.EMERALD_ENERGY_BALL.get(), player, world);

        double xPos = player.getX();
        double yPos = player.getEyeY() - 0.1D;
        double zPos = player.getZ();

        this.setPos(xPos, yPos, zPos);
        this.noPhysics = true;
        this.life = 0;
        this.owner = player;
    }

    public EmeraldEnergyBall(EntityType<? extends EmeraldEnergyBall> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Nullable
    // Helper function for raycasting target entities
    private static EntityHitResult getEntityHitResult(Entity p_37295_, Predicate<Entity> p_37296_, Vec3 rayDir) {
        Level level = p_37295_.level;
        Vec3 vec31 = p_37295_.position();
        Vec3 vec32 = vec31.add(rayDir);
        HitResult hitresult = level
                .clip(new ClipContext(vec31, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_37295_));

        if (hitresult.getType() != HitResult.Type.MISS) {
            vec32 = hitresult.getLocation();
        }

        EntityHitResult hitresult1 = ProjectileUtil.getEntityHitResult(level, p_37295_, vec31, vec32,
                p_37295_.getBoundingBox().expandTowards(rayDir).inflate(1.0D), p_37296_);

        if (hitresult1 != null) {
            hitresult = hitresult1;
        }

        return hitresult1;
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

        Vec3 shootDir = (new Vec3(p_37266_, p_37267_, p_37268_)).normalize();
        this.shootDir = shootDir;
    }

    private void resolveEnemyTarget() {
        // Handle entity target "homing"
        // get boudning box of the emerald ball entity, rather than players, this allows
        // ball to target after shooting.
        AABB aabb = this.getBoundingBox().inflate(64.0D, 24.0D, 64.0D);
        List<? extends Mob> candidates = this.level.getNearbyEntities(Mob.class,
                TargetingConditions.forCombat().range(64.0D), this.owner, aabb);
        Mob foundTarget = null;
        double shortestDist = Double.MAX_VALUE;

        for (Mob mobs : candidates) {
            Vec3 dirToEntity = mobs.position().subtract(this.position()).normalize();
            if (dirToEntity.dot(this.shootDir) >= 0.9) {
                foundTarget = mobs;
                double distTo = foundTarget.position().subtract(this.position()).length();
                if (distTo < shortestDist) {
                    shortestDist = distTo;
                    this.target = foundTarget;
                }
            }
        }
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
        // initial target check
        this.resolveEnemyTarget();
    }

    @Override
    protected void defineSynchedData() {
        //
    }

    protected SoundEvent getPloofSound() {
        return SoundEvents.AMETHYST_BLOCK_STEP;
    }

    protected SoundEvent getTargetedSound() {
        return SoundEvents.FIREWORK_ROCKET_LAUNCH;
    }

    protected SoundEvent getHitSound() {
        if (this.target != null) {
            return SoundEvents.DRAGON_FIREBALL_EXPLODE;
        } else {
            return SoundEvents.ZOMBIE_VILLAGER_CURE;
        }
    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    protected boolean canHitEntity(Entity p_37341_) {
        return super.canHitEntity(p_37341_) && !p_37341_.noPhysics;
    }

    public void tick() {
        super.tick();

        this.playSound(this.getPloofSound(), 2F, 3F);

        if (this.target != null) {
            // Target locked on travel sound
            if (this.life % 3 == 0) {
                this.playSound(this.getTargetedSound(), (float) Math.random(), (float) Math.random());
            }
        }

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

            Vec3 delta = this.getDeltaMovement();
            int a = 1;

            // MOVEMENT LOGIC - TARGETED
            if (this.target != null) {
                Vec3 dirToEntity = this.target.getEyePosition().subtract(this.position()).normalize();
                delta = delta.lerp(dirToEntity, 0.15);
                this.setDeltaMovement(delta);

                Vec3 vec32 = target.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec32.y * 0.015D * (double) a, this.getZ());
                if (this.level.isClientSide) {
                    this.yOld = this.getY();
                }

                double d02 = 0.05D * (double) a;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec32.normalize().scale(d02)));

                // If target is dead, R.I.P., set target to null to allow ball entity to find a
                // new target and continue moving.
                if (this.target.isRemoved()) {
                    this.target = null;
                }

                ++this.life;

            } else {
                // MOVEMENT LOGIC - NO TARGET.
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

                // constantly check for potential targets.
                this.resolveEnemyTarget();

            }

        } else {

            for (int i = 0; i < 4; ++i) {
                this.level.addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_GREEN.get(), this.getX(),
                        this.getY(), this.getZ(),
                        this.random.nextGaussian() * 0.1D, this.random.nextGaussian() * 0.1D,
                        this.random.nextGaussian() * 0.1D);

            }
        }
    }

    protected void onHitBlock(BlockHitResult hitBlock) {
        // discard entity on hitblock unless LEAVES, GLASS, or ICE
        BlockState blockState = this.level.getBlockState(hitBlock.getBlockPos());
        if (blockState.getMaterial() != Material.LEAVES && blockState.getMaterial() != Material.GLASS
                && blockState.getMaterial() != Material.ICE) {
            this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);

            if (!this.level.isClientSide) { // qty spread velocity
                ((ServerLevel) this.level).sendParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 1,
                        0D,
                        0D, 0D, 0D);
                ((ServerLevel) this.level).sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(),
                        20,
                        1D, 1D, 1D, 0.3D);
            }

            super.onHitBlock(hitBlock);
            this.discard();
        }
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
            if (this.target != null) {
                this.playSound(this.getHitSound(), 3F, 2F);
            } else {
                this.playSound(this.getHitSound(), 1.2F, 1.5F);
            }

            if (!this.level.isClientSide) {
                ((ServerLevel) this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 20,
                        0.15D, 0.15D, 0.15D, 0.2D);
            }

            this.discard();
        }
    }
}

package com.seabreyh.mana.entity;

import com.seabreyh.mana.particle.ManaParticles;
import com.seabreyh.mana.registry.ManaItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FallenStar extends AbstractArrow {
    private int age;
    public float bobOffs;
    private BlockState lastState;
    private double baseDamage = 2.0D;
    private final SoundEvent HIT_SOUND = SoundEvents.AMETHYST_BLOCK_BREAK;
    private final SoundEvent FALL_SOUND = SoundEvents.AMETHYST_BLOCK_CHIME;

    public FallenStar(EntityType<? extends FallenStar> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    protected FallenStar(EntityType<? extends FallenStar> p_36711_, double p_36712_, double p_36713_,
            double p_36714_, Level p_36715_) {
        super(p_36711_, p_36712_, p_36713_, p_36714_, p_36715_);
    }

    public void tick() {

        if (this.age != -32768) {
            ++this.age;
        }

        boolean flag = this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
            this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);
        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)) {
            this.clearFire();
        }

        if (this.inGround && !flag) {
            if (this.lastState != blockstate && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level.isClientSide) {
                this.tickDespawn();
            }

            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vec3 vec32 = this.position();
            Vec3 vec33 = vec32.add(vec3);
            HitResult hitresult = this.level
                    .clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hitresult.getType() != HitResult.Type.MISS) {
                vec33 = hitresult.getLocation();
            }

            while (!this.isRemoved()) {
                EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) hitresult).getEntity();
                    Entity entity1 = this.getOwner();
                    if (entity instanceof Player && entity1 instanceof Player
                            && !((Player) entity1).canHarmPlayer((Player) entity)) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag
                        && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                    this.onHit(hitresult);
                    this.hasImpulse = true;
                }

                if (entityhitresult == null || this.getPierceLevel() <= 0) {
                    break;
                }

                hitresult = null;
            }

            vec3 = this.getDeltaMovement();
            double deltaX = vec3.x;
            double deltaY = vec3.y;
            double deltaZ = vec3.z;
            if (!this.isOnGround()) {
                for (int i = 0; i < 4; ++i) {
                    if (this.age % 1 == 0)
                        this.level.addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE.get(),
                                this.getX() + deltaX * (double) i / 4.0D - deltaX * 1.5,
                                this.getY() + deltaY * (double) i / 4.0D - deltaY * 1.5,
                                this.getZ() + deltaZ * (double) i / 4.0D - deltaZ * 1.5, -deltaX,
                                -deltaY + 0.2D,
                                -deltaZ);
                }
                if (this.age % 2 == 0)
                    this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), FALL_SOUND,
                            SoundSource.AMBIENT,
                            100.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            }

            double d7 = this.getX() + deltaX;
            double d2 = this.getY() + deltaY;
            double d3 = this.getZ() + deltaZ;
            double d4 = vec3.horizontalDistance();
            if (flag) {
                this.setYRot((float) (Mth.atan2(-deltaX, -deltaZ) * (double) (180F / (float) Math.PI)));
            } else {
                this.setYRot((float) (Mth.atan2(deltaX, deltaZ) * (double) (180F / (float) Math.PI)));
            }

            this.setXRot((float) (Mth.atan2(deltaY, d4) * (double) (180F / (float) Math.PI)));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            float f = 0.99F;
            float f1 = 0.05F;
            if (this.isInWater()) {
                for (int j = 0; j < 4; ++j) {
                    float f2 = 0.25F;
                    this.level.addParticle(ParticleTypes.BUBBLE, d7 - deltaX * 0.25D, d2 - deltaY * 0.25D,
                            d3 - deltaZ * 0.25D, deltaX,
                            deltaY, deltaZ);
                }

                f = this.getWaterInertia();
            }

            this.setDeltaMovement(vec3.scale((double) f));
            if (!this.isNoGravity() && !flag) {
                Vec3 vec34 = this.getDeltaMovement();
                this.setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
            }

            this.setPos(d7, d2, d3);
            this.checkInsideBlocks();
        }
        if (this.level.isClientSide && this.age % 5 == 0) {
            this.level.addParticle(ParticleTypes.END_ROD, this.getX() + this.random.nextGaussian() * 0.5,
                    this.getY() + 0.4 + this.random.nextGaussian() * 0.2,
                    this.getZ() + this.random.nextGaussian() * 0.5,
                    0D, 0.1D, 0D);

            // this.playSound(FALL_SOUND, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F +
            // 0.9F));

        }
    }

    private boolean shouldFall() {
        return this.inGround && this.level.noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply((double) (this.random.nextFloat() * 0.2F),
                (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
        this.age = 0;
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ManaItems.FALLEN_STAR_ITEM.get());
    }

    public ItemStack getItem() {
        return new ItemStack(ManaItems.FALLEN_STAR_ITEM.get());
    }

    @Override
    protected void onHitEntity(EntityHitResult p_36757_) {
        Entity entity = p_36757_.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double) f * this.baseDamage, 0.0D, 2.147483647E9D));

        if (this.isCritArrow()) {
            long j = (long) this.random.nextInt(i / 2 + 2);
            i = (int) Math.min(j + (long) i, 2147483647L);
        }

        Entity entity1 = this.getOwner();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = DamageSource.arrow(this, this);
        } else {
            damagesource = DamageSource.arrow(this, entity1);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity) entity1).setLastHurtMob(entity);
            }
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getRemainingFireTicks();
        if (this.isOnFire() && !flag) {
            entity.setSecondsOnFire(5);
        }

        if (entity.hurt(damagesource, (float) i)) {
            if (flag) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity;
                if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
                    livingentity.setArrowCount(livingentity.getArrowCount() + 1);
                }

                this.doPostHurtEffects(livingentity);
                if (entity1 != null && livingentity != entity1 && livingentity instanceof Player
                        && entity1 instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer) entity1).connection
                            .send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }

            }

            playHitSound();

        } else {
            entity.setRemainingFireTicks(k);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            }
        }

    }

    @Override
    protected void onHitBlock(BlockHitResult p_36755_) {
        this.lastState = this.level.getBlockState(p_36755_.getBlockPos());
        Vec3 vec3 = p_36755_.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale((double) 0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.playHitSound();
        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte) 0);
        this.setShotFromCrossbow(false);
    }

    private void playHitSound() {
        this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), HIT_SOUND,
                SoundSource.AMBIENT,
                100.0F, 1.4F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
            if (this.tryPickup(player)) {
                player.take(this, 1);
                this.discard();
            }

        }
    }

    protected boolean tryPickup(Player player) {
        return player.getInventory().add(this.getPickupItem());
    }

    public int getAge() {
        return this.age;
    }

    public float getSpin(float p_32009_) {
        return ((float) this.getAge() + p_32009_) / 20.0F + this.bobOffs;
    }

}

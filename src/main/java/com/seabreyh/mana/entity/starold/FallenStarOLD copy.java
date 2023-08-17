package com.seabreyh.mana.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.seabreyh.mana.ManaEntityDataSerializers;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.client.renderers.entity.FallenStarSyncData;
import com.seabreyh.mana.registry.ManaItems;
import com.seabreyh.mana.registry.ManaParticles;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FallenStar extends Projectile {
    // Synced FallenStar Data
    private static final EntityDataAccessor<FallenStarSyncData> FALLEN_STAR_DATA = SynchedEntityData.defineId(
            FallenStar.class, ManaEntityDataSerializers.FALLEN_STAR_DATA);

    private EntityDimensions dimensions;
    @Nullable
    private BlockState lastState;
    protected boolean inGround;
    protected int inGroundTime;
    public FallenStar.Pickup pickup = FallenStar.Pickup.DISALLOWED;
    public int shakeTime;
    private int life;
    private boolean isFalling;
    double catchSpeed = 0.8D;
    int timeTillStartCatch = 130;
    private double baseDamage = 10.0D;
    private int knockback;
    private Player ownPlayer; // GET TO SPAWN
    private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();

    public FallenStar(EntityType<? extends FallenStar> entity, Level level) {
        super(entity, level);
        setIsFalling(true);

        this.dimensions = entity.getDimensions(); // GET TO SPAWN
    }

    // is this ever called?
    public FallenStar(EntityType<? extends FallenStar> getEntity, Level world, Player ownPlayer) {
        this(getEntity, world);
        this.ownPlayer = ownPlayer;

    }

    // -----------------------------------------
    // Sync Data
    // -----------------------------------------

    @Override
    protected void defineSynchedData() {
        entityData.define(FALLEN_STAR_DATA, new FallenStarSyncData());
    }

    public void addAdditionalSaveData(CompoundTag p_36772_) {
        super.addAdditionalSaveData(p_36772_);
        p_36772_.putShort("life", (short) this.life);
        if (this.lastState != null) {
            p_36772_.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
        }

        p_36772_.putBoolean("inGround", this.inGround);
        p_36772_.putByte("pickup", (byte) this.pickup.ordinal());
        p_36772_.putDouble("damage", this.baseDamage);
    }

    public void readAdditionalSaveData(CompoundTag p_36761_) {
        super.readAdditionalSaveData(p_36761_);
        this.life = p_36761_.getShort("life");
        if (p_36761_.contains("inBlockState", 10)) {
            this.lastState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),
                    p_36761_.getCompound("inBlockState"));
        }

        this.shakeTime = p_36761_.getByte("shake") & 255;
        this.inGround = p_36761_.getBoolean("inGround");
        if (p_36761_.contains("damage", 99)) {
            this.baseDamage = p_36761_.getDouble("damage");
        }

        // this.pickup = AbstractArrow.Pickup.byOrdinal(p_36761_.getByte("pickup"));
        // this.setCritArrow(p_36761_.getBoolean("crit"));
        // this.setPierceLevel(p_36761_.getByte("PierceLevel"));
        // if (p_36761_.contains("SoundEvent", 8)) {
        // this.soundEvent = BuiltInRegistries.SOUND_EVENT
        // .getOptional(new ResourceLocation(p_36761_.getString("SoundEvent")))
        // .orElse(this.getDefaultHitGroundSoundEvent());
        // }

        // this.setShotFromCrossbow(p_36761_.getBoolean("ShotFromCrossbow"));
    }

    public void setNoPhysics(boolean bool) {
        FallenStarSyncData data = getFallenStarData();
        data.noPhysics = bool;
        this.entityData.set(FALLEN_STAR_DATA, data);
        this.noPhysics = bool;
    }

    public void setIsFalling(Boolean bool) {
        FallenStarSyncData data = getFallenStarData();
        data.isFalling = bool;
        this.entityData.set(FALLEN_STAR_DATA, data);
        this.isFalling = bool;
    }

    public FallenStarSyncData getFallenStarData() {
        FallenStarSyncData data = entityData.get(FALLEN_STAR_DATA);
        this.noPhysics = data.noPhysics;
        return entityData.get(FALLEN_STAR_DATA);
    }

    // -----------------------------------------
    // TICK
    // -----------------------------------------

    public void tick() {
        super.tick();

        if (this.inGround) {
            setIsFalling(false);
        }

        boolean flag = this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();

        // if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
        // double d0 = vec3.horizontalDistance();
        // this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float)
        // Math.PI)));
        // this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float)
        // Math.PI)));
        // this.yRotO = this.getYRot();
        // this.xRotO = this.getXRot();
        // }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);

        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
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

        if (this.inGround && !flag) {
            if (this.lastState != blockstate && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level().isClientSide) {
                this.tickDespawn();
            }
            ++this.inGroundTime;

        } else {
            this.inGroundTime = 0;
            Vec3 vec32 = this.position();
            Vec3 vec33 = vec32.add(vec3);
            HitResult hitresult = this.level()
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

                hitresult = null;
            }

            vec3 = this.getDeltaMovement();
            double deltaX = vec3.x;
            double deltaY = vec3.y;
            double deltaZ = vec3.z;

            // if (!this.onGround() && !this.getSyncMoveToCatcher()) {

            // setIsFalling(true);
            // // play normal falling particles
            // for (int i = 0; i < 8; ++i) {
            // this.level().addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_FALLING_STAR.get(),
            // this.getX() + deltaX * (double) i / 4.0D - deltaX * 2.5,
            // this.getY() + deltaY * (double) i / 4.0D - deltaY * 2.5 + 0.55D,
            // this.getZ() + deltaZ * (double) i / 4.0D - deltaZ * 2.5, -deltaX, -deltaY,
            // -deltaZ);

            // this.level().addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
            // this.getX() + this.random.nextGaussian() * 0.5,
            // this.getY() + this.random.nextGaussian() * 0.7,
            // this.getZ() + this.random.nextGaussian() * 0.5,
            // 0D, 0.4D, 0D);
            // }
            // if (getAge() % 2 == 0) {
            // this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(),
            // FALL_SOUND,
            // SoundSource.AMBIENT, 20.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            // }

            // // THIS USES ONGROUND()???? USE IN OR ONGROUND??
            // } else if (!this.onGround() && this.getSyncMoveToCatcher()) {
            // ManaMod.LOGGER.info("GOING TO CATCHER");
            // ManaMod.LOGGER.info("ID: " + this.getId() + " moveToCatcher: " +
            // this.getSyncMoveToCatcher()
            // + " Catcher:" + this.catcher);
            // // play star catcher particles
            // for (int i = 0; i < 4; ++i) {
            // //
            // this.level().addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_STAR_CATCHER.get(),
            // // this.getX() + deltaX * (double) i / 4.0D - deltaX * 1.5,
            // // this.getY() + deltaY * (double) i / 4.0D - deltaY * 1.5 + 0.48D,
            // // this.getZ() + deltaZ * (double) i / 4.0D - deltaZ * 1.5,
            // // -deltaX,
            // // -deltaY,
            // // -deltaZ);

            // // this.level().addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
            // // this.getX() + this.random.nextGaussian() * 0.5,
            // // this.getY() + this.random.nextGaussian() * 0.7,
            // // this.getZ() + this.random.nextGaussian() * 0.5,
            // // 0D, 0.4D, 0D);
            // }
            // if (getAge() % 2 == 0) {
            // this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(),
            // FALL_SOUND,
            // SoundSource.AMBIENT, 5.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

            // }
            // }

            // double d7 = this.getX() + deltaX;
            // double d2 = this.getY() + deltaY;
            // double d3 = this.getZ() + deltaZ;
            // double d4 = vec3.horizontalDistance();

            // if (flag) {
            // this.setYRot((float) (Mth.atan2(-deltaX, -deltaZ) * (double) (180F / (float)
            // Math.PI)));
            // } else {
            // this.setYRot((float) (Mth.atan2(deltaX, deltaZ) * (double) (180F / (float)
            // Math.PI)));
            // }

            // this.setXRot((float) (Mth.atan2(deltaY, d4) * (double) (180F / (float)
            // Math.PI)));
            // this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            // this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            // float f = 0.99F;
            // this.setDeltaMovement(vec3.scale((double) f));

            // if (!this.isNoGravity() && !flag) {
            // Vec3 vec34 = this.getDeltaMovement();
            // this.setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
            // }

            // this.setPos(d7, d2, d3);
            // this.checkInsideBlocks();
        }
    }

    // -----------------------------------------
    // END TICK
    // -----------------------------------------

    private void makeParticle(int p_36877_) {
        int i = 2;
        if (i != -1 && p_36877_ > 0) {
            double d0 = (double) (i >> 16 & 255) / 255.0D;
            double d1 = (double) (i >> 8 & 255) / 255.0D;
            double d2 = (double) (i >> 0 & 255) / 255.0D;

            for (int j = 0; j < p_36877_; ++j) {
                this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(),
                        this.getRandomZ(0.5D), d0, d1, d2);
            }

        }
    }

    // // Projectile
    // public static boolean canSpawn(EntityType<? extends Projectile> p_27578_,
    // LevelAccessor p_27579_,
    // MobSpawnType p_27580_, BlockPos p_27581_, Random p_27582_) {
    // return true;
    // }

    public void setOwner(@Nullable Entity p_36770_) {
        super.setOwner(p_36770_);
        if (p_36770_ instanceof Player) {
            this.pickup = ((Player) p_36770_).getAbilities().instabuild ? FallenStar.Pickup.CREATIVE_ONLY
                    : FallenStar.Pickup.ALLOWED;
        }

    }

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

    public void shootFromRotation(float p_37253_, float p_37254_, float p_37255_, float p_37256_, float p_37257_) {
        float f = -Mth.sin(p_37254_ * ((float) Math.PI / 180F)) * Mth.cos(p_37253_ * ((float) Math.PI / 180F));
        float f1 = -Mth.sin((p_37253_ + p_37255_) * ((float) Math.PI / 180F));
        float f2 = Mth.cos(p_37254_ * ((float) Math.PI / 180F)) * Mth.cos(p_37253_ * ((float) Math.PI / 180F));
        this.shoot((double) f, (double) f1, (double) f2, p_37256_, p_37257_);
    }

    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.lastState = this.level().getBlockState(blockHitResult.getBlockPos());
        super.onHitBlock(blockHitResult);
        Vec3 vec3 = blockHitResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale((double) 0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;
        this.shakeTime = 7;
        this.setSoundEvent(SoundEvents.ARROW_HIT);
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity hitEntity = entityHitResult.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double) f * this.baseDamage, 0.0D, (double) Integer.MAX_VALUE));
        Entity fallenStarOwner = this.getOwner();
        DamageSource damagesource;

        if (fallenStarOwner == null) {
            entityHitResult.getEntity().hurt(this.damageSources().indirectMagic(this, this), (float) i);
        } else {
            entityHitResult.getEntity().hurt(this.damageSources().indirectMagic(this, fallenStarOwner), (float) i);
            if (fallenStarOwner instanceof LivingEntity) {
                ((LivingEntity) fallenStarOwner).setLastHurtMob(hitEntity);
            }
        }

        if (entityHitResult.getEntity().hurt(this.damageSources().indirectMagic(this, this), (float) i)) {
            if (hitEntity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) hitEntity;

                if (fallenStarOwner != null && livingentity != fallenStarOwner && livingentity instanceof Player
                        && fallenStarOwner instanceof ServerPlayer) {
                    ((ServerPlayer) fallenStarOwner).connection
                            .send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }

            }

            this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

        } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == FallenStar.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            }
        }

    }

    // public boolean isNoPhysics() {
    // if (!this.level().isClientSide) {
    // return this.noPhysics;
    // } else {
    // return (this.entityData.get(ID_FLAGS) & 2) != 0;
    // }
    // }

    private boolean shouldFall() {
        return this.inGround && this.level().noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply((double) (this.random.nextFloat() * 0.2F),
                (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
        this.life = 0;
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 1200) {
            this.discard();
        }

    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 p_36758_, Vec3 p_36759_) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, p_36758_, p_36759_,
                this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    // protected abstract ItemStack getPickupItem();

    protected ItemStack getPickupItem() {
        return new ItemStack(ManaItems.FALLEN_STAR_ITEM.get());
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected final SoundEvent getHitGroundSoundEvent() {
        return this.soundEvent;
    }

    public void setSoundEvent(SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
    }

    public static enum Pickup {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;

        public static FallenStar.Pickup byOrdinal(int val) {
            if (val < 0 || val > values().length) {
                val = 0;
            }

            return values()[val];
        }
    }

    public boolean isNoPhysics() {
        return this.noPhysics;
    }

    public void handleEntityEvent(byte p_36869_) {
        if (p_36869_ == 0) {
            int i = 2;
            if (i != -1) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;

                for (int j = 0; j < 20; ++j) {
                    this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(),
                            this.getRandomZ(0.5D), d0, d1, d2);
                }
            }
        } else {
            super.handleEntityEvent(p_36869_);
        }

    }

    // -----------------------------------------
    // GETTERS AND SETTERS
    // -----------------------------------------

    public boolean isInGround() {
        return this.inGround;
    }

}

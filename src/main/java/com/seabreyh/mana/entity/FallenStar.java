package com.seabreyh.mana.entity;

import com.seabreyh.mana.ManaEntityDataSerializers;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.blocks.entity.StarCatcherEntityBlock;
import com.seabreyh.mana.client.renderers.entity.FallenStarSyncData;
import com.seabreyh.mana.event.player.PlayerWishEvent;
import com.seabreyh.mana.registry.ManaItems;
import com.seabreyh.mana.registry.ManaParticles;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner.SpawnPredicate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FallenStar extends AbstractArrow implements SpawnPredicate {

    // Define Data Sync
    private static final EntityDataAccessor<FallenStarSyncData> FALLEN_STAR_DATA = SynchedEntityData.defineId(
            FallenStar.class, ManaEntityDataSerializers.FALLEN_STAR_DATA);

    // private static final EntityDataAccessor<Boolean> IS_FALLING =
    // SynchedEntityData.defineId(
    // FallenStar.class, EntityDataSerializers.BOOLEAN);
    public Boolean isFalling;

    private int age;
    private int maxAge = 23000;

    public float bobOffs;
    private BlockState lastState;
    private double baseDamage = 10.0D;
    private EntityDimensions dimensions;
    float currentTime;
    // private boolean isFalling = true;
    private boolean playerWishedOn = false;
    private Player ownPlayer;
    private boolean isTargeted = false;
    int failedProbability = random.nextInt(10);
    private boolean isFailedLandingStar = false;
    private boolean madeFirstFall = false;
    public AbstractArrow.Pickup pickup = AbstractArrow.Pickup.ALLOWED;

    private boolean moveToCatcher = false;
    private int clientSideCatchStarTickCount;
    private BlockPos catcherPos;
    private StarCatcherEntityBlock pBlockEntity;

    private final SoundEvent HIT_SOUND = SoundEvents.AMETHYST_BLOCK_BREAK;
    private final SoundEvent HIT_GROUND_FAIL = SoundEvents.DRAGON_FIREBALL_EXPLODE;
    private final SoundEvent FALL_SOUND = SoundEvents.AMETHYST_BLOCK_CHIME;
    private final SoundEvent BUBBLE = SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT;

    public FallenStar(EntityType<? extends FallenStar> getEntity, Level world) {
        super(getEntity, world);

        setIsFalling(true);
        // this.isFalling = true;
        this.dimensions = getEntity.getDimensions();

        // determine stars probabiliy to land successfuly on the server side only.
        if (!this.level().isClientSide) {
            if (this.failedProbability < 3 && !isFailedLandingStar && !madeFirstFall) {
                isFailedLandingStar = true;
                madeFirstFall = true;
            }
        }

    }

    public FallenStar(EntityType<? extends FallenStar> getEntity, Level world, Player ownPlayer) {
        this(getEntity, world);
        this.ownPlayer = ownPlayer;

    }
    // -------------------------------------------
    // DATA SYNCING
    // -------------------------------------------

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(FALLEN_STAR_DATA, new FallenStarSyncData());
    }

    public FallenStarSyncData getFallenStarData() {
        return entityData.get(FALLEN_STAR_DATA);
    }

    public void syncFallenStarData() {
        FallenStarSyncData fallenStarData = getFallenStarData();
        if (fallenStarData == null)
            return;

        fallenStarData.update(this);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (!level().isClientSide)
            return;

        if (FALLEN_STAR_DATA.equals(key)) {
            FallenStarSyncData fallenStarData = getFallenStarData();
            if (fallenStarData == null)
                return;
            fallenStarData.apply(this);
        }
    }

    public void getIsFalling() {
        FallenStarSyncData data = getFallenStarData();

        ManaMod.LOGGER.info("GetIsFalling: " + data);

        this.isFalling = data.isFalling;

    }

    public void setIsFalling(Boolean bool) {
        FallenStarSyncData data = getFallenStarData();
        data.isFalling = bool;

        ManaMod.LOGGER.info("Set: " + data);
        data = getFallenStarData();
        ManaMod.LOGGER.info("BOOOL: " + data.isFalling + "id: " + this.getId());
        this.entityData.set(FALLEN_STAR_DATA, data);
        getIsFalling();
    }
    // END ---------------------------------------

    public static boolean canSpawn(EntityType<? extends AbstractArrow> p_27578_, LevelAccessor p_27579_,
            MobSpawnType p_27580_, BlockPos p_27581_, Random p_27582_) {
        return true;
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

    public StarCatcherEntityBlock getStarCatcherEntityBlock() {
        return this.pBlockEntity;
    }

    public boolean getIsTargeted() {
        return this.isTargeted;
    }

    public boolean getMoveToCatcher() {
        return this.moveToCatcher;
    }

    public void setIsTargeted(boolean isTargeted) {
        this.isTargeted = isTargeted;
    }

    public void toStarCatcher(BlockPos catcherPos, StarCatcherEntityBlock pBlockEntity) {
        this.catcherPos = catcherPos;
        this.pBlockEntity = pBlockEntity;
        this.moveToCatcher = true;
        // this.isFalling = false;
        setIsFalling(false);
    }

    public void stopStarCatch() {
        this.moveToCatcher = false;
        this.setNoPhysics(false);
        this.isTargeted = false;
        this.pickup = AbstractArrow.Pickup.ALLOWED;
    }

    public void tick() {
        // ManaMod.LOGGER.info("ID:" + this.getId() +
        // " Age:" + this.getAge() +
        // // " PickUp:" + this.pickup +
        // // " Targeted:" + this.getIsTargeted() +
        // // " MoveToCatcher: " + this.getMoveToCatcher() +
        // " isFalling: " + this.getIsFalling());
        // Star Catcher -------------------------------------
        // SPEED TO MOVE STAR
        // double catchSpeed = 0.8D;
        double catchSpeed = 0.001D;
        int timeTillStartCatch = 130;
        if (moveToCatcher && pBlockEntity.isRemoved()) {
            stopStarCatch();
        }
        if (moveToCatcher && clientSideCatchStarTickCount >= timeTillStartCatch) {
            // Move to the star catcher
            this.pickup = AbstractArrow.Pickup.DISALLOWED;
            this.setNoPhysics(true);
            Vec3 vec3 = new Vec3(catcherPos.getX(), catcherPos.getY(), catcherPos.getZ()).subtract(this.position().x,
                    this.position().y, this.position().z);
            this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.115D * (double) catchSpeed, this.getZ());

            if (this.level().isClientSide) {
                this.yOld = this.getY();
            }

            double d0 = 0.05D * (double) catchSpeed;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(d0)));

            if (this.clientSideCatchStarTickCount == timeTillStartCatch) {
                this.playSound(SoundEvents.EVOKER_CAST_SPELL, 2.0F, 1.0F);
            }

            // If within 0.7 blocks of catcher, catch.
            if (Math.abs(vec3.x) < 0.7 && Math.abs(vec3.y) < 0.7 && Math.abs(vec3.z) < 0.7) {
                this.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 2.0F, 1.0F);
                StarCatcherEntityBlock.craftItem(pBlockEntity);
                discardStar();
            }
        }
        ++this.clientSideCatchStarTickCount;
        // End Star Catcher -------------------------------------

        // SUPER TICK ----------- <<<
        super.tick();
        // SUPER TICK ----------- <<<

        currentTime = this.level().getTimeOfDay(1.0F);
        if (currentTime > 0.75) {
            discardStar();
        }

        if (this.age != -32768) {
            ++this.age;
        }

        // if (this.isFalling && this.ownPlayer != null &&
        // this.ownPlayer.getUseItem().is(Items.SPYGLASS) && !this.playerWishedOn) {
        if (this.isFalling && this.ownPlayer != null && this.ownPlayer.getUseItem().is(Items.SPYGLASS)
                && !this.playerWishedOn) {
            Vec3 dirPlayerToStar = this.position().subtract(this.ownPlayer.position());
            dirPlayerToStar = new Vec3(dirPlayerToStar.x, 0.0, dirPlayerToStar.z).normalize();
            float playerRotX = this.ownPlayer.getXRot();
            float playerRotY = this.ownPlayer.getYRot();

            float shootX = -Mth.sin(playerRotY * ((float) Math.PI / 180F))
                    * Mth.cos(playerRotX * ((float) Math.PI / 180F));
            float shootZ = Mth.cos(playerRotY * ((float) Math.PI / 180F))
                    * Mth.cos(playerRotX * ((float) Math.PI / 180F));

            Vec3 dirPlayerLooking = new Vec3(shootX, 0.0, shootZ).normalize();
            double playerSeesStar = dirPlayerToStar.dot(dirPlayerLooking);

            if (playerSeesStar > 0.98 && playerRotX <= -15F) {
                PlayerWishEvent.starGrantPlayerWish(this.ownPlayer, level());
                playerWishedOn = true;
            }
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

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)) {
            this.clearFire();
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

                if (entityhitresult == null || this.getPierceLevel() <= 0) {
                    break;
                }

                hitresult = null;
            }

            vec3 = this.getDeltaMovement();
            double deltaX = vec3.x;
            double deltaY = vec3.y;
            double deltaZ = vec3.z;

            if (!this.onGround() && !moveToCatcher) {

                setIsFalling(true);
                // play normal falling particles
                for (int i = 0; i < 8; ++i) {
                    this.level().addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_FALLING_STAR.get(),
                            this.getX() + deltaX * (double) i / 4.0D - deltaX * 2.5,
                            this.getY() + deltaY * (double) i / 4.0D - deltaY * 2.5 + 0.55D,
                            this.getZ() + deltaZ * (double) i / 4.0D - deltaZ * 2.5, -deltaX, -deltaY, -deltaZ);

                    this.level().addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
                            this.getX() + this.random.nextGaussian() * 0.5,
                            this.getY() + this.random.nextGaussian() * 0.7,
                            this.getZ() + this.random.nextGaussian() * 0.5,
                            0D, 0.4D, 0D);
                }
                if (this.age % 2 == 0) {
                    this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), FALL_SOUND,
                            SoundSource.AMBIENT, 20.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                }

            } else if (!this.onGround() && moveToCatcher) {
                // play star catcher particles
                for (int i = 0; i < 4; ++i) {
                    this.level().addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_STAR_CATCHER.get(),
                            this.getX() + deltaX * (double) i / 4.0D - deltaX * 1.5,
                            this.getY() + deltaY * (double) i / 4.0D - deltaY * 1.5 + 0.48D,
                            this.getZ() + deltaZ * (double) i / 4.0D - deltaZ * 1.5,
                            -deltaX,
                            -deltaY,
                            -deltaZ);

                    this.level().addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
                            this.getX() + this.random.nextGaussian() * 0.5,
                            this.getY() + this.random.nextGaussian() * 0.7,
                            this.getZ() + this.random.nextGaussian() * 0.5,
                            0D, 0.4D, 0D);
                }
                if (this.age % 2 == 0) {
                    this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), FALL_SOUND,
                            SoundSource.AMBIENT, 5.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

                }
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
            this.setDeltaMovement(vec3.scale((double) f));

            if (!this.isNoGravity() && !flag) {
                Vec3 vec34 = this.getDeltaMovement();
                this.setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
            }

            this.setPos(d7, d2, d3);
            this.checkInsideBlocks();
        }

        if (this.level().isClientSide) {
            if (this.age % 3 == 0) {
                this.level().addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
                        this.getX() + this.random.nextGaussian() * 0.3,
                        this.getY() + 0.4 + this.random.nextGaussian() * 0.5,
                        this.getZ() + this.random.nextGaussian() * 0.3,
                        0D, 0D, 0D);
            }

            if (isInWater()) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getX() + this.random.nextGaussian() * 0.2,
                        this.getY() + 2 + this.random.nextGaussian() * 0.9,
                        this.getZ() + this.random.nextGaussian() * 0.2,
                        0D, 0.1D, 0D);

                this.level().addParticle(ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + this.random.nextGaussian() * 0.2,
                        this.getY() + 1 + this.random.nextGaussian() * 0.2,
                        this.getZ() + this.random.nextGaussian() * 0.2,
                        0D, 2D, 0D);
            }
        }

        if (isInWater() && this.age % 5 == 0) {
            this.level().playSound((Player) null, this.getOnPos(), BUBBLE, SoundSource.AMBIENT,
                    1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }

    private boolean shouldFall() {
        return this.inGround && this.level().noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
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
    protected void onHitEntity(EntityHitResult entityHitResult) {
        setIsFalling(false);
        // this.isFalling = false;
        Entity entity = entityHitResult.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double) f * this.baseDamage, 0.0D, 2.147483647E9D));

        Entity entity1 = this.getOwner();
        DamageSource damagesource;
        if (entity1 == null) {
            entityHitResult.getEntity().hurt(this.damageSources().arrow(this, this), 0.0F);
        } else {
            entityHitResult.getEntity().hurt(this.damageSources().arrow(this, entity1), 0.0F);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity) entity1).setLastHurtMob(entity);
            }
        }

        if (entityHitResult.getEntity().hurt(this.damageSources().arrow(this, entity1), (float) i)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity;
                if (!this.level().isClientSide && this.getPierceLevel() <= 0) {
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
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            }
        }
    }

    private boolean getIsFailedLandingStar() {
        return this.isFailedLandingStar;
    }

    @Override
    protected void onHitBlock(BlockHitResult p_36755_) {
        // this.isFalling = false;
        setIsFalling(false);
        this.lastState = this.level().getBlockState(p_36755_.getBlockPos());
        Vec3 vec3 = p_36755_.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale((double) 0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        if (!isFailedLandingStar) {
            this.playHitSound();
        }
        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte) 0);
        this.setShotFromCrossbow(false);

        // Star has chance to not sucessfully hit ground and crumble.
        if (getIsFailedLandingStar()) {
            discardStarFailedLanding();
        }

    }

    public void discardStarFailedLanding() {
        this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 4.0F);
        this.playSound(SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 1F, 0.2F);
        if (!this.level().isClientSide) { // qty spread velocity
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                    20, 0D, 0D, 0D, 0.7D);
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(),
                    10, 0D, 0D, 0D, 0.1D);
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(),
                    60, 0D, 0D, 0D, 0.7D);
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.SMALL_FLAME, this.getX(), this.getY(),
                    this.getZ(),
                    20, 0D, 0D, 0D, 0.3D);
        }
        this.discard();
    }

    private void playHitSound() {
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), HIT_SOUND, SoundSource.AMBIENT,
                3.5F,
                2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
            if (this.tryPickup(player) && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                player.take(this, 1);
                this.discard();
            }
        }
    }

    protected boolean tryPickup(Player player) {
        if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
            return player.getInventory().add(this.getPickupItem());
        }
        return false;
    }

    public int getAge() {
        return this.age;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public float getSpin(float p_32009_) {
        return ((float) this.getAge() + p_32009_) / 20.0F + this.bobOffs;
    }

    // Call this to remove the start from an environment situation, such as sunrise,
    // or killing after old age.
    public void discardStar() {
        this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
        if (!this.level().isClientSide) { // qty spread velocity
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 1,
                    0D,
                    0D, 0D, 0D);
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(),
                    20,
                    1D, 1D, 1D, 0.3D);
        }
        this.discard();
    }

    @Override
    protected void tickDespawn() {
        ++this.age;
        // After 23000 ticks, roughly a full day cycle, the star will be removed.
        if (this.age >= this.maxAge) {
            discardStar();
        }
    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    protected void doWaterSplashEffect() {
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.PLAYER_SPLASH_HIGH_SPEED,
                SoundSource.AMBIENT, 2.5F,
                0.6F / (this.random.nextFloat() * 0.2F + 0.9F));

        double d2 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.dimensions.width;

        for (int i = 0; (float) i < 1.0F + this.dimensions.width * 20.0F; ++i) {
            this.level().addParticle(ParticleTypes.SPIT, this.getX() + d2,
                    this.getY() + this.random.nextGaussian() * 2.2,
                    this.getZ() + this.random.nextGaussian() * 0.5,
                    0D, 1.8D, 0D);

            this.level().addParticle(ParticleTypes.SPIT, this.getX() + d2,
                    this.getY() + this.random.nextGaussian() * 2.2,
                    this.getZ() + this.random.nextGaussian() * 0.5,
                    0D, 1.8D, 0D);
        }

        for (int j = 0; (float) j < 1.0F + this.dimensions.width * 20.0F; ++j) {
            this.level().addParticle(ParticleTypes.SPIT, this.getX() + d2,
                    this.getY() + this.random.nextGaussian() * 1.2,
                    this.getZ() + this.random.nextGaussian() * 0.5,
                    0D, 0.9D, 0D);

            this.level().addParticle(ParticleTypes.SPIT, this.getX() + d2 + this.random.nextGaussian() * 0.5,
                    this.getY() + this.random.nextGaussian() * 0.7,
                    this.getZ() + this.random.nextGaussian() * 0.5,
                    0D, 0.4D, 0D);

            this.level().addParticle(ParticleTypes.SMOKE, this.getX() + d2,
                    this.getY() + this.random.nextGaussian() * 1.2,
                    this.getZ() + this.random.nextGaussian() * 0.5,
                    0D, 0.9D, 0D);
        }

        this.gameEvent(GameEvent.SPLASH);
    }

    @Override
    public boolean test(EntityType<?> p_47107_, BlockPos p_47108_, ChunkAccess p_47109_) {
        return false;
    }
}

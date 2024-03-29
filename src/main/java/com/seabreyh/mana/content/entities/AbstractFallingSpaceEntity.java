package com.seabreyh.mana.content.entities;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.foundation.event.player.PlayerWishEvent;
import com.seabreyh.mana.foundation.event.world.ShootingStarEvent;
import com.seabreyh.mana.registries.ManaParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractFallingSpaceEntity extends AbstractArrow {

    protected int age;
    protected final int maxAge = 23000;

    public Boolean isFalling = true;
    private Player ownPlayer;

    // check to see if instanceof FallenStarEntity
    private boolean playerWishedOn = false;

    protected BlockState lastState;
    public AbstractArrow.Pickup pickup = AbstractArrow.Pickup.ALLOWED;
    // private BlockState lastState;
    private double baseDamage = 10.0D;
    private float waterInertia = 0.7F;
    private EntityDimensions dimensions;
    float currentTime;
    protected boolean spawnedNaturally = false;
    protected int failedProbability = random.nextInt(20);
    protected int failedDistanceModifier = random.nextInt(100);
    protected boolean isFailedLandingStar;

    // splash variables
    private Vec3 lastPosition = this.position();
    private double travelDistance;

    private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();

    private final float STAR_DESPAWN_TIME = ShootingStarEvent.getStarDespawnTime();
    private final float STAR_SPAWN_START_TIME = ShootingStarEvent.getStarSpawnStartTime();

    protected final SoundEvent HIT_SOUND = SoundEvents.AMETHYST_BLOCK_BREAK;
    protected final SoundEvent HIT_GROUND_FAIL = SoundEvents.DRAGON_FIREBALL_EXPLODE;
    protected final SoundEvent FALL_SOUND = SoundEvents.AMETHYST_BLOCK_CHIME;
    protected final SoundEvent BUBBLE = SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT;
    protected final SoundEvent DISCARD_ENTITY = SoundEvents.FIRE_EXTINGUISH;

    // private static final EntityDataAccessor<FallenStarSyncData> FALLEN_STAR_DATA
    // = SynchedEntityData.defineId(
    // AbstractFallingSpaceEntity.class --,
    // ManaEntityDataSerializers.FALLEN_STAR_DATA);

    protected AbstractFallingSpaceEntity(EntityType<? extends AbstractFallingSpaceEntity> entityType, Level level) {
        super(entityType, level);
        this.dimensions = entityType.getDimensions();
        this.soundEvent = this.getDefaultHitGroundSoundEvent();
    }

    // Shooting Star Event
    public AbstractFallingSpaceEntity(EntityType<? extends AbstractFallingSpaceEntity> getEntity, Level world,
            Player ownPlayer) {
        this(getEntity, world);
        this.ownPlayer = ownPlayer;

    }

    // Block Dispenser
    protected AbstractFallingSpaceEntity(EntityType<? extends AbstractFallingSpaceEntity> p_36711_, double p_36712_,
            double p_36713_,
            double p_36714_, Level p_36715_) {
        this(p_36711_, p_36715_);
        this.setPos(p_36712_, p_36713_, p_36714_);
    }

    @Override
    public void tick() {

        this.baseTick();

        // distance for splash particles
        if (!this.firstTick) {
            travelDistance = lastPosition.distanceTo(this.position());
        }
        this.lastPosition = this.position();

        // check if entity should despawn, server side
        if (!this.level().isClientSide) {
            this.tickDespawn();
        }

        // wish upon star
        if (this.isFalling && this.ownPlayer != null && this.ownPlayer.getUseItem().is(Items.SPYGLASS)
                && !this.playerWishedOn && this instanceof FallenStarEntity) {
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

        // physics
        boolean flag = this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
            this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        // Get the block position and the block type that the entity is currently in
        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level().getBlockState(blockpos);

        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.inGround = true; // use this func that will be overwritten by child
                        this.isFalling = false;
                        break;
                    }
                }
            }
        }

        // FIXME update this to make more reliable inGround and flag, just always tick
        // despawn once entity created
        // in ground and has physics
        if (this.inGround && !flag) {
            // if last block hit != current block in, then start falling
            if (this.lastState != blockstate && this.shouldFall()) {
                this.startFalling();
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
                break;

            }

            vec3 = this.getDeltaMovement();
            double deltaX = vec3.x;
            double deltaY = vec3.y;
            double deltaZ = vec3.z;

            if (this.isFalling && (this.travelDistance > 0.16) && this.tickCount > 3) {
                // play normal falling particles if greater than 0.16 travel distance
                // if (this.level().isClientSide) {
                playTravelEffects(deltaX, deltaY, deltaZ);
                // }

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
            if (this.isInWater()) {
                f = this.waterInertia;
            }
            this.setDeltaMovement(vec3.scale((double) f));

            if (!this.isNoGravity() && !flag) {
                Vec3 vec34 = this.getDeltaMovement();
                this.setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
            }

            this.setPos(d7, d2, d3);
            this.checkInsideBlocks();

        }

        if (this.level().isClientSide) {
            if (this.tickCount % 3 == 0) {
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

        if (isInWater() && this.tickCount % 9 == 0) {
            this.level().playSound((Player) null, this.getOnPos(), BUBBLE, SoundSource.AMBIENT,
                    1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }

    }
    // END TICK ---------------------------------------------------------------

    // ---------------------------------
    // Functions
    // ---------------------------------

    protected void playTravelEffects(double deltaX, double deltaY, double deltaZ) {

        for (int i = 0; i < 8; ++i) {
            this.level().addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_FALLING_STAR.get(),
                    this.getX() + deltaX * (double) i / 4.0D - deltaX * 2.5,
                    this.getY() + deltaY * (double) i / 4.0D - deltaY * 2.5,
                    this.getZ() + deltaZ * (double) i / 4.0D - deltaZ * 2.5,
                    0, 0, 0);

            if (!this.isUnderWater()) {
                this.level().addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
                        this.getX() + this.random.nextGaussian() * 0.5,
                        this.getY() + this.random.nextGaussian() * 0.7,
                        this.getZ() + this.random.nextGaussian() * 0.5,
                        0D, 0.4D, 0D);
            }
        }

        if (this.tickCount % 2 == 0 && !this.isUnderWater()) {
            this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), FALL_SOUND,
                    SoundSource.AMBIENT, 20.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        } else if (this.tickCount % 7 == 0) {
            this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), FALL_SOUND,
                    SoundSource.AMBIENT, 20.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
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

    @Override // override from parent to make custom splash effect
    protected void doWaterSplashEffect() {
        Entity entity = (Entity) (this.isVehicle() && this.getControllingPassenger() != null
                ? this.getControllingPassenger()
                : this);
        float f = entity == this ? 0.2F : 0.9F;
        Vec3 vec3 = entity.getDeltaMovement();

        float f1 = Math.min(1.0F,
                (float) Math
                        .sqrt(vec3.x * vec3.x * (double) 0.2F + vec3.y * vec3.y + vec3.z * vec3.z * (double) 0.2F)
                        * f);

        if (f1 < 0.22F) {
            // big splash
            this.playSound(this.getSwimSplashSound(), f1,
                    1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);

            float f2 = (float) Mth.floor(this.getY());

            for (int i = 0; (float) i < 1.0F + this.dimensions.width * 20.0F; ++i) {
                double d0 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.dimensions.width;
                double d1 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.dimensions.width;
                this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + d0, (double) (f2 + 1.0F), this.getZ() + d1,
                        vec3.x, vec3.y - this.random.nextDouble() * (double) 0.2F, vec3.z);
            }

            for (int j = 0; (float) j < 1.0F + this.dimensions.width * 20.0F; ++j) {
                double d2 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.dimensions.width;
                double d3 = (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.dimensions.width;
                this.level().addParticle(ParticleTypes.SPLASH, this.getX() + d2, (double) (f2 + 1.0F), this.getZ() + d3,
                        vec3.x, vec3.y, vec3.z);
            }
        } else {
            // little splash
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

        }
        this.gameEvent(GameEvent.SPLASH);
    }

    // pickup entity as item
    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && this.inGround) {
            if (this.tryPickup(player) && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                player.take(this, 1);
                this.discard();
            }
        }
    }

    // check if can pickup
    protected boolean tryPickup(Player p_150121_) {
        switch (this.pickup) {
            case ALLOWED:
                return p_150121_.getInventory().add(this.getPickupItem());
            case CREATIVE_ONLY:
                return p_150121_.getAbilities().instabuild;
            default:
                return false;
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult p_36755_) {

        this.playHitSound();

        // change the sound from the parent class
        this.setSoundEvent(HIT_SOUND);

        Vec3 vec3 = p_36755_.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale((double) 0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);

        BlockState blockstate = this.level().getBlockState(p_36755_.getBlockPos());
        blockstate.onProjectileHit(this.level(), blockstate, p_36755_, this);
        this.lastState = this.level().getBlockState(p_36755_.getBlockPos());

        this.pickup = AbstractArrow.Pickup.ALLOWED;
        this.isFalling = false;
        this.inGround = true;

    }

    protected void discardFailedLanding() {
        this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 8.0F);
        this.playSound(SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 1F, 8.0F);
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
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.FLASH, this.getX(),
                    this.getY(), this.getZ(), 1,
                    0D,
                    0D, 0D, 0D);
        }
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {

        Entity entityTarget = entityHitResult.getEntity();
        Entity entityOwner = this.getOwner();
        DamageSource damageSource;
        float f = (float) this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double) f * this.baseDamage, 0.0D, 2.147483647E9D));

        if (entityOwner == null) {
            damageSource = this.damageSources().indirectMagic(this, this);
        } else {
            damageSource = this.damageSources().indirectMagic(this, entityOwner);
            if (entityOwner instanceof LivingEntity) {
                ((LivingEntity) entityOwner).setLastHurtMob(entityTarget);
            }
        }

        entityTarget.hurt(damageSource, (float) i);
        playHitSound();
    }

    protected void playHitSound() {
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), HIT_SOUND, SoundSource.AMBIENT,
                3.5F,
                2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    private boolean shouldFall() {
        return this.inGround && this.level().noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
    }

    private void startFalling() {
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
        this.isFalling = true;
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply((double) (this.random.nextFloat() * 0.2F),
                (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
        this.age = 0;
    }

    // Despawn timer
    @Override
    protected void tickDespawn() {
        checkTimeOfDay();
        this.age++;
        if (this.age >= this.maxAge)
            discardEntity();
    }

    // Discard entity
    public void discardEntity() {
        this.playSound(DISCARD_ENTITY, 1.0F, 1.0F);
        if (!this.level().isClientSide) { // qty spread velocity
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.FLASH, this.getX(),
                    this.getY(), this.getZ(), 1,
                    0D,
                    0D, 0D, 0D);
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY(), this.getZ(),
                    20,
                    1D, 1D, 1D, 0.3D);
        }
        this.discard();
    }

    public void checkTimeOfDay() {
        // only do time despawns for naturally spawned entities.
        if (spawnedNaturally) {
            currentTime = this.level().getTimeOfDay(1.0F);
            if (currentTime > STAR_DESPAWN_TIME || currentTime < STAR_SPAWN_START_TIME)
                discardEntity();
        }
    }

    // ---------------------------------
    // Data Syncing and Saving
    // ---------------------------------

    // Item to pickup, defined in parent class
    protected abstract ItemStack getPickupItem();

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    // Runs when loading world - Load saved entity data
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.isFalling = compoundTag.getBoolean("isFalling");
    }

    // Runs when saving/pausing/leaving world - Save entity data
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("isFalling", this.isFalling);
    }

    // ---------------------------------
    // SETTERS AND GETTERS
    // ---------------------------------

    @Override // override from parent to make entity never catch fire.
    public boolean isOnFire() {
        return false;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    public boolean isInGround() {
        return this.inGround;
    }

    protected void setInGround(boolean inGround) {
        this.inGround = inGround;
    }

    public int getAge() {
        return this.age;
    }

}

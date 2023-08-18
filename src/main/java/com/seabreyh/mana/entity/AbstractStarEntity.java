package com.seabreyh.mana.entity;

import java.util.UUID;
import javax.annotation.Nullable;

import com.seabreyh.mana.ManaEntityDataSerializers;
import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.blocks.entity.StarCatcherEntityBlock;
import com.seabreyh.mana.client.renderers.entity.FallenStarSyncData;
import com.seabreyh.mana.event.player.PlayerWishEvent;
import com.seabreyh.mana.event.world.ShootingStarEvent;
import com.seabreyh.mana.registry.ManaParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractStarEntity extends AbstractArrow {

    // protected boolean inGround;
    private int age; // server var
    private int maxAge = 23000;
    private double catchSpeed = 0.8D;
    private int timeTillStartCatch = 130;
    private boolean moveToCatcher;
    private boolean isTargeted;
    private StarCatcherEntityBlock catcher;
    private BlockPos catcherPos;
    private int clientSideCatchStarTickCount;
    public Boolean isFalling;
    private Player ownPlayer;
    private boolean playerWishedOn = false;
    private BlockState lastState;
    public AbstractArrow.Pickup pickup = AbstractArrow.Pickup.ALLOWED;
    // private BlockState lastState;
    private double baseDamage = 10.0D;
    private float waterInertia = 0.7F;
    private EntityDimensions dimensions;
    float currentTime;
    protected int inGroundTime;
    private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
    private float starDespawnTime = ShootingStarEvent.getStarDespawnTime();
    private float starSpawnStartTime = ShootingStarEvent.getStarSpawnStartTime();
    private boolean hasDoneFirstSplash = false;

    private final SoundEvent HIT_SOUND = SoundEvents.AMETHYST_BLOCK_BREAK;
    private final SoundEvent HIT_GROUND_FAIL = SoundEvents.DRAGON_FIREBALL_EXPLODE;
    private final SoundEvent FALL_SOUND = SoundEvents.AMETHYST_BLOCK_CHIME;
    private final SoundEvent BUBBLE = SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT;
    private final SoundEvent DISCARD_ENTITY = SoundEvents.FIRE_EXTINGUISH;

    private static final EntityDataAccessor<FallenStarSyncData> FALLEN_STAR_DATA = SynchedEntityData.defineId(
            AbstractStarEntity.class, ManaEntityDataSerializers.FALLEN_STAR_DATA);

    protected AbstractStarEntity(EntityType<? extends AbstractStarEntity> entityType, Level level) {
        super(entityType, level);
        this.isFalling = true;
        this.dimensions = entityType.getDimensions();

        ManaMod.LOGGER.info("[Mana]: Creating AbstractStarEntity");
    }

    public AbstractStarEntity(EntityType<? extends AbstractStarEntity> getEntity, Level world, Player ownPlayer) {
        this(getEntity, world);
        this.ownPlayer = ownPlayer;

    }

    @Override
    public void tick() {
        this.baseTick();
        if (moveToCatcher && catcher.isRemoved()) {
            stopStarCatch();
        }

        if (this.wasTouchingWater) {
            hasDoneFirstSplash = true;
            this.isFalling = false;
        }

        // moveToCatcher=true and travelTicks >= TotalTravelTicks
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

            // If within 0.7 blocks of catcher, catch and discard entity.
            if (Math.abs(vec3.x) < 0.7 && Math.abs(vec3.y) < 0.7 && Math.abs(vec3.z) < 0.7) {
                this.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 2.0F, 1.0F);
                StarCatcherEntityBlock.craftItem(catcher);
                discardEntity();
            }
        }
        ++this.clientSideCatchStarTickCount;

        currentTime = this.level().getTimeOfDay(1.0F);
        ManaMod.LOGGER.info(
                "Time: " + currentTime + " Despawn Time: " + starDespawnTime + " Spawn Time: " + starSpawnStartTime);

        if (currentTime > starDespawnTime || currentTime < starSpawnStartTime) {
            discardEntity();
        }
        if (this.age != -32768) {
            ++this.age;
        }

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

        // Get the block position and the block type that the entity is currently in
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
                break;

            }

            vec3 = this.getDeltaMovement();
            double deltaX = vec3.x;
            double deltaY = vec3.y;
            double deltaZ = vec3.z;

            if (!this.onGround() && !moveToCatcher) {
                // play normal falling particles
                for (int i = 0; i < 8; ++i) {
                    this.level().addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_FALLING_STAR.get(),
                            this.getX() + deltaX * (double) i / 4.0D - deltaX * 2.5,
                            this.getY() + deltaY * (double) i / 4.0D - deltaY * 2.5 + 0.55D,
                            this.getZ() + deltaZ * (double) i / 4.0D - deltaZ * 2.5, -deltaX, -deltaY, -deltaZ);

                    if (!this.isUnderWater()) {
                        this.level().addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
                                this.getX() + this.random.nextGaussian() * 0.5,
                                this.getY() + this.random.nextGaussian() * 0.7,
                                this.getZ() + this.random.nextGaussian() * 0.5,
                                0D, 0.4D, 0D);
                    }
                }

                if (this.age % 2 == 0 && !this.isUnderWater()) {
                    this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), FALL_SOUND,
                            SoundSource.AMBIENT, 20.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                } else if (this.age % 7 == 0) {
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
    // END TICK ---------------------------------------------------------------

    // ---------------------------------
    // Functions
    // ---------------------------------
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

    @Override
    protected void doWaterSplashEffect() {
        if (!hasDoneFirstSplash) {
            Entity entity = (Entity) (this.isVehicle() && this.getControllingPassenger() != null
                    ? this.getControllingPassenger()
                    : this);
            float f = entity == this ? 0.2F : 0.9F;
            Vec3 vec3 = entity.getDeltaMovement();
            float f1 = Math.min(1.0F,
                    (float) Math
                            .sqrt(vec3.x * vec3.x * (double) 0.2F + vec3.y * vec3.y + vec3.z * vec3.z * (double) 0.2F)
                            * f);

            float splashAmplifier = f1 * 5;

            ManaMod.LOGGER.info(" " + f1 + " am " + splashAmplifier);
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
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && (this.inGround || this.isNoPhysics())) {
            if (this.tryPickup(player) && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                player.take(this, 1);
                this.discard();
            }
        }
    }

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

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    @Override
    public boolean isNoPhysics() {
        if (!this.level().isClientSide) {
            return this.noPhysics;
        } else {
            return (getSyncedStarData().noPhysics);
        }
    }

    public void toStarCatcher(BlockPos catcherPos, StarCatcherEntityBlock pBlockEntity) {
        // MAKE THIS RUN IN CLIENT TOO WHEN CALLED FROM SERVER
        this.catcherPos = catcherPos;
        setCatcher(pBlockEntity);
        this.moveToCatcher = true;
        this.isFalling = false;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {

        this.isFalling = false;
        Entity entity = entityHitResult.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double) f * this.baseDamage, 0.0D, 2.147483647E9D));

        Entity entity1 = this.getOwner();
        DamageSource damagesource;

        if (entity1 == null) {
            // If no owner, use this as damage source
            damagesource = this.damageSources().indirectMagic(this, this);
        } else {
            // If owner, use this as damage source
            damagesource = this.damageSources().indirectMagic(this, entity1);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity) entity1).setLastHurtMob(entity);
            }
        }

        // If can damage, damages entity if true, otherwise go to next else statement
        if (entity.hurt(damagesource, (float) i)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity;

                // this.doPostHurtEffects(livingentity);
                if (entity1 != null && livingentity != entity1 && livingentity instanceof Player
                        && entity1 instanceof ServerPlayer && !this.isSilent()) {
                    // TODO ???
                    ((ServerPlayer) entity1).connection
                            .send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }
            }

            // playHitSound();

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

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected void onHitBlock(BlockHitResult p_36755_) {
        this.setSoundEvent(HIT_SOUND);
        this.isFalling = false;
        this.inGround = true;

        BlockState blockstate = this.level().getBlockState(p_36755_.getBlockPos());
        blockstate.onProjectileHit(this.level(), blockstate, p_36755_, this);
        this.lastState = this.level().getBlockState(p_36755_.getBlockPos());
        Vec3 vec3 = p_36755_.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale((double) 0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);

        playHitSound();
    }

    private void playHitSound() {
        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), HIT_SOUND, SoundSource.AMBIENT,
                3.5F,
                2F / (this.random.nextFloat() * 0.2F + 0.9F));
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

    public void stopStarCatch() {
        this.moveToCatcher = false;
        this.setNoPhysics(false);
        this.isTargeted = false;
        this.pickup = AbstractArrow.Pickup.ALLOWED;
    }

    // Despawn timer
    @Override
    protected void tickDespawn() {
        this.age++;
        // After 23000 ticks, roughly a full day cycle, the star will be removed.
        if (this.age >= this.maxAge) {
            discardEntity();
        }
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

    // Item to pickup, defined in parent class
    protected abstract ItemStack getPickupItem();

    // ---------------------------------
    // DATA SYNCING
    // ---------------------------------

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(FALLEN_STAR_DATA, new FallenStarSyncData());
    }

    // Runs when loading world - Load saved entity data
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.moveToCatcher = compoundTag.getBoolean("moveToCatcher");
    }

    // Runs when saving/pausing/leaving world - Save entity data
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("moveToCatcher", this.moveToCatcher);
    }

    // Data Sync Getters/Setters---------------------

    // Used by setter funcs that update vars which are synced between client/server
    private void updateSyncedData(String dataName) {
        ManaMod.LOGGER.info("updateSyncedData() called with dataName: " + dataName);
        FallenStarSyncData data = getSyncedStarData();
        switch (dataName) {
            case "noPhysics":
                data.noPhysics = this.noPhysics;
                this.entityData.set(FALLEN_STAR_DATA, data);
                break;
            case "inGround":
                data.inGround = this.inGround;
                this.entityData.set(FALLEN_STAR_DATA, data);
                break;
            case "pickup":
                data.pickup = this.pickup;
                this.entityData.set(FALLEN_STAR_DATA, data);
                break;
            case "isTargeted":
                data.isTargeted = this.isTargeted;
                this.entityData.set(FALLEN_STAR_DATA, data);
                break;
            case "catcher":
                data.catcher = this.catcher;
                this.entityData.set(FALLEN_STAR_DATA, data);
                break;
            case "moveToCatcher":
                data.moveToCatcher = this.moveToCatcher;
                this.entityData.set(FALLEN_STAR_DATA, data);
                break;
            default:
                ManaMod.LOGGER.warn("updateSyncedData() called with invalid dataName: " + dataName);
                break;
        }
    }

    // Get synced star data
    public FallenStarSyncData getSyncedStarData() {
        return entityData.get(FALLEN_STAR_DATA);
    }

    // // noPhysics GETTER - returns client/server value
    // public boolean getNoPhysics() {
    // if (!this.level().isClientSide) {
    // return this.noPhysics;
    // } else {
    // return getSyncedStarData().noPhysics;
    // }
    // }

    // // noPhysics SETTER
    // @Override
    // public void setNoPhysics(boolean bool) {
    // this.noPhysics = bool;
    // this.updateSyncedData("noPhysics");
    // }

    // // InGround GETTER - returns client/server value
    // public boolean getInGround() {
    // if (!this.level().isClientSide) {
    // return this.noPhysics;
    // } else {
    // return getSyncedStarData().noPhysics;
    // }
    // }

    // InGround SETTER
    public void setInGround(boolean bool) {
        this.inGround = bool;
        this.updateSyncedData("inGround");
    }

    // isTargeted GETTER - returns client/server value
    public boolean getIsTargeted() {
        if (!this.level().isClientSide) {
            return this.isTargeted;
        } else {
            return getSyncedStarData().isTargeted;
        }
    }

    // isTargeted SETTER
    public void setIsTargeted(boolean bool) {
        this.isTargeted = bool;
        this.updateSyncedData("isTargeted");
    }

    // catcher GETTER - returns client/server value
    public StarCatcherEntityBlock getCatcher() {
        if (!this.level().isClientSide) {
            return this.catcher;
        } else {
            return getSyncedStarData().catcher;
        }
    }

    // catcher SETTER
    public void setCatcher(StarCatcherEntityBlock starCatcher) {
        this.catcher = starCatcher;
        this.updateSyncedData("catcher");
    }

    // moveToCatcher GETTER - returns client/server value
    public boolean getMoveToCatcher() {
        if (!this.level().isClientSide) {
            // Return this if server
            return this.moveToCatcher;
        } else {
            // Return this if client
            return getSyncedStarData().moveToCatcher;
        }
    }

    // moveToCatcher SETTER
    public void setMoveToCatcher(boolean bool) {
        this.moveToCatcher = bool;
        this.updateSyncedData("moveToCatcher");
    }
    // ---------------------------------
    // BASIC SETTERS AND GETTERS
    // ---------------------------------

    public int getAge() {
        return this.age;
    }

}

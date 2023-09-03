package com.seabreyh.mana.content.entities;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.blocks.block_entities.StarCatcherBlockEntity;
import com.seabreyh.mana.registries.ManaEntities;
import com.seabreyh.mana.registries.ManaItems;
import com.seabreyh.mana.registries.ManaParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FallenStarEntity extends AbstractFallingSpaceEntity {

    private double catchSpeed = 0.8D;
    private int timeTillStartCatch = 130;
    public StarCatcherBlockEntity catcher;
    public BlockPos catcherPos;
    private boolean isTargeted;
    public boolean moveToCatcher;
    private int clientSideCatchStarTickCount = 0;

    public FallenStarEntity(EntityType<? extends FallenStarEntity> entity, Level level) {
        super(entity, level);
    }

    // Shooting Star Event
    public FallenStarEntity(EntityType<? extends FallenStarEntity> entity, Level level, Player player) {
        super(entity, level, player);
    }

    // Block Dispenser
    public FallenStarEntity(Level level, double posX, double posY, double posZ) {
        super(ManaEntities.FALLEN_STAR.get(), posX, posY, posZ, level);
    }

    @Override
    public void tick() {

        super.tick();

        // physics - inground
        if (this.level().isClientSide) {
            this.noPhysics = false;
        } else {
            this.noPhysics = !this.level().noCollision(this, this.getBoundingBox().deflate(0.3D));
            if (this.noPhysics) {
                this.moveTowardsClosestSpace(this.getX(),
                        (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2D,
                        this.getZ());
            }
        }

        // check if catcher was removed
        if (catcher != null) {
            if (catcher.isRemoved() || !catcher.hasNotReachedStackLimit()) {
                stopStarCatch();
            }
        }

        // physics for move to catcher
        if (moveToCatcher && clientSideCatchStarTickCount >= timeTillStartCatch) {
            // Move to the star catcher
            // this.pickup = AbstractArrow.Pickup.DISALLOWED;
            this.setNoPhysics(true);
            Vec3 vec3 = new Vec3(catcherPos.getX(), catcherPos.getY(), catcherPos.getZ()).subtract(this.position().x,
                    this.position().y, this.position().z);
            this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.115D * (double) catchSpeed, this.getZ());

            if (level().isClientSide) {
                this.yOld = this.getY();
            }

            double d0 = 0.05D * (double) catchSpeed;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(d0)));

            // play start catch sound
            if (this.clientSideCatchStarTickCount == timeTillStartCatch) {
                this.playSound(SoundEvents.EVOKER_CAST_SPELL, 2.0F, 1.0F);
            }

            // If within 0.7 blocks of catcher, catch and discard entity.
            if (Math.abs(vec3.x) < 0.7 && Math.abs(vec3.y) < 0.7 && Math.abs(vec3.z) < 0.7) {
                this.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 2.0F, 1.0F);
                catcher.craftItem();
                discard();
            }
        }

        if (this.isFalling) {
            this.clientSideCatchStarTickCount = 0;
        } else {
            ++this.clientSideCatchStarTickCount;
        }
    }
    // END TICK ---------------------------------------------------------------

    // ---------------------------------
    // Functions
    // ---------------------------------

    @Override
    protected void onHitBlock(BlockHitResult p_36755_) {
        this.setSoundEvent(HIT_SOUND);
        this.isFalling = false;
        if (!moveToCatcher) {
            this.inGround = true;
        }
        BlockState blockstate = this.level().getBlockState(p_36755_.getBlockPos());
        blockstate.onProjectileHit(this.level(), blockstate, p_36755_, this);
        this.lastState = this.level().getBlockState(p_36755_.getBlockPos());
        Vec3 vec3 = p_36755_.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale((double) 0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);

        playHitSound();
    }

    @Override // override parent class function to include catcher effects
    protected void playTravelEffects(double deltaX, double deltaY, double deltaZ) {
        if (!moveToCatcher) {
            // normal falling effects
            super.playTravelEffects(deltaX, deltaY, deltaZ);

        } else {
            // star catcher effects
            for (int i = 0; i < 4; ++i) {
                this.level().addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_STAR_CATCHER.get(),
                        this.getX() + deltaX * (double) i / 4.0D - deltaX * 1.5,
                        this.getY() + deltaY * (double) i / 4.0D - deltaY * 1.5 + 0.48D,
                        this.getZ() + deltaZ * (double) i / 4.0D - deltaZ * 1.5,
                        -deltaX, -deltaY, -deltaZ);

                if (!this.isUnderWater() && this.tickCount % 10 == 0) {
                    this.level().addParticle(ManaParticles.TWINKLE_PARTICLE.get(),
                            this.getX() + this.random.nextGaussian() * 0.5,
                            this.getY() + this.random.nextGaussian() * 0.7,
                            this.getZ() + this.random.nextGaussian() * 0.5,
                            0D, 0.4D, 0D);
                }
            }
            if (this.tickCount % 2 == 0) {
                this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), FALL_SOUND,
                        SoundSource.AMBIENT, 5.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

            }
        }
    }

    public void toStarCatcher(BlockPos catcherPos) {
        this.isTargeted = true;
        this.catcherPos = catcherPos;
        setCatcher(catcherPos);
        this.moveToCatcher = true;
        this.isFalling = false;
        this.inGround = false;
    }

    public void stopStarCatch() {
        this.isTargeted = false;
        this.catcherPos = null;
        this.catcher = null;
        this.moveToCatcher = false;
        this.isFalling = true;
        this.setNoPhysics(false);
        // this.pickup = AbstractArrow.Pickup.ALLOWED;
        this.clientSideCatchStarTickCount = 0;
    }

    // ---------------------------------
    // Data Syncing and Saving
    // ---------------------------------

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    // Runs when loading world - Load saved entity data
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.moveToCatcher = compoundTag.getBoolean("moveToCatcher");
        if (compoundTag.contains("catcherPos")) {
            catcherPos = NbtUtils.readBlockPos(compoundTag.getCompound("catcherPos"));
            setCatcher(catcherPos);
        }
    }

    // Runs when saving/pausing/leaving world - Save entity data
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("moveToCatcher", this.moveToCatcher);
        if (catcherPos != null) {
            compoundTag.put("catcherPos", NbtUtils.writeBlockPos(catcherPos));
        }

    }

    // ---------------------------------
    // SETTERS AND GETTERS
    // ---------------------------------

    // Override parent function to make sure its not moving to catcher, otherwise
    // allow the var update.
    @Override
    protected void setInGround(boolean inGround) {
        if (!moveToCatcher) {
            this.inGround = inGround;
        }
    }

    // GETTER - isTargeted
    public boolean getIsTargeted() {
        return this.isTargeted;
    }

    // SETTER - isTargeted
    public void setIsTargeted(boolean bool) {
        this.isTargeted = bool;
        // this.updateSyncedData("isTargeted");
    }

    // GETTER - catcher
    public StarCatcherBlockEntity getCatcher() {
        return this.catcher;
    }

    // SETTER - catcher
    public void setCatcher(BlockPos catcherPos) {
        BlockEntity blockEntity = level().getBlockEntity(catcherPos);
        if (blockEntity instanceof StarCatcherBlockEntity) {
            this.catcher = (StarCatcherBlockEntity) blockEntity;
        } else {
            ManaMod.LOGGER.error("Error at setCatcher() - Block entity at Pos: " + catcherPos
                    + " does not appear to be Star Catcher. Entity Found: " + blockEntity);
        }
    }

    public boolean readyToCatch() {
        return ((this.clientSideCatchStarTickCount >= timeTillStartCatch) && !this.isFalling);
    }

    // Get pickup item from item class
    protected ItemStack getPickupItem() {
        return new ItemStack(ManaItems.FALLEN_STAR_ITEM.get());
    }

}
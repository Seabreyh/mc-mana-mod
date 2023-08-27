package com.seabreyh.mana.content.blocks.block_entities;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.entities.FallenStarEntity;
import com.seabreyh.mana.foundation.client.gui.screens.StarCatcherMenu;
import com.seabreyh.mana.foundation.networking.ManaMessages;
import com.seabreyh.mana.foundation.networking.packet.FallenStarS2CPacket;
import com.seabreyh.mana.registries.ManaBlockEntities;
import com.seabreyh.mana.registries.ManaItems;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraftforge.items.IItemHandler;

public class BlockEntityStarCatcher extends BlockEntity implements MenuProvider {
    public int tickCount;
    public int catchCount;
    public float activeRotation;
    public float rotationSpeed = 1F;
    private final List<BlockPos> effectBlocks = Lists.newArrayList();

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public BlockEntityStarCatcher(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ManaBlockEntities.STAR_CATCHER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        // ManaMod.LOGGER.info("StarCatcher ENTITY BLOCK init");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("Star Catcher");
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    // public static void setRotationSpeed(float speed, BlockEntityStarCatcher
    // entity) {
    // entity.rotationSpeed = speed;
    // }

    // private void setRotationSpeedInternal(float speed) {
    // this.rotationSpeed = speed;
    // }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new StarCatcherMenu(pContainerId, pInventory, this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        // https://docs.minecraftforge.net/en/1.20.x/datastorage/capabilities/
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, BlockEntityStarCatcher pBlockEntity) {

        if (pLevel.isClientSide) {
            // ManaMod.LOGGER
            // .info("ID" + pBlockEntity.worldPosition.getZ() + " speed: " +
            // pBlockEntity.rotationSpeed
            // + " catchCount: "
            // + pBlockEntity.catchCount);

            if (pBlockEntity.catchCount > 0) {
                float modifier = Math.min((float) pBlockEntity.catchCount * 10.0f, 20.0f);
                pBlockEntity.rotationSpeed = (3.0f + modifier);
            } else {

                pBlockEntity.rotationSpeed = (1.0f);
            }
        }

        pBlockEntity.locateStars(pLevel, pPos, pState, pBlockEntity);

        List<BlockPos> list = pBlockEntity.effectBlocks;
        // animationTick(pLevel, pPos, list, pBlockEntity.tickCount);
        ++pBlockEntity.activeRotation;
    }

    private static void animationTick(Level p_155419_, BlockPos p_155420_, List<BlockPos> p_155421_, int p_155423_) {
        double d0 = (double) (Mth.sin((float) (p_155423_ + 35) * 0.1F) / 2.0F + 0.5F);
        d0 = (d0 * d0 + d0) * (double) 0.3F;
    }

    private static void locateStars(Level plevel, BlockPos pPos, BlockState pState,
            BlockEntityStarCatcher pBlockEntity) {
        if (!plevel.isClientSide()) {

            if (pBlockEntity.hasNotReachedStackLimit()) {

                AABB area = pBlockEntity.getRenderBoundingBox().inflate(80.0D, 80.0D, 80.0D);
                List<FallenStarEntity> fallenStars = plevel.getEntitiesOfClass(FallenStarEntity.class, area);

                for (FallenStarEntity foundStar : fallenStars) {
                    // make sure star can only be targeted by one star catcher

                    if (foundStar.readyToCatch()) {

                        if (foundStar.getIsTargeted() == false) {
                            if (plevel instanceof ServerLevel) {
                                ManaMessages.sendToNear(plevel, pBlockEntity.getBlockPos(), 60,
                                        new FallenStarS2CPacket(pBlockEntity.getBlockPos(), foundStar.getId(), true));
                            }
                            foundStar.toStarCatcher(pBlockEntity.getBlockPos());

                        }

                    }
                }

            } else {
                AABB area = pBlockEntity.getRenderBoundingBox().inflate(80.0D, 80.0D, 80.0D);
                List<FallenStarEntity> fallenStars = plevel.getEntitiesOfClass(FallenStarEntity.class, area);

                for (FallenStarEntity foundStar : fallenStars) {

                    if (foundStar.getIsTargeted() && foundStar.getCatcher() == pBlockEntity) {
                        if (plevel instanceof ServerLevel) {
                            ManaMessages.sendToNear(plevel, pBlockEntity.getBlockPos(), 60,
                                    new FallenStarS2CPacket(pBlockEntity.getBlockPos(), foundStar.getId(), false));
                        }

                        foundStar.stopStarCatch();

                    }
                }
            }
        } else {
            // Client side only needs to keep track of how many stars are being caught
            // in order to set the spin speed in the renderer

            AABB area = pBlockEntity.getRenderBoundingBox().inflate(80.0D, 80.0D, 80.0D);
            List<FallenStarEntity> fallenStars = plevel.getEntitiesOfClass(FallenStarEntity.class, area);

            pBlockEntity.catchCount = 0;
            for (FallenStarEntity foundStar : fallenStars) {

                // ManaMod.LOGGER.info("Stars catcher: " + foundStar.getCatcher() + " this
                // catcher: " + pBlockEntity);

                if (foundStar.getIsTargeted() && foundStar.getCatcher() == pBlockEntity) {
                    ++pBlockEntity.catchCount;
                }

                // // Dummy statement to get linter to not complain about unused vars
                // foundStar.equals(foundStar);
            }
        }
    }

    public void craftItem() {
        if (hasNotReachedStackLimit()) {
            this.itemHandler.setStackInSlot(0, new ItemStack(ManaItems.FALLEN_STAR_ITEM.get(),
                    this.itemHandler.getStackInSlot(0).getCount() + 1));
        }
    }

    public boolean hasNotReachedStackLimit() {
        return this.itemHandler.getStackInSlot(0).getCount() < this.itemHandler.getStackInSlot(0).getMaxStackSize();
    }

}
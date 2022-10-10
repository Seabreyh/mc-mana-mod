package com.seabreyh.mana.blocks.entity;

import com.seabreyh.mana.gui.menus.StaffTableMenu;
import com.seabreyh.mana.registry.ManaBlockEntities;
import com.seabreyh.mana.registry.ManaItems;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class StaffTableEntityBlock extends BlockEntity implements MenuProvider {
    public int tickCount;
    public int catchCount;
    private float activeRotation;
    private float rotationSpeed = 6F;
    private final List<BlockPos> effectBlocks = Lists.newArrayList();

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public StaffTableEntityBlock(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ManaBlockEntities.STAFF_TABLE_ENTITY_BLOCK.get(), pWorldPosition, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Staff Table");
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public static void setRotationSpeed(float speed, StaffTableEntityBlock entity) {
        entity.rotationSpeed = speed;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new StaffTableMenu(pContainerId, pInventory, this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, StaffTableEntityBlock pBlockEntity) {
        if (pLevel.isClientSide()) {
            if (pBlockEntity.catchCount > 0) {
                float modifier = Math.min((float) pBlockEntity.catchCount * 10.0f, 20.0f);
                StaffTableEntityBlock.setRotationSpeed(6.0f + modifier, pBlockEntity);
            } else {
                StaffTableEntityBlock.setRotationSpeed(3.0F, pBlockEntity);
            }
        }

        // locateStars(pLevel, pBlockEntity, pPos, pState);

        List<BlockPos> list = pBlockEntity.effectBlocks;
        animationTick(pLevel, pPos, list, pBlockEntity.tickCount);
        ++pBlockEntity.activeRotation;
    }

    private static void animationTick(Level p_155419_, BlockPos p_155420_, List<BlockPos> p_155421_, int p_155423_) {
        double d0 = (double) (Mth.sin((float) (p_155423_ + 35) * 0.1F) / 2.0F + 0.5F);
        d0 = (d0 * d0 + d0) * (double) 0.3F;
    }

    public float getActiveRotation(float p_59198_) {
        return (this.activeRotation + p_59198_) * -0.0375F * getRotationSpeed();
    }

    // private static void locateStars(Level plevel, StaffTableEntityBlock pBlockEntity, BlockPos pPos,
    //         BlockState pState) {

    //     if (!plevel.isClientSide()) {
    //         if (hasNotReachedStackLimit(pBlockEntity)) {

    //             // AABB area = pBlockEntity.getRenderBoundingBox().inflate(80.0D, 80.0D, 80.0D);
    //             // List<FallenStar> fallenStars = plevel.getEntitiesOfClass(FallenStar.class, area);

    //             // for (FallenStar foundStar : fallenStars) {
    //             //     // make sure star can only be targeted by one star catcher
    //             //     if (foundStar.getIsFalling() == false
    //             //             && foundStar.getIsTargeted() == false) {
    //             //         if (foundStar instanceof FallenStar) {
    //             //             if (foundStar.getIsTargeted() == false) {
    //             //                 foundStar.setIsTargeted(true);
    //             //                 foundStar.toStarCatcher(pBlockEntity.getBlockPos(), pBlockEntity);
    //             //                 pBlockEntity.catchCount++;
    //             //             }
    //             //         }

    //             //     }
    //             // }

    //         } else {
    //             // AABB area = pBlockEntity.getRenderBoundingBox().inflate(80.0D, 80.0D, 80.0D);
    //             // List<FallenStar> fallenStars = plevel.getEntitiesOfClass(FallenStar.class, area);

    //             // for (FallenStar foundStar : fallenStars) {
    //             //     if (foundStar.getIsTargeted()) {
    //             //         foundStar.stopStarCatch();
    //             //         pBlockEntity.catchCount--;
    //             //     }
    //             // }
    //         }
    //     } else {
    //         // Client side only needs to keep track of how many stars are being caught
    //         // in order to set the spin speed in the renderer

    //         // AABB area = pBlockEntity.getRenderBoundingBox().inflate(80.0D, 80.0D, 80.0D);
    //         // List<FallenStar> fallenStars = plevel.getEntitiesOfClass(FallenStar.class, area);

    //         // pBlockEntity.catchCount = 0;
    //         // for (FallenStar foundStar : fallenStars) {
    //         //     pBlockEntity.catchCount++;

    //         //     // Dummy statement to get linter to not complain about unused vars
    //         //     foundStar.equals(foundStar);
    //         // }
    //     }
    // }

    public static void craftItem(StaffTableEntityBlock entity) {
        if (hasNotReachedStackLimit(entity)) {
            entity.itemHandler.setStackInSlot(0, new ItemStack(ManaItems.FALLEN_STAR_ITEM.get(),
                    entity.itemHandler.getStackInSlot(0).getCount() + 1));
        }
    }

    private static boolean hasNotReachedStackLimit(StaffTableEntityBlock entity) {
        return entity.itemHandler.getStackInSlot(0).getCount() < entity.itemHandler.getStackInSlot(0).getMaxStackSize();
    }

}
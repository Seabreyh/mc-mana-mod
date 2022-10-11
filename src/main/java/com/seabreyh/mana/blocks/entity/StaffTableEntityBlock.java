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
import net.minecraft.world.item.Items;
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
    private int itemToCraft = 0;

    private final ItemStackHandler itemHandler = new ItemStackHandler(5) { // UPDATE TO THE AMOUNT OF SLOTS YOU HAVE
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

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, StaffTableEntityBlock entity) {
        if (hasRecipe(entity) && hasNotReachedStackLimit(entity)) {
            craftItem(entity);
        }
    }

    public float getActiveRotation(float p_59198_) {
        return (this.activeRotation + p_59198_) * -0.0375F * getRotationSpeed();
    }

    public static void craftItem(StaffTableEntityBlock entity) {
        entity.itemHandler.extractItem(0, 1, false);
        entity.itemHandler.extractItem(1, 1, false);
        entity.itemHandler.extractItem(2, 1, false);
        entity.itemHandler.extractItem(3, 1, false);

        switch (entity.itemToCraft) {
            case 1:
                entity.itemHandler.setStackInSlot(4, new ItemStack(ManaItems.AMETHYST_STAFF.get(),
                        entity.itemHandler.getStackInSlot(4).getCount() + 1));
                entity.itemToCraft = 0;
                break;
            case 2:
                entity.itemHandler.setStackInSlot(4, new ItemStack(ManaItems.EMERALD_STAFF.get(),
                        entity.itemHandler.getStackInSlot(4).getCount() + 1));
                entity.itemToCraft = 0;
                break;
            default:
                break;

        }
    }

    private static boolean hasRecipe(StaffTableEntityBlock entity) {

        if ( // ---------------------------------------
        entity.itemHandler.getStackInSlot(0).getItem() == ManaItems.FILLED_MANA_CAPSULE.get() &&
                entity.itemHandler.getStackInSlot(1).getItem() == Items.AMETHYST_BLOCK &&
                entity.itemHandler.getStackInSlot(2).getItem() == Items.COPPER_INGOT &&
                entity.itemHandler.getStackInSlot(3).getItem() == Items.COPPER_INGOT) {

            boolean hasItemInSlotOne = entity.itemHandler.getStackInSlot(0).getItem() == ManaItems.FILLED_MANA_CAPSULE
                    .get();
            boolean hasItemInSlotTwo = entity.itemHandler.getStackInSlot(1).getItem() == Items.AMETHYST_BLOCK;
            boolean hasItemInSlotThree = entity.itemHandler.getStackInSlot(2).getItem() == Items.COPPER_INGOT;
            boolean hasItemInSlotFour = entity.itemHandler.getStackInSlot(3).getItem() == Items.COPPER_INGOT;
            entity.itemToCraft = 1;
            return hasItemInSlotOne && hasItemInSlotTwo && hasItemInSlotThree && hasItemInSlotFour;
        }
        if ( // ---------------------------------------
        entity.itemHandler.getStackInSlot(0).getItem() == ManaItems.FILLED_MANA_CAPSULE.get() &&
                entity.itemHandler.getStackInSlot(1).getItem() == Items.EMERALD_BLOCK &&
                entity.itemHandler.getStackInSlot(2).getItem() == Items.GOLD_INGOT &&
                entity.itemHandler.getStackInSlot(3).getItem() == Items.GOLD_INGOT) {

            boolean hasItemInSlotOne = entity.itemHandler.getStackInSlot(0).getItem() == ManaItems.FILLED_MANA_CAPSULE
                    .get();
            boolean hasItemInSlotTwo = entity.itemHandler.getStackInSlot(1).getItem() == Items.EMERALD_BLOCK;
            boolean hasItemInSlotThree = entity.itemHandler.getStackInSlot(2).getItem() == Items.GOLD_INGOT;
            boolean hasItemInSlotFour = entity.itemHandler.getStackInSlot(3).getItem() == Items.GOLD_INGOT;
            entity.itemToCraft = 2;
            return hasItemInSlotOne && hasItemInSlotTwo && hasItemInSlotThree && hasItemInSlotFour;
        } else {
            return false;
        }

    }

    private static boolean hasNotReachedStackLimit(StaffTableEntityBlock entity) {
        return entity.itemHandler.getStackInSlot(4).getCount() < entity.itemHandler.getStackInSlot(4).getMaxStackSize();
    }

}
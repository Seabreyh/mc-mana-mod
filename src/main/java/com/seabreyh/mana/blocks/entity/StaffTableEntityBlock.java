package com.seabreyh.mana.blocks.entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.blocks.StaffTable;
import com.seabreyh.mana.gui.menus.StaffTableMenu;
import com.seabreyh.mana.recipies.StaffTableRecipies;
import com.seabreyh.mana.registry.ManaBlockEntities;
import com.seabreyh.mana.registry.ManaItems;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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

        public static void tick(Level pLevel, BlockPos pPos, BlockState pState, StaffTableEntityBlock pBlockEntity) {


            
            if(hasRecipe(pBlockEntity) && hasNotReachedStackLimit(pBlockEntity)) {
                craftItem(pBlockEntity);
                ManaMod.LOGGER.info("1");

            }
            else if(hasRecipe(pBlockEntity)) {
                    craftItem(pBlockEntity);
                    ManaMod.LOGGER.info("2");

            } else {

                }
    }

    public float getActiveRotation(float p_59198_) {
        return (this.activeRotation + p_59198_) * -0.0375F * getRotationSpeed();
    }

    public static void craftItem(StaffTableEntityBlock entity) {

        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<StaffTableRecipies> match = level.getRecipeManager()
                .getRecipeFor(StaffTableRecipies.Type.INSTANCE, inventory, level);

        if(match.isPresent()) {
            ManaMod.LOGGER.info("MATCH PRESENT");
            entity.itemHandler.extractItem(0,1, false);
            entity.itemHandler.extractItem(1,1, false);
            entity.itemHandler.extractItem(2,1, false);
            entity.itemHandler.extractItem(3,1, false);

            entity.itemHandler.setStackInSlot(4, new ItemStack(match.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(4).getCount() + 1));

            // entity.resetProgress();
        }else{
            ManaMod.LOGGER.info("MATCH NOT PRESENT");
        }
                
    }

    private static boolean hasRecipe(StaffTableEntityBlock entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<StaffTableRecipies> match = level.getRecipeManager()
                .getRecipeFor(StaffTableRecipies.Type.INSTANCE, inventory, level);

                return match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem())
                 && hasManaCapsule(entity);
       
    }

    private static boolean hasNotReachedStackLimit(StaffTableEntityBlock entity) {
        return entity.itemHandler.getStackInSlot(4).getCount() < entity.itemHandler.getStackInSlot(4).getMaxStackSize();
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        return inventory.getItem(4).getItem() == output.getItem() || inventory.getItem(4).isEmpty();
    }

    private static boolean hasManaCapsule(StaffTableEntityBlock entity) {
        return entity.itemHandler.getStackInSlot(0).getItem() == ManaItems.FILLED_MANA_CAPSULE.get();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(4).getMaxStackSize() > inventory.getItem(4).getCount();
    }
}
package com.seabreyh.mana.blocks.entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.gui.menus.StaffTableMenu;
import com.seabreyh.mana.recipies.StaffTableRecipies;
import com.seabreyh.mana.registry.ManaBlockEntities;
import com.seabreyh.mana.registry.ManaItems;

import it.unimi.dsi.fastutil.booleans.Boolean2CharFunction;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

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
import net.minecraft.world.inventory.ContainerData;
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
    //POTENTIAL ISSUE 
    private final ItemStackHandler itemHandler = new ItemStackHandler(5) { // UPDATE TO THE AMOUNT OF SLOTS YOU HAVE
        @Override
        protected void onContentsChanged(int slot) {
            ManaMod.LOGGER.info("Contents changed - SLOT: " + slot);

            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public StaffTableEntityBlock(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ManaBlockEntities.STAFF_TABLE_ENTITY_BLOCK.get(), pWorldPosition, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0: return StaffTableEntityBlock.this.progress;
                    case 1: return StaffTableEntityBlock.this.maxProgress;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: StaffTableEntityBlock.this.progress = value; break;
                    case 1: StaffTableEntityBlock.this.maxProgress = value; break;
                }
            }

            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Staff Table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new StaffTableMenu(pContainerId, pInventory, this, this.data);
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
        tag.putInt("staff_table.progress", progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("staff_table.progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, StaffTableEntityBlock pBlockEntity) {
        if(hasRecipe(pBlockEntity)) {
            pBlockEntity.progress++;
            setChanged(pLevel, pPos, pState);
            if(pBlockEntity.progress > pBlockEntity.maxProgress) {
                craftItem(pBlockEntity);
            }
        } else {
            pBlockEntity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean hasRecipe(StaffTableEntityBlock entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }
        //how does the logic understand what slots the items go in to complete the recipe.
        //video reference.
        Optional<StaffTableRecipies> match = level.getRecipeManager()
                .getRecipeFor(StaffTableRecipies.Type.INSTANCE, inventory, level);
        //THIS IS NEVER BEING CALLED
        //crashes when match.isPresent is removed.
        //no match is found and that causes an error? No recipie match found? do I need to have a shaped recipe?
                return match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem())
                 && hasManaCapsule(entity);
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

            entity.resetProgress();
        }else{
            ManaMod.LOGGER.info("MATCH NOT PRESENT");
        }
                
    }

    private void resetProgress() {
        this.progress = 0;
    }


    private static boolean hasNotReachedStackLimit(StaffTableEntityBlock entity) {
        ManaMod.LOGGER.info("hasNotReachedStackLimit: " + String.valueOf(entity.itemHandler.getStackInSlot(4).getCount() < entity.itemHandler.getStackInSlot(4).getMaxStackSize()));
        return entity.itemHandler.getStackInSlot(4).getCount() < entity.itemHandler.getStackInSlot(4).getMaxStackSize();
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        ManaMod.LOGGER.info("canInsertIntoOutputSlot: " + String.valueOf(inventory.getItem(4).getItem() == output.getItem() || inventory.getItem(4).isEmpty()));
        return inventory.getItem(4).getItem() == output.getItem() || inventory.getItem(4).isEmpty();
    }

    private static boolean hasManaCapsule(StaffTableEntityBlock entity) {
        ManaMod.LOGGER.info("hasManaCapsule: " + String.valueOf(entity.itemHandler.getStackInSlot(0).getItem() == ManaItems.FILLED_MANA_CAPSULE.get()));
        return entity.itemHandler.getStackInSlot(0).getItem() == ManaItems.FILLED_MANA_CAPSULE.get();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        ManaMod.LOGGER.info("canInsertAmountIntoOutputSlot: " + String.valueOf(inventory.getItem(4).getMaxStackSize() > inventory.getItem(4).getCount()));
        return inventory.getItem(4).getMaxStackSize() > inventory.getItem(4).getCount();
    }
}
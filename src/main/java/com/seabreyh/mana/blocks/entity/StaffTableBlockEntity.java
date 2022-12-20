package com.seabreyh.mana.blocks.entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.gui.menus.StaffTableMenu;
import com.seabreyh.mana.recipes.StaffTableRecipes;
import com.seabreyh.mana.registry.ManaBlockEntities;
import com.seabreyh.mana.registry.ManaItems;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class StaffTableBlockEntity extends BlockEntity implements MenuProvider {

    Optional<StaffTableRecipes> match;

    private boolean hasOutputItem = false;
    private boolean hasCrafted = false;
    private boolean hasRecipe = false;

    private final ItemStackHandler itemHandler = new ItemStackHandler(5) { // UPDATE TO THE AMOUNT OF SLOTS YOU HAVE
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public StaffTableBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ManaBlockEntities.STAFF_TABLE_ENTITY_BLOCK.get(), pWorldPosition, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return StaffTableBlockEntity.this.progress;
                    case 1:
                        return StaffTableBlockEntity.this.maxProgress;
                    default:
                        return 0;
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        StaffTableBlockEntity.this.progress = value;
                        break;
                    case 1:
                        StaffTableBlockEntity.this.maxProgress = value;
                        break;
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

    // SimpleContainer inventory = new
    // SimpleContainer(entity.itemHandler.getSlots());

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, StaffTableBlockEntity entity) {

        if (!pLevel.isClientSide) {
            // check to see if there is a recipe, if so run if statement.
            if (hasRecipe(entity)) {
                entity.hasRecipe = true;
                Level levelTick = entity.level;
                SimpleContainer inventoryTick = new SimpleContainer(entity.itemHandler.getSlots());
                entity.match = levelTick.getRecipeManager().getRecipeFor(StaffTableRecipes.Type.INSTANCE, inventoryTick,
                        levelTick);
                ManaMod.LOGGER.info(entity.match.get().getIngredients().toString());

                // try {
                // ManaMod.LOGGER.debug(entity.match.get().getResultItem().getItem().toString());
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                if (entity.match == null) {
                    ManaMod.LOGGER.info("match is null");
                }

            } else {
                entity.hasRecipe = false;
            }
            // if has recipe AND output slot is empty
            if (entity.hasRecipe && isOutputEmpty(entity)) {
                showOutput(entity);
            }

            // REMOVE RECIPE
            // if no item in output, BUT output has been displayed and recipe is still in
            // slots, player assumed to have taken output item. Remove recipe.
            if (!entity.hasOutputItem && entity.hasCrafted && entity.hasRecipe) {
                takeRecipeItems(entity);
                entity.hasRecipe = false;
                entity.hasOutputItem = false;
                entity.hasCrafted = false;
            }

            // REMOVE OUTPUT ITEM
            // if has output item, and output has been crafted, but no recipe, remove
            // output. Player assumed to have taken a recipe item back out.
            // MAY NEED TO CHECK AGAIN FOR RECIPE HERE RATHER THAN RELYING ON VARIABLE.
            if (entity.hasOutputItem && entity.hasCrafted && !entity.hasRecipe) {
                entity.itemHandler.extractItem(4, 1, false);
                entity.hasOutputItem = false;
                entity.hasCrafted = false;
            }
        }
    }

    private static boolean isOutputEmpty(StaffTableBlockEntity entity) {
        return ItemStack.matches(entity.itemHandler.getStackInSlot(4), new ItemStack(Items.AIR, 1));
    }

    private static boolean hasRecipe(StaffTableBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        entity.match = level.getRecipeManager()
                .getRecipeFor(StaffTableRecipes.Type.INSTANCE, inventory, level);

        // check here per inventory slot for each ingrediant in the json file

        return entity.match.isPresent() && hasManaCapsule(entity);
    }

    public static void showOutput(StaffTableBlockEntity entity) {

        // try {
        // ManaMod.LOGGER.debug(entity.match.get().getResultItem().getItem().toString());
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // entity.itemHandler.setStackInSlot(4, new
        // ItemStack(entity.match.get().getResultItem().getItem(),
        // entity.itemHandler.getStackInSlot(4).getCount() + 1));

        entity.hasOutputItem = true;
        entity.hasCrafted = true;
    }

    public static void takeRecipeItems(StaffTableBlockEntity entity) {
        BlockPos pPos = entity.getBlockPos();
        Level level = entity.getLevel();

        entity.itemHandler.extractItem(0, 1, false);
        entity.itemHandler.extractItem(1, 1, false);
        entity.itemHandler.extractItem(2, 1, false);
        entity.itemHandler.extractItem(3, 1, false);

        entity.itemHandler.insertItem(0, new ItemStack(ManaItems.EMPTY_MANA_CAPSULE.get()), false);

        level.playSound((Player) null, pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.BOTTLE_EMPTY,
                SoundSource.AMBIENT, 10.0F, 1F);
    }

    private static boolean hasNotReachedStackLimit(StaffTableBlockEntity entity) {
        return entity.itemHandler.getStackInSlot(4).getCount() < entity.itemHandler.getStackInSlot(4).getMaxStackSize();
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        return inventory.getItem(4).getItem() == output.getItem() || inventory.getItem(4).isEmpty();
    }

    private static boolean hasManaCapsule(StaffTableBlockEntity entity) {
        return entity.itemHandler.getStackInSlot(0).getItem() == ManaItems.FILLED_MANA_CAPSULE.get();
    }

    // private static boolean hasRecipeItem(StaffTableBlockEntity entity, int
    // itemNumber){
    // return entity.itemHandler.getStackInSlot(itemNumber).getItem() ==
    // entity.match.get().getIngredients()

    // entity.itemHandler.setStackInSlot(itemNumber, new
    // ItemStack(entity.match.get().getResultItem().getItem(),
    // entity.itemHandler.getStackInSlot(4).getCount() + 1));

    // }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(4).getMaxStackSize() > inventory.getItem(4).getCount();
    }
}
package com.seabreyh.mana.blocks.entity;

import com.seabreyh.mana.entity.FallenStar;
import com.seabreyh.mana.registry.ManaBlockEntities;
import com.seabreyh.mana.registry.ManaItems;
import com.seabreyh.mana.screen.StarCatcherMenu;

import java.util.List;
import java.util.Random;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class StarCatcherEntityBlock extends BlockEntity implements MenuProvider {
    public int tickCount;
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

    public StarCatcherEntityBlock(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ManaBlockEntities.STAR_CATCHER_ENTITY_BLOCK.get(), pWorldPosition, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Star Catcher");
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }
    public static void setRotationSpeed(float speed, StarCatcherEntityBlock entity) {
        entity.rotationSpeed = speed;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new StarCatcherMenu(pContainerId, pInventory, this);
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
    public void invalidateCaps()  {
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

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, StarCatcherEntityBlock pBlockEntity) {

        locateStars(pLevel, pBlockEntity, pPos, pState);

        List<BlockPos> list = pBlockEntity.effectBlocks;
        animationTick(pLevel, pPos, list, pBlockEntity.tickCount);
        ++pBlockEntity.activeRotation;
    }


    private static void animationTick(Level p_155419_, BlockPos p_155420_, List<BlockPos> p_155421_, int p_155423_) {
        double d0 = (double)(Mth.sin((float)(p_155423_ + 35) * 0.1F) / 2.0F + 0.5F);
        d0 = (d0 * d0 + d0) * (double)0.3F;
     }

    public float getActiveRotation(float p_59198_) {
        return (this.activeRotation + p_59198_) * -0.0375F * getRotationSpeed();
    }

   private static void locateStars(Level plevel, StarCatcherEntityBlock pBlockEntity, BlockPos pPos, BlockState pState) {
        FallenStar foundTarget = null;
        if(hasNotReachedStackLimit(pBlockEntity) && foundTarget == null) {

        AABB area = pBlockEntity.getRenderBoundingBox().inflate(80.0D, 80.0D, 80.0D);
        List<FallenStar> fallenStars = plevel.getEntitiesOfClass(FallenStar.class, area);

        for (FallenStar foundStar : fallenStars) {
            foundTarget = foundStar;
            //make sure star can only be targeted by one star catcher
                if(foundTarget.getIsFalling() == false && foundTarget.getIsTargeted() == false) {
                    if (foundTarget != null && foundTarget instanceof FallenStar) {
                        if(foundTarget.getIsTargeted() == false){
                            foundTarget.setIsTargeted(true);
                            foundTarget.toStarCatcher(pBlockEntity.getBlockPos(), pBlockEntity);
                        }
                        foundTarget = null;
                    } 
                    
                }
            }
        }
    }

   public static void craftItem(StarCatcherEntityBlock entity) {
       entity.itemHandler.setStackInSlot(0, new ItemStack(ManaItems.FALLEN_STAR_ITEM.get(),
               entity.itemHandler.getStackInSlot(0).getCount() + 1));
   }

//    private static boolean hasRecipe(StarCatcherEntityBlock entity) {
//        boolean hasItemInWaterSlot = PotionUtils.getPotion(entity.itemHandler.getStackInSlot(0)) == Potions.WATER;
//        boolean hasItemInFirstSlot = entity.itemHandler.getStackInSlot(1).getItem() == ManaItems.FALLEN_STAR_ITEM.get();
//        boolean hasItemInSecondSlot = entity.itemHandler.getStackInSlot(2).getItem() == ManaItems.FALLEN_STAR_ITEM.get();

//        return hasItemInWaterSlot && hasItemInFirstSlot && hasItemInSecondSlot;
//    }

   private static boolean hasNotReachedStackLimit(StarCatcherEntityBlock entity) {
       return entity.itemHandler.getStackInSlot(0).getCount() < entity.itemHandler.getStackInSlot(0).getMaxStackSize();
   }


}
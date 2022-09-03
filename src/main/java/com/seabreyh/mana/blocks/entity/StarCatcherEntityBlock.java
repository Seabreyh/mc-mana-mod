package com.seabreyh.mana.blocks.entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.entity.FallenStar;
import com.seabreyh.mana.particle.ManaParticles;
import com.seabreyh.mana.registry.ManaBlockEntities;
import com.seabreyh.mana.registry.ManaItems;
import com.seabreyh.mana.screen.StarCatcherMenu;

import it.unimi.dsi.fastutil.booleans.Boolean2CharFunction;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.text.html.parser.Entity;

import org.checkerframework.common.returnsreceiver.qual.This;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class StarCatcherEntityBlock extends BlockEntity implements MenuProvider {
    private Vec3 shootDir;
    private LivingEntity owner;
    private FallenStar target;
    public int tickCount;

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public StarCatcherEntityBlock(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ManaBlockEntities.STAR_CATCHER_ENTITY_BLOCK.get(), pWorldPosition, pBlockState);
        ManaMod.LOGGER.debug("entity block");
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Star Catcher");
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
        if(hasRecipe(pBlockEntity) && hasNotReachedStackLimit(pBlockEntity)) {
           craftItem(pBlockEntity);
        }

        locateStars(pLevel, pBlockEntity, pPos, pState);
    }

   private static void locateStars(Level plevel, StarCatcherEntityBlock pBlockEntity, BlockPos pPos, BlockState pState) {

    // double x = (double)pPos.getX();
    // double y = (double)pPos.getY();
    // double z = (double)pPos.getZ();
        if(hasNotReachedStackLimit(pBlockEntity)) {


            AABB area = pBlockEntity.getRenderBoundingBox().inflate(80.0D, 80.0D, 80.0D);
    List<FallenStar> fallenStars = plevel.getEntitiesOfClass(FallenStar.class, area);

    FallenStar foundTarget = null;
    for (FallenStar foundStar : fallenStars) {
        foundTarget = foundStar;
        //make sure star can only be targeted by one star catcher
            if(foundTarget.getIsFalling() == false && foundTarget.getIsTargeted() == false) {
                if (foundTarget != null && foundTarget instanceof FallenStar) {
                    // ManaMod.LOGGER.debug("ball found target!");
                    // ManaMod.LOGGER.debug(pBlockEntity.target.toString());
                    if(foundTarget.getIsTargeted() == false){
                        foundTarget.setIsTargeted(true);

                        double bx = (double)pPos.getX();
                        double by = (double)pPos.getY();
                        double bz = (double)pPos.getZ();

                        double sx = foundTarget.getX();
                        double sy = foundTarget.getY();
                        double sz = foundTarget.getZ();
                        
                        //normalize ??????
                        Vec3 dirToCatcher = foundTarget.position().subtract(new Vec3(bx, by, bz));
                        double distTo = foundTarget.position().subtract(new Vec3(bx, by, bz)).length();
                        ManaMod.LOGGER.debug(dirToCatcher.toString());
                        ManaMod.LOGGER.debug(String.valueOf(distTo));

                        for(double i = distTo; i >= 0; i--) {
                            // foundTarget.setPosRaw(foundTarget.position().x - dirToCatcher.x, foundTarget.position().y - dirToCatcher.y, foundTarget.position().z - dirToCatcher.z);
                            plevel.addParticle(ManaParticles.MAGIC_PLOOM_PARTICLE_FALLING_STAR.get(), 
                            sx - ((dirToCatcher.x - 0.5)/i),
                            sy - ((dirToCatcher.y - 0.5)/i), 
                            sz - ((dirToCatcher.z - 0.5)/i),
                            0,0,0);
                            
                            sx = sx - ((dirToCatcher.x - 0.5)/i);
                            sy = sy - ((dirToCatcher.y- 0.5) /i);
                            sz = sz - ((dirToCatcher.z - 0.5)/i);
                        }

                    }
                    craftItem(pBlockEntity);
                    foundTarget.discardStar();
                }
               
                
            }else{
                
            }

        }
    
            
    }

    // if (pBlockEntity.target != null && foundTarget instanceof FallenStar) {
    //     // ManaMod.LOGGER.debug("ball found target!");
    //     // ManaMod.LOGGER.debug(pBlockEntity.target.toString());
    //     if(!pBlockEntity.target.getIsFalling()){
    //         // pBlockEntity.target.discardStar();
    //         pBlockEntity.target.setPosRaw(pBlockEntity.target.position().x + 0.5, pBlockEntity.target.position().y + 0.5, pBlockEntity.target.position().z + 0.5);
    //     }
    
    // }
}

// private static void setTarget(LivingEntity pTarget) {
//     target = pTarget;
// }

// private static LivingEntity getTarget(){
//     return target;
// }

   private static void craftItem(StarCatcherEntityBlock entity) {
       entity.itemHandler.setStackInSlot(0, new ItemStack(ManaItems.FALLEN_STAR_ITEM.get(),
               entity.itemHandler.getStackInSlot(0).getCount() + 1));
   }

   private static boolean hasRecipe(StarCatcherEntityBlock entity) {
       boolean hasItemInWaterSlot = PotionUtils.getPotion(entity.itemHandler.getStackInSlot(0)) == Potions.WATER;
       boolean hasItemInFirstSlot = entity.itemHandler.getStackInSlot(1).getItem() == ManaItems.FALLEN_STAR_ITEM.get();
       boolean hasItemInSecondSlot = entity.itemHandler.getStackInSlot(2).getItem() == ManaItems.FALLEN_STAR_ITEM.get();

       return hasItemInWaterSlot && hasItemInFirstSlot && hasItemInSecondSlot;
   }

   private static boolean hasNotReachedStackLimit(StarCatcherEntityBlock entity) {
       return entity.itemHandler.getStackInSlot(0).getCount() < entity.itemHandler.getStackInSlot(0).getMaxStackSize();
   }
}
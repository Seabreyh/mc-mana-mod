package com.seabreyh.mana.blocks.entity;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.registry.ManaBlockEntities;
import com.seabreyh.mana.registry.ManaItems;
import com.seabreyh.mana.screen.StarCatcherMenu;

import java.util.Random;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    private LivingEntity target;
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
    }

    // public static void clientTick(Level p_155404_, BlockPos p_155405_, BlockState p_155406_, StarCatcherEntityBlock p_155407_) {
    //     ++p_155407_.tickCount;
    //     long i = p_155404_.getGameTime();
    // }

    // public static void serverTick(Level p_155439_, BlockPos p_155440_, BlockState p_155441_, StarCatcherEntityBlock p_155442_) {
    //     ++p_155442_.tickCount;
    //     long i = p_155439_.getGameTime();
    // }

//  @Override
//  public void tick() {
//      super.tick();
//      if (getSpeed() == 0)
//          return;


//      boolean isNatural = level.dimensionType().natural();
//      int dayTime = (int) ((level.getDayTime() * (isNatural ? 1 : 24)) % 24000);
//      int hours = (dayTime / 1000 + 6) % 24;
//      int minutes = (dayTime % 1000) * 60 / 1000;

//      if (!isNatural) {
//          if (level.isClientSide) {
//              moveHands(hours, minutes);

//              if (AnimationTickHolder.getTicks() % 6 == 0)
//                  playSound(SoundEvents.NOTE_BLOCK_HAT, 1 / 16f, 2f);
//              else if (AnimationTickHolder.getTicks() % 3 == 0)
//                  playSound(SoundEvents.NOTE_BLOCK_HAT, 1 / 16f, 1.5f);
//          }
//          return;
//      }

//      if (!level.isClientSide) {
//          if (animationType == Animation.NONE) {
//              if (hours == 12 && minutes < 5)
//                  startAnimation(Animation.PIG);
//              if (hours == 18 && minutes < 36 && minutes > 31)
//                  startAnimation(Animation.CREEPER);
//          } else {
//              float value = animationProgress.getValue();
//              animationProgress.setValue(value + 1);
//              if (value > 100)
//                  animationType = Animation.NONE;

//              if (animationType == Animation.SURPRISE && Mth.equal(animationProgress.getValue(), 50)) {
//                  Vec3 center = VecHelper.getCenterOf(worldPosition);
//                  level.destroyBlock(worldPosition, false);
//                  level.explode(null, CUCKOO_SURPRISE, null, center.x, center.y, center.z, 3, false,
//                      Explosion.BlockInteraction.BREAK);
//              }

//          }
//      }

//      if (level.isClientSide) {
//          moveHands(hours, minutes);

//          if (animationType == Animation.NONE) {
//              if (AnimationTickHolder.getTicks() % 32 == 0)
//                  playSound(SoundEvents.NOTE_BLOCK_HAT, 1 / 16f, 2f);
//              else if (AnimationTickHolder.getTicks() % 16 == 0)
//                  playSound(SoundEvents.NOTE_BLOCK_HAT, 1 / 16f, 1.5f);
//          } else {

//              boolean isSurprise = animationType == Animation.SURPRISE;
//              float value = animationProgress.getValue();
//              animationProgress.setValue(value + 1);
//              if (value > 100)
//                  animationType = null;

//              // sounds

//              if (value == 1)
//                  playSound(SoundEvents.NOTE_BLOCK_CHIME, 2, .5f);
//              if (value == 21)
//                  playSound(SoundEvents.NOTE_BLOCK_CHIME, 2, 0.793701f);

//              if (value > 30 && isSurprise) {
//                  Vec3 pos = VecHelper.offsetRandomly(VecHelper.getCenterOf(this.worldPosition), level.random, .5f);
//                  level.addParticle(ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 0, 0, 0);
//              }
//              if (value == 40 && isSurprise)
//                  playSound(SoundEvents.TNT_PRIMED, 1f, 1f);

//              int step = isSurprise ? 3 : 15;
//              for (int phase = 30; phase <= 60; phase += step) {
//                  if (value == phase - step / 3)
//                      playSound(SoundEvents.CHEST_OPEN, 1 / 16f, 2f);
//                  if (value == phase) {
//                      if (animationType == Animation.PIG)
//                          playSound(SoundEvents.PIG_AMBIENT, 1 / 4f, 1f);
//                      else
//                          playSound(SoundEvents.CREEPER_HURT, 1 / 4f, 3f);
//                  }
//                  if (value == phase + step / 3)
//                      playSound(SoundEvents.CHEST_CLOSE, 1 / 16f, 2f);

//              }

//          }

//          return;
//      }
//  }

//    private void resolveEnemyTarget() {
//     // Handle entity target "homing"
//     AABB aabb = this.owner.getBoundingBox().inflate(64.0D, 24.0D, 64.0D);
//     List<? extends LivingEntity> candidates = this.level.getNearbyEntities(LivingEntity.class,
//             TargetingConditions.forNonCombat().range(64.0D),
//             this.owner, aabb);

//     LivingEntity foundTarget = null;
//     double shortestDist = Double.MAX_VALUE;
//     for (LivingEntity livingentity : candidates) {

//             foundTarget = livingentity;

//             BlockPos catchPos = this.getBlockPos();
//             Vec3 entityPos = foundTarget.position();
//             this.target = foundTarget;
        
//     }

//     if (this.target != null) {
//         ManaMod.LOGGER.debug("ball found target!");
//     }
// }

   private static void craftItem(StarCatcherEntityBlock entity) {
       entity.itemHandler.extractItem(0, 1, false);
       entity.itemHandler.extractItem(1, 1, false);
       entity.itemHandler.getStackInSlot(2).hurt(1, new Random(), null);

       entity.itemHandler.setStackInSlot(3, new ItemStack(ManaItems.FALLEN_STAR_ITEM.get(),
               entity.itemHandler.getStackInSlot(3).getCount() + 1));
   }

   private static boolean hasRecipe(StarCatcherEntityBlock entity) {
       boolean hasItemInWaterSlot = PotionUtils.getPotion(entity.itemHandler.getStackInSlot(0)) == Potions.WATER;
       boolean hasItemInFirstSlot = entity.itemHandler.getStackInSlot(1).getItem() == ManaItems.FALLEN_STAR_ITEM.get();
       boolean hasItemInSecondSlot = entity.itemHandler.getStackInSlot(2).getItem() == ManaItems.FALLEN_STAR_ITEM.get();

       return hasItemInWaterSlot && hasItemInFirstSlot && hasItemInSecondSlot;
   }

   private static boolean hasNotReachedStackLimit(StarCatcherEntityBlock entity) {
       return entity.itemHandler.getStackInSlot(3).getCount() < entity.itemHandler.getStackInSlot(3).getMaxStackSize();
   }
}
package com.seabreyh.mana.content.items.block_entity_items;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.blocks.block_entities.StarCatcherBlockEntity;
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
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraftforge.items.IItemHandler;

public class FallenStarBlockEntityItem extends BlockEntity implements MenuProvider {
    public int tickCount;
    public float activeRotation;
    public float rotationSpeed = 1F;

    public FallenStarBlockEntityItem(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ManaBlockEntities.FALLEN_STAR_BLOCK_ENTITY_ITEM.get(), pWorldPosition, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FallenStarBlockEntityItem pBlockEntity) {

        if (pLevel.isClientSide) {
            // ManaMod.LOGGER
            // .info("ID" + pBlockEntity.worldPosition.getZ() + " speed: " +
            // pBlockEntity.rotationSpeed
            // + " catchCount: "
            // + pBlockEntity.catchCount);

            // if (pBlockEntity.catchCount > 0) {
            // float modifier = Math.min((float) pBlockEntity.catchCount * 10.0f, 20.0f);
            // pBlockEntity.rotationSpeed = (3.0f + modifier);
            // } else {

            // pBlockEntity.rotationSpeed = (1.0f);
            // }
            // }

            // // animationTick(pLevel, pPos, list, pBlockEntity.tickCount);
            // ++pBlockEntity.activeRotation;}
        }
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        // FIXME Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createMenu'");

    }

    @Override
    public Component getDisplayName() {
        // FIXME Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDisplayName'");
    }

}
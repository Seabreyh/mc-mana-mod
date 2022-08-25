package com.seabreyh.mana.entity;

import com.seabreyh.mana.registry.ManaItems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FallenStar extends AbstractArrow {
    private int age;
    public float bobOffs;

    public FallenStar(EntityType<? extends FallenStar> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    protected FallenStar(EntityType<? extends FallenStar> p_36711_, double p_36712_, double p_36713_,
            double p_36714_, Level p_36715_) {
        super(p_36711_, p_36712_, p_36713_, p_36714_, p_36715_);
    }

    public void tick() {
        super.tick();

        if (this.age != -32768) {
            ++this.age;
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ManaItems.FALLEN_STAR_ITEM.get());
    }

    public ItemStack getItem() {
        return new ItemStack(ManaItems.FALLEN_STAR_ITEM.get());
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
            if (this.tryPickup(player)) {
                player.take(this, 1);
                this.discard();
            }

        }
    }

    protected boolean tryPickup(Player player) {
        return player.getInventory().add(this.getPickupItem());
    }

    public int getAge() {
        return this.age;
    }

    public float getSpin(float p_32009_) {
        return ((float) this.getAge() + p_32009_) / 20.0F + this.bobOffs;
    }

}

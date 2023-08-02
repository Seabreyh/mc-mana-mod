package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.items.EmeraldStaff;
import com.seabreyh.mana.items.EmptyManaCapsule;
import com.seabreyh.mana.items.FallenStarItem;
import com.seabreyh.mana.items.FilledManaCapsule;
import com.seabreyh.mana.items.ManaCrystal;
import com.seabreyh.mana.items.ManaDust;
import com.seabreyh.mana.items.ManaShard;
import com.seabreyh.mana.items.StarDust;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ManaMod.MOD_ID);

    // -----------------------
    // REGISTER ITEMS
    // -----------------------

    public static final RegistryObject<Item> STAR_DUST = ITEMS.register("star_dust",
            () -> new StarDust(new Item.Properties()));

    public static final RegistryObject<Item> EMERALD_STAFF = ITEMS.register("emerald_staff",
            () -> new EmeraldStaff(new Item.Properties()));

    public static final RegistryObject<Item> EMPTY_MANA_CAPSULE = ITEMS.register("empty_mana_capsule",
            () -> new EmptyManaCapsule(new Item.Properties()));

    public static final RegistryObject<Item> FALLEN_STAR_ITEM = ITEMS.register("fallen_star_item",
            () -> new FallenStarItem(new Item.Properties()));

    public static final RegistryObject<Item> FILLED_MANA_CAPSULE = ITEMS.register("filled_mana_capsule",
            () -> new FilledManaCapsule(new Item.Properties()));

    public static final RegistryObject<Item> MANA_CRYSTAL = ITEMS.register("mana_crystal",
            () -> new ManaCrystal(new Item.Properties()));

    public static final RegistryObject<Item> MANA_DUST = ITEMS.register("mana_dust",
            () -> new ManaDust(new Item.Properties()));

    public static final RegistryObject<Item> MANA_SHARD = ITEMS.register("mana_shard",
            () -> new ManaShard(new Item.Properties()));

    // -----------------------
    // END REGISTER ITEMS
    // -----------------------

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

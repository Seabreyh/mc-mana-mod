package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.items.AmethystStaff;
import com.seabreyh.mana.items.EmeraldStaff;
import com.seabreyh.mana.items.FallenStarItem;
import com.seabreyh.mana.items.FilledManaCapsule;
import com.seabreyh.mana.items.GrantedWishItem;
import com.seabreyh.mana.items.EmptyManaCapsule;
import com.seabreyh.mana.items.ManaCrystal;
import com.seabreyh.mana.items.ManaDust;
import com.seabreyh.mana.items.ManaShard;
import com.seabreyh.mana.items.ManaTreat;
import com.seabreyh.mana.items.SealedWishItem;
import com.seabreyh.mana.items.StarDust;
import com.seabreyh.mana.items.WishItem;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaItems {

        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
                        ManaMod.MOD_ID);

        public static final RegistryObject<Item> AMETHYST_STAFF = ITEMS.register("amethyst_staff",
                        () -> new AmethystStaff(new Item.Properties().durability(500)
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)));

        public static final RegistryObject<Item> EMERALD_STAFF = ITEMS.register("emerald_staff",
                        () -> new EmeraldStaff(new Item.Properties().durability(500)
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)));

        public static final RegistryObject<Item> FALLEN_STAR_ITEM = ITEMS.register("fallen_star_item",
                        () -> new FallenStarItem(new Item.Properties()
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> WISH_ITEM = ITEMS.register("wish_item",
                        () -> new WishItem(new Item.Properties()
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)
                                        .stacksTo(1)));

        public static final RegistryObject<Item> SEALED_WISH_ITEM = ITEMS.register("sealed_wish_item",
                        () -> new SealedWishItem(new Item.Properties()
                                        .stacksTo(1)));

        public static final RegistryObject<Item> GRANTED_WISH_ITEM = ITEMS.register("granted_wish_item",
                        () -> new GrantedWishItem(new Item.Properties()
                                        .stacksTo(1)));

        public static final RegistryObject<Item> MANA_CRYSTAL = ITEMS.register("mana_crystal",
                        () -> new ManaCrystal(new Item.Properties()
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> STAR_DUST = ITEMS.register("star_dust",
                        () -> new StarDust(new Item.Properties()
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> MANA_DUST = ITEMS.register("mana_dust",
                        () -> new ManaDust(new Item.Properties()
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> MANA_SHARD = ITEMS.register("mana_shard",
                        () -> new ManaShard(new Item.Properties()
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> EMPTY_MANA_CAPSULE = ITEMS.register("empty_mana_capsule",
                        () -> new EmptyManaCapsule(new Item.Properties()
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)
                                        .stacksTo(1)));

        public static final RegistryObject<Item> FILLED_MANA_CAPSULE = ITEMS.register("filled_mana_capsule",
                        () -> new FilledManaCapsule(new Item.Properties()
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)
                                        .stacksTo(1)));

        public static final RegistryObject<Item> MANA_TREAT = ITEMS.register("mana_treat",
                        () -> new ManaTreat(new Item.Properties()
                                        .tab(ManaCreativeTabs.MANA_TAB_ITEMS)
                                        .stacksTo(64).food(ManaTreat.MANA_TREAT)));

}

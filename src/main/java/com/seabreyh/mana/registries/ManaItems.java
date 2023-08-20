package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.items.AmethystStaff;
import com.seabreyh.mana.content.items.EmeraldStaff;
import com.seabreyh.mana.content.items.EmptyManaCapsule;
import com.seabreyh.mana.content.items.FallenStarItem;
import com.seabreyh.mana.content.items.FilledManaCapsule;
import com.seabreyh.mana.content.items.GrantedWishItem;
import com.seabreyh.mana.content.items.ManaCrystal;
import com.seabreyh.mana.content.items.ManaDust;
import com.seabreyh.mana.content.items.ManaShard;
import com.seabreyh.mana.content.items.ManaTreat;
import com.seabreyh.mana.content.items.SealedWishItem;
import com.seabreyh.mana.content.items.StarDust;
import com.seabreyh.mana.content.items.WishItem;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
                        ManaMod.MOD_ID);

        // -----------------------
        // REGISTER ITEMS
        // -----------------------

        public static final RegistryObject<Item> AMETHYST_STAFF = ITEMS.register("amethyst_staff",
                        () -> new AmethystStaff(AmethystStaff.PROPERTIES));

        public static final RegistryObject<Item> EMERALD_STAFF = ITEMS.register("emerald_staff",
                        () -> new EmeraldStaff(EmeraldStaff.PROPERTIES));

        public static final RegistryObject<Item> STAR_DUST = ITEMS.register("star_dust",
                        () -> new StarDust(new Item.Properties()));

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

        public static final RegistryObject<Item> MANA_TREAT = ITEMS.register("mana_treat",
                        () -> new ManaTreat(new Item.Properties().food(ManaTreat.FOOD_PROPERTIES)));

        public static final RegistryObject<Item> WISH_ITEM = ITEMS.register("wish_item",
                        () -> new WishItem(WishItem.PROPERTIES));

        public static final RegistryObject<Item> SEALED_WISH_ITEM = ITEMS.register("sealed_wish_item",
                        () -> new SealedWishItem(SealedWishItem.PROPERTIES));

        public static final RegistryObject<Item> GRANTED_WISH_ITEM = ITEMS.register("granted_wish_item",
                        () -> new GrantedWishItem(GrantedWishItem.PROPERTIES));
        // -----------------------z
        // END REGISTER ITEMS
        // -----------------------

        public static void register(IEventBus eventBus) {
                ITEMS.register(eventBus);
        }
}

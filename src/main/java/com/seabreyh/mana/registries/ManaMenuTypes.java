package com.seabreyh.mana.registries;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.foundation.client.gui.screens.StaffTableMenu;
import com.seabreyh.mana.foundation.client.gui.screens.StarCatcherMenu;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaMenuTypes {
        public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
                        ManaMod.MOD_ID);

        // REGISTER MENU TYPES --------------

        public static final RegistryObject<MenuType<StarCatcherMenu>> STAR_CATCHER_MENU = registerMenuType(
                        StarCatcherMenu::new, "star_catcher_menu");

        public static final RegistryObject<MenuType<StaffTableMenu>> STAFF_TABLE_MENU = registerMenuType(
                        StaffTableMenu::new, "staff_table_menu");

        // END REGISTER MENU TYPES --------------

        private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(
                        IContainerFactory<T> factory,
                        String name) {
                return MENUS.register(name, () -> IForgeMenuType.create(factory));
        }

        public static void register(IEventBus eventBus) {
                MENUS.register(eventBus);
        }
}
package com.seabreyh.mana.registry;

import com.seabreyh.mana.ManaMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ManaSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ManaMod.MOD_ID);

    public static RegistryObject<SoundEvent> SHOOTING_STAR = registerSoundEvent("shooting_star");
    public static RegistryObject<SoundEvent> STAR_BOOM = registerSoundEvent("star_boom");
    public static RegistryObject<SoundEvent> STAR_CAPTURE = registerSoundEvent("star_capture");
    
    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(ManaMod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}

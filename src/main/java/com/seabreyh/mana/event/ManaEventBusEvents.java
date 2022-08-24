package com.seabreyh.mana.event;

import com.seabreyh.mana.particle.MagicPloomParticle;
import com.seabreyh.mana.particle.ManaParticles;

// import com.seabreyh.mana.recipe.GemCuttingStationRecipe;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.seabreyh.mana.ManaMod;

@Mod.EventBusSubscriber(modid = ManaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ManaEventBusEvents {

    // @SubscribeEvent
    // public static void registerRecipeTypes(final
    // RegistryEvent.Register<RecipeSerializer<?>> event) {
    // Registry.register(Registry.RECIPE_TYPE, GemCuttingStationRecipe.Type.ID,
    // GemCuttingStationRecipe.Type.INSTANCE);
    // }

    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(ManaParticles.MAGIC_PLOOM_PARTICLE.get(),
                MagicPloomParticle.Provider::new);
    }
}

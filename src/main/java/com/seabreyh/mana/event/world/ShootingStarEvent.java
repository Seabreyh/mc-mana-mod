package com.seabreyh.mana.event.world;

import java.util.Random;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.entity.FallenStar;
import com.seabreyh.mana.registry.ManaEntities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class ShootingStarEvent {
    private static final Random random = new Random();
    private static int worldTime = 0;
    private static final float SECONDS_BETWEEN_EVENT = 50F;

    public static void processPlayerEvent(final LivingUpdateEvent event) {
        if (event.getEntity() instanceof Player) {
            Player thisPlayer = (Player) event.getEntity();

            Level world = thisPlayer.getCommandSenderWorld();

            int timer = (int) (random.nextFloat() * 20.0F * SECONDS_BETWEEN_EVENT);
            timer = timer > 0 ? timer : 1;
            float currentTime = world.getTimeOfDay(1.0F);
            if (worldTime % timer == 0 && currentTime > 0.3 && currentTime < 0.7 && !world.isRaining()) {
                if (!world.isClientSide) {
                    FallenStar shootingStar = new FallenStar(ManaEntities.FALLEN_STAR.get(), world, thisPlayer);

                    double shootXOffset = 60D * random.nextDouble() - 30D;
                    double shootYOffset = 75D;
                    double shootZOffset = 60D * random.nextDouble() - 30D;

                    Vec3 shootPos = thisPlayer.position().add(new Vec3(shootXOffset, shootYOffset, shootZOffset));
                    shootingStar.setPos(shootPos);

                    float randShootDirAngle = random.nextFloat() * 360.0F;
                    float randShootSteepAngle = random.nextFloat() * -45.0F;
                    shootingStar.shootFromRotation(randShootSteepAngle, randShootDirAngle, 1F, 1.5F, 1F);

                    world.addFreshEntity(shootingStar);
                }
            }
            worldTime++;
        }
    }
}

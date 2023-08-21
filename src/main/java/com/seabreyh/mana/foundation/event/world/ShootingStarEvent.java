package com.seabreyh.mana.foundation.event.world;

import com.seabreyh.mana.ManaMod;
import com.seabreyh.mana.content.entities.FallenStarEntity;
import com.seabreyh.mana.registries.ManaEntities;

import java.util.Random;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;

public class ShootingStarEvent {
    private static final Random random = new Random();
    private static int worldTime = 0;
    private static final float SECONDS_BETWEEN_EVENT = 50F;
    private static final float TIME_SPAWN_START = 0.3F;
    private static final float TIME_SPAWN_STOP = 0.7F;
    private static final float TIME_DESPAWN = 0.75F;
    private static boolean allowStarEvent = true;

    public static void processPlayerEvent(final LivingTickEvent event) {
        if (event.getEntity() instanceof Player && allowStarEvent) {
            Player thisPlayer = (Player) event.getEntity();

            Level world = thisPlayer.getCommandSenderWorld();

            int timer = (int) (random.nextFloat() * 20.0F * SECONDS_BETWEEN_EVENT);
            timer = timer > 0 ? timer : 1;
            float currentTime = world.getTimeOfDay(1.0F);
            if (worldTime % timer == 0 && currentTime > TIME_SPAWN_START && currentTime < TIME_SPAWN_STOP &&
                    !world.isRaining()
                    && world.dimension() == Level.OVERWORLD) {
                if (!world.isClientSide) {
                    FallenStarEntity shootingStar = new FallenStarEntity(ManaEntities.FALLEN_STAR.get(),
                            world, thisPlayer);

                    double shootXOffset = (60D * random.nextDouble() - 30D) * random.nextDouble();
                    double shootYOffset = 330D; // world height and then some
                    double shootZOffset = (60D * random.nextDouble() - 30D) * random.nextDouble();

                    Vec3 playerPos = thisPlayer.position().add(new Vec3(shootXOffset,
                            0, shootZOffset));
                    Vec3 shootPos = new Vec3(playerPos.x, shootYOffset, playerPos.z);
                    shootingStar.setPos(shootPos);

                    float randShootDirAngle = random.nextFloat() * 360.0F;
                    // float randShootSteepAngle = random.nextFloat() * -45.0F;
                    float randShootSteepAngle = random.nextFloat() * -75.0F;

                    float rangeXZ = 0.5F + random.nextFloat() * (1.3F - 0.5F);
                    float rangeY = 1.0F + random.nextFloat() * (2F - 1.0F);

                    shootingStar.shootFromRotation(randShootSteepAngle, randShootDirAngle, rangeXZ,
                            rangeY, rangeXZ);

                    world.addFreshEntity(shootingStar);
                }
            }
            worldTime++;
        }
    }

    public static float getStarDespawnTime() {
        return TIME_DESPAWN;
    }

    public static float getStarSpawnStartTime() {
        return TIME_SPAWN_START;
    }
}

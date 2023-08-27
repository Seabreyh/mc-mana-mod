/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.seabreyh.mana.content.entities;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 * This event is fired when a projectile entity impacts something.<br>
 * This event is fired via
 * {@link EventFactory#onStarImpact(AbstractFallingSpaceEntity, HitResult)}
 * This event is fired for all vanilla projectiles by Forge,
 * custom projectiles should fire this event and check the result in a similar
 * fashion.
 * This event is cancelable. When canceled, the impact will not be processed and
 * the projectile will continue flying.
 * Killing or other handling of the entity after event cancellation is up to the
 * modder.
 */
@Cancelable
public class StarImpactEvent extends EntityEvent {
    private final HitResult ray;
    private final AbstractFallingSpaceEntity projectile;

    public StarImpactEvent(AbstractFallingSpaceEntity projectile, HitResult ray) {
        super(projectile);
        this.ray = ray;
        this.projectile = projectile;
    }

    public HitResult getRayTraceResult() {
        return ray;
    }

    public AbstractFallingSpaceEntity getProjectile() {
        return projectile;
    }
}

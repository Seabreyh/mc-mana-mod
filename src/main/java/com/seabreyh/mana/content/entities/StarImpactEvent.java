package com.seabreyh.mana.content.entities;

import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

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

package com.seabreyh.mana.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;

public abstract class BaseMagicPloomParticle extends SimpleAnimatedParticle {

    public float fadeR;
    public float fadeG;
    public float fadeB;

    protected BaseMagicPloomParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
            SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, spriteSet, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut() {
        this.rCol += (this.fadeR - this.rCol) * 0.1F;
        this.gCol += (this.fadeG - this.gCol) * 0.1F;
        this.bCol += (this.fadeB - this.bCol) * 0.1F;
    }

    public abstract void setCol();

    public abstract void setFadeCol();

}
package com.seabreyh.mana.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public abstract class MagicPloomParticle extends SimpleAnimatedParticle {

    public float fadeR;
    public float fadeG;
    public float fadeB;

    protected MagicPloomParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet,
            double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, spriteSet, 1.0F);

        this.gravity = 0.0F;
        this.friction = 0.0F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1F;
        this.lifetime = 10;
        this.setSpriteFromAge(spriteSet);

        setFade();
        setCol();

        this.alpha = 1.0f;
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

    public abstract void setFade();
}
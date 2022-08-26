package com.seabreyh.mana.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class MagicPloomParticle extends SimpleAnimatedParticle {

    public float fadeR;
    public float fadeG;
    public float fadeB;
    
    protected MagicPloomParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
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

    public void setCol(){
        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;
    }

    public void setFade(){
        this.fadeR = 204f / 255.0f;
        this.fadeG = 102f / 255.0f;
        this.fadeB = 255f / 255.0f;
    }
}
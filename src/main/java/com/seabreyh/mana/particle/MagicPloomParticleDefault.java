package com.seabreyh.mana.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagicPloomParticleDefault extends MagicPloomParticle {
    protected MagicPloomParticleDefault(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, spriteSet, xd, yd, zd);

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
    public void setCol(){
        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;
    }

    @Override
    public void setFade(){
        this.fadeR = 204f / 255.0f;
        this.fadeG = 102f / 255.0f;
        this.fadeB = 255f / 255.0f;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                double x, double y, double z,
                double dx, double dy, double dz) {
            return new MagicPloomParticleDefault(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
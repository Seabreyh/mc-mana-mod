package com.seabreyh.mana.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagicPloomParticle extends SimpleAnimatedParticle {
    private float fadeR;
    private float fadeG;
    private float fadeB;

    protected MagicPloomParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
            SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, spriteSet, 1.0F);

        this.gravity = 0.0F;
        this.friction = 0.0F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1F;
        this.lifetime = 10;
        this.setSpriteFromAge(spriteSet);

        this.fadeR = 204f / 255.0f;
        this.fadeG = 102f / 255.0f;
        this.fadeB = 255f / 255.0f;

        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;

        this.alpha = 1.0f;
    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut() {
        // if (this.age > this.lifetime / 2.0) {
        // this.setAlpha(1.0F - ((float) this.age - (float) (this.lifetime / 2)) /
        // (float) this.lifetime);
        this.rCol += (this.fadeR - this.rCol) * 0.1F;
        this.gCol += (this.fadeG - this.gCol) * 0.1F;
        this.bCol += (this.fadeB - this.bCol) * 0.1F;
        // }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            return new MagicPloomParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
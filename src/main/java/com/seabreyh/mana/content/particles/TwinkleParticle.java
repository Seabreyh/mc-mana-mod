package com.seabreyh.mana.content.particles;

import java.util.Random;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TwinkleParticle extends TextureSheetParticle {
   static final Random RANDOM = new Random();
   private final SpriteSet sprites;

   TwinkleParticle(ClientLevel p_172136_, double p_172137_, double p_172138_, double p_172139_, double p_172140_,
         double p_172141_, double p_172142_, SpriteSet p_172143_) {
      super(p_172136_, p_172137_, p_172138_, p_172139_, p_172140_, p_172141_, p_172142_);
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = p_172143_;
      this.quadSize *= 0.75F;
      this.hasPhysics = false;
      this.setSpriteFromAge(p_172143_);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public int getLightColor(float p_172146_) {
      float f = ((float) this.age + p_172146_) / (float) this.lifetime;
      f = Mth.clamp(f, 0.0F, 1.0F);
      int i = super.getLightColor(p_172146_);
      int j = i & 255;
      int k = i >> 16 & 255;
      j += (int) (f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_172172_) {
         this.sprite = p_172172_;
      }

      public Particle createParticle(SimpleParticleType p_172183_, ClientLevel p_172184_, double p_172185_,
            double p_172186_, double p_172187_, double p_172188_, double p_172189_, double p_172190_) {
         TwinkleParticle glowparticle = new TwinkleParticle(p_172184_, p_172185_, p_172186_, p_172187_,
               0.5D - TwinkleParticle.RANDOM.nextDouble(), p_172189_, 0.5D - TwinkleParticle.RANDOM.nextDouble(),
               this.sprite);
         if (p_172184_.random.nextBoolean()) {
            glowparticle.setColor(1.0F, 1.0F, 1.0F);
         } else {
            glowparticle.setColor(1.0F, 1.0F, 1.0F);
         }

         glowparticle.yd *= (double) 0.2F;
         if (p_172188_ == 0.0D && p_172190_ == 0.0D) {
            glowparticle.xd *= (double) 0.1F;
            glowparticle.zd *= (double) 0.1F;
         }

         glowparticle.setLifetime((int) (8.0D / (p_172184_.random.nextDouble() * 0.8D + 0.2D)));
         return glowparticle;
      }
   }

}
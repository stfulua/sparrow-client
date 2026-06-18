package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(StatusEffect.class)
public class OldPotionColorsMixin {

    @Unique
    private static final Map<String, Integer> sparrow_OLD = new HashMap<>();

    static {
        sparrow_OLD.put("absorption", 0x2552A5);
        sparrow_OLD.put("bad_omen", 0x0B6138);
        sparrow_OLD.put("blindness", 0x1F1F23);
        sparrow_OLD.put("conduit_power", 0x1DC2D1);
        sparrow_OLD.put("dolphins_grace", 0x88A3BE);
        sparrow_OLD.put("fire_resistance", 0xE49A3A);
        sparrow_OLD.put("glowing", 0x94A061);
        sparrow_OLD.put("haste", 0xD9C043);
        sparrow_OLD.put("health_boost", 0xF87D23);
        sparrow_OLD.put("hero_of_the_village", 0x44FF44);
        sparrow_OLD.put("hunger", 0x587653);
        sparrow_OLD.put("instant_damage", 0x430A09);
        sparrow_OLD.put("instant_health", 0xF82423);
        sparrow_OLD.put("invisibility", 0x7F8392);
        sparrow_OLD.put("jump_boost", 0x22FF4C);
        sparrow_OLD.put("levitation", 0xCEFFFF);
        sparrow_OLD.put("luck", 0x339900);
        sparrow_OLD.put("mining_fatigue", 0x4A4217);
        sparrow_OLD.put("nausea", 0x551D4A);
        sparrow_OLD.put("night_vision", 0x1F1FA1);
        sparrow_OLD.put("poison", 0x87A363);
        sparrow_OLD.put("regeneration", 0xCD5CAB);
        sparrow_OLD.put("resistance", 0x99453A);
        sparrow_OLD.put("saturation", 0xF82423);
        sparrow_OLD.put("slow_falling", 0xFFEFD1);
        sparrow_OLD.put("slowness", 0x5A6C81);
        sparrow_OLD.put("speed", 0x7CAFC6);
        sparrow_OLD.put("strength", 0x932423);
        sparrow_OLD.put("unluck", 0xC0A44D);
        sparrow_OLD.put("water_breathing", 0x2E5299);
        sparrow_OLD.put("weakness", 0x484D48);
        sparrow_OLD.put("wither", 0x352A27);
    }

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void onGetColor(CallbackInfoReturnable<Integer> cir) {
        if (!ConfigCache.oldPotions) return;
        StatusEffect self = (StatusEffect) (Object) this;
        Identifier id = Registries.STATUS_EFFECT.getId(self);
        if (id != null) {
            Integer oldColor = sparrow_OLD.get(id.getPath());
            if (oldColor != null) {
                cir.setReturnValue(oldColor);
            }
        }
    }
}

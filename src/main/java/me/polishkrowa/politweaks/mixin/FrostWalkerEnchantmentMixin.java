package me.polishkrowa.politweaks.mixin;

import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerEnchantmentMixin {

    @Redirect(method = "freezeWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isOnGround()Z"))
    private static boolean injected(LivingEntity instance) {
        if (instance instanceof PlayerEntity) {
            return instance.isOnGround() || instance.hasVehicle();
        }
        return instance.isOnGround();
    }

}

package me.polishkrowa.politweaks.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ZombieVillagerEntity.class)
public class ZombieVillagerEntityMixin {

    @ModifyVariable(method = "setConverting", at = @At(value = "HEAD"), argsOnly = true)
    private int injected(int i) {
        return 1;
    }


}

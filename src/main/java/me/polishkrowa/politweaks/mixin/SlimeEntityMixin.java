package me.polishkrowa.politweaks.mixin;

import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SlimeEntity.class)
public class SlimeEntityMixin {

    @Redirect(method = "canSpawn", at=@At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private static int injected(Random instance, int i) {
        return 0;
    }


}

package me.polishkrowa.politweaks.mixin;

import net.minecraft.block.FrogspawnBlock;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FrogspawnBlock.class)
public class FrogspawnBlockMixin {


    @Redirect(method = "spawnTadpoles", at = @At(value = "INVOKE",target = "Lnet/minecraft/util/math/random/Random;nextBetweenExclusive(II)I",ordinal = 0))
    private int setFrogspawnBlockMaxHeight(Random instance, int min, int max) {
        return max;
    }

    @Inject(method = "getHatchTime",at = @At(value = "HEAD"),cancellable = true)
    private static void setFrogspawnBlockMaxHeight(Random random, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(300);
    }

}

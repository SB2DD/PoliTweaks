package me.polishkrowa.politweaks.mixin;

import net.minecraft.block.TurtleEggBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TurtleEggBlock.class)
public class TurtleEggBlockMixin {

    @Inject(method = "shouldHatchProgress", at = @At("HEAD"), cancellable = true)
    private void shouldHatchProgress(World world, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((double) world.getSkyAngle(1.0F) < 0.69D && (double) world.getSkyAngle(1.0F) > 0.65D || world.random.nextInt(1) == 0);
    }
}

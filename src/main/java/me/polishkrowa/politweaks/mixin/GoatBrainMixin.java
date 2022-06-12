package me.polishkrowa.politweaks.mixin;

import net.minecraft.entity.passive.GoatBrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GoatBrain.class)
public class GoatBrainMixin {


    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/intprovider/UniformIntProvider;create(II)Lnet/minecraft/util/math/intprovider/UniformIntProvider;", ordinal = 2))
    private static void injected(Args args) {
        args.set(0,60);
        args.set(1,60);
    }

    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/intprovider/UniformIntProvider;create(II)Lnet/minecraft/util/math/intprovider/UniformIntProvider;", ordinal = 3))
    private static void injectedd(Args args) {
        args.set(0,30);
        args.set(1,30);
    }
}

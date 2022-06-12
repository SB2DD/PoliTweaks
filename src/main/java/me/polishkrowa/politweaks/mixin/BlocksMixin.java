package me.polishkrowa.politweaks.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Blocks.class)
public class BlocksMixin {

    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;strength(FF)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 183))
    private static void injected(Args args) {
        args.set(0, 1.5F);
    }

    @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;strength(FF)Lnet/minecraft/block/AbstractBlock$Settings;", ordinal = 38))
    private static void injectedd(Args args) {
        args.set(0, 0.4F);
    }

}

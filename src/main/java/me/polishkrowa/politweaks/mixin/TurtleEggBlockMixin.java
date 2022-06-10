package me.polishkrowa.politweaks.mixin;

import net.minecraft.block.TurtleEggBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TurtleEggBlock.class)
public class TurtleEggBlockMixin {

    @ModifyConstant(method = "shouldHatchProgress", constant = @Constant(intValue = 500))
    private int shouldHatchProgress(int constant) {
        return 1;
    }
}

package me.polishkrowa.politweaks.mixin;

import net.minecraft.entity.passive.TadpoleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(TadpoleEntity.class)
public class TadpoleEntityMixin {

//    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(I)I"))
//    private static int injected(int a) {
//        return -240;
//    }

}
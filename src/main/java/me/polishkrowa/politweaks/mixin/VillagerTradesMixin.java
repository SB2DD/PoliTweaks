package me.polishkrowa.politweaks.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(targets = "net.minecraft.village.TradeOffers$EnchantBookFactory")
public class VillagerTradesMixin {



//    @ModifyArgs(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentLevelEntry;<init>(Lnet/minecraft/enchantment/Enchantment;I)V"))
//    private void injected(Args args) {
//        Enchantment enchantment = args.get(0);
//        args.set(1, enchantment.getMaxLevel());

    @Redirect(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;nextInt(Lnet/minecraft/util/math/random/Random;II)I"))
    private int injected(Random random, int min, int max) {
        return max;
    }
//    }

    // TERRIBLE way to do this, but it works :/
//    Queue<Enchantment> queue = new LinkedList<>();
//
//    @ModifyVariable(argsOnly = false, method = "create", at = @At(value = "STORE"), index = 5)
//    private int injected(int value) {
//        return queue.poll().getMaxLevel();
//    }

//    @Inject(locals = LocalCapture.CAPTURE_FAILSOFT, method = "create", at = @At(shift = At.Shift.BEFORE, value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;nextInt(Lnet/minecraft/util/math/random/Random;II)I"))
//    private void injected(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir, List list, Enchantment enchantment) {
//        queue.add(enchantment);
//    }
}

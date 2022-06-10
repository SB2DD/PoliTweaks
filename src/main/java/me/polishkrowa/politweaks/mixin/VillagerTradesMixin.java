package me.polishkrowa.politweaks.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets = "net.minecraft.village.TradeOffers$EnchantBookFactory")
public class VillagerTradesMixin {

    @ModifyArgs(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentLevelEntry;<init>(Lnet/minecraft/enchantment/Enchantment;I)V"))
    private void injected(Args args) {
        Enchantment enchantment = args.get(0);
        args.set(1, enchantment.getMaxLevel());
    }


}

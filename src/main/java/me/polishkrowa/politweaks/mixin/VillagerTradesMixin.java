package me.polishkrowa.politweaks.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(targets = "net.minecraft.village.TradeOffers$EnchantBookFactory")
public class VillagerTradesMixin {



//    @ModifyArgs(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentLevelEntry;<init>(Lnet/minecraft/enchantment/Enchantment;I)V"))
//    private void injected(Args args) {
//        Enchantment enchantment = args.get(0);
//        args.set(1, enchantment.getMaxLevel());

    @Final
    @Shadow
    private int experience;


//    @Shadow @Final private int experience;

    List usedEnchants = new ArrayList<>();

    /**
     * @author me
     * @reason Making every enchant loop instead of random
     */
    @Overwrite
    public TradeOffer create(Entity entity, Random random) {
        if (usedEnchants.isEmpty())
            usedEnchants = Registry.ENCHANTMENT.stream().filter(Enchantment::isAvailableForEnchantedBookOffer).collect(Collectors.toList());
        Enchantment enchantment = (Enchantment)usedEnchants.get(random.nextInt(usedEnchants.size()));
        int i = enchantment.getMaxLevel();
        ItemStack itemStack = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i));
        int j = 2 + random.nextInt(5 + i * 10) + 3 * i;
        if (enchantment.isTreasure()) {
            j *= 2;
        }

        if (j > 64) {
            j = 64;
        }


        usedEnchants.remove(enchantment);
        return new TradeOffer(new ItemStack(Items.EMERALD, j), new ItemStack(Items.BOOK), itemStack, 12, this.experience, 0.2F);
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

package me.polishkrowa.politweaks.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    List<RegistryEntry<Enchantment>> usedEnchants = new ArrayList<>();

    /**
     * @author me
     * @reason Making every enchant loop instead of random
     */
    @Overwrite
    public TradeOffer create(Entity entity, Random random) {
        if (usedEnchants.isEmpty())
            usedEnchants = entity.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).streamEntries().collect(Collectors.toList());
        RegistryEntry<Enchantment> enchantEntry = usedEnchants.get(random.nextInt(usedEnchants.size()));
        Enchantment enchantment = enchantEntry.value();
        int i = enchantment.getMaxLevel();
        ItemStack itemStack = EnchantmentHelper.getEnchantedBookWith(new EnchantmentLevelEntry(enchantEntry, i));
        int j = 2 + random.nextInt(5 + i * 10) + 3 * i;
        if (enchantEntry.isIn(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
            j *= 2;
        }

        if (j > 64) {
            j = 64;
        }

        usedEnchants.remove(enchantEntry);
        return new TradeOffer(new TradedItem(Items.EMERALD, j), Optional.of(new TradedItem(Items.BOOK)), itemStack, 12, this.experience, 0.2F);
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

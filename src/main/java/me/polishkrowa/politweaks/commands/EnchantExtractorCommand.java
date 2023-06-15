package me.polishkrowa.politweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;

public class EnchantExtractorCommand {

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(CommandManager.literal("extract-enchant").executes((context) -> {
            return execute(context.getSource());
        }));
        dispatcher.register(CommandManager.literal("ee").executes(literalCommandNode.getCommand()));
        dispatcher.register(CommandManager.literal("extract-e").executes(literalCommandNode.getCommand()));
        dispatcher.register(CommandManager.literal("e-enchant").executes(literalCommandNode.getCommand()));
        dispatcher.register(CommandManager.literal("e-e").executes(literalCommandNode.getCommand()));
    }

    private static int execute(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player.getInventory().getMainHandStack().isEmpty()) {
            source.sendError(Text.literal("Your main item has to be in your mainhand !"));
            return 0;
        }

        if (player.getInventory().isEmpty() || player.getInventory().offHand.isEmpty()) {
            source.sendError(Text.literal("You need a book in your offhand !"));
            return 0;
        }

        ItemStack mainItem = player.getInventory().getMainHandStack();

        boolean isOneBook = (player.getInventory().offHand.get(0).getCount() == 1);
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
//        EnchantedBookItem enchantedBook = (EnchantedBookItem) new ItemStack(new EnchantedBookItem(new Item.Settings()).maxCount(1).rarity(Rarity.UNCOMMON)));

        if (mainItem.getEnchantments().isEmpty()) {
            source.sendError(Text.literal("The item in your mainhand needs enchantments !"));
            return 0;
        }
        List<NbtElement> e = new ArrayList<>();
        for (NbtElement id : mainItem.getEnchantments()) {
            NbtCompound com = (NbtCompound) id;
//            System.out.println(id);
//            System.out.println(com);
//            System.out.println(com.getString("id"));
//            System.out.println(Identifier.tryParse(com.getString("id")).toString());
//            System.out.println(Registry.ENCHANTMENT.get(Identifier.tryParse(com.getString("id"))).getMaxLevel());
//            System.out.println(com.getInt("lvl"));


            EnchantedBookItem.addEnchantment(book, new EnchantmentLevelEntry(Registries.ENCHANTMENT.get(Identifier.tryParse(com.getString("id"))), com.getInt("lvl")));

            e.add(id);
        }
        for (NbtElement nbtElement : e) {
            mainItem.getEnchantments().remove(nbtElement);
        }

        if (isOneBook) {
            player.getInventory().offHand.set(0,book);
            player.getInventory().updateItems();
        } else {
            ItemStack offHand = player.getInventory().offHand.get(0);
            offHand.setCount(offHand.getCount() - 1);
            player.getInventory().offHand.set(0, offHand);
            player.getInventory().updateItems();

            ItemEntity itemEntity = player.dropItem(book, false);
            if (itemEntity != null) {
                itemEntity.resetPickupDelay();
                itemEntity.setOwner(player.getUuid());
            }
        }

        source.sendMessage(Text.literal("Enchantments extracted !").formatted(Formatting.GREEN));
        return 1;
    }

}

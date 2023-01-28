package me.polishkrowa.politweaks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.HashMap;
import java.util.UUID;

public class HandleRightClick {

    private static HashMap<UUID, Boolean> second = new HashMap<>();

    public static void handleRightClickEntity(ServerPlayerEntity player, Entity clickedEntity, Hand hand) {
//        System.out.println(player.getName() + "Rigjt clicked with " + hand.name());
        if (hand.equals(Hand.OFF_HAND)) return;

        //handle double packet send (ignores second)
        if (!second.containsKey(player.getUuid())) second.put(player.getUuid(), false);
        second.put(player.getUuid(), !second.get(player.getUuid()));
        if (second.get(player.getUuid())) return;


        //Pl: Milk it
        if (clickedEntity instanceof PlayerEntity clickedPlayer && player.getInventory().getMainHandStack().getItem().equals(Items.BUCKET)) {
            player.getInventory().getMainHandStack().setCount(player.getInventory().getMainHandStack().getCount() - 1);
            ItemStack stack = new ItemStack(Items.MILK_BUCKET);
            stack.setCustomName(Text.literal(clickedPlayer.getName().getString() + "'s milk"));
            player.server.getPlayerManager().broadcast(Text.literal(player.getName().getString() + " milked " + clickedPlayer.getName().getString()).formatted(Formatting.AQUA), false);

            clickedPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 10));

            if (!player.getInventory().insertStack(stack)) {
                ItemEntity itemEntity = player.dropItem(stack, false);
                if (itemEntity != null) {
                    itemEntity.resetPickupDelay();
                    itemEntity.setOwner(player.getUuid());
                }
            }

        }
    }
}

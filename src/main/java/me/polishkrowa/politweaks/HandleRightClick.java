package me.polishkrowa.politweaks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class HandleRightClick {

    public static void handleRightClick(ServerPlayerEntity player, Entity clickedEntity, Hand hand) {
        if (hand.equals(Hand.OFF_HAND)) return;
        if (clickedEntity instanceof PlayerEntity clickedPlayer && player.getInventory().getMainHandStack().getItem().equals(Items.BUCKET)) {
            player.getInventory().getMainHandStack().setCount(player.getInventory().getMainHandStack().getCount() - 1);
            ItemStack stack = new ItemStack(Items.MILK_BUCKET);
            stack.setCustomName(Text.literal(clickedPlayer.getName().getString() + "'s milk"));
            player.server.getPlayerManager().broadcast(Text.literal(player.getName().getString() + " milked " + clickedPlayer.getName().getString()), MessageType.SYSTEM);

            if (!player.getInventory().insertStack(stack)) {
                ItemEntity itemEntity;
                itemEntity = player.dropItem(stack, false);
                if (itemEntity != null) {
                    itemEntity.resetPickupDelay();
                    itemEntity.setOwner(player.getUuid());
                }
            }
        }
    }
}

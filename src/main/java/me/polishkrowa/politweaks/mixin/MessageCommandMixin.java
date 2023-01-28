package me.polishkrowa.politweaks.mixin;

import me.polishkrowa.politweaks.commands.ReplyCommand;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;

@Mixin(targets = "net.minecraft.server.command.MessageCommand")
public class MessageCommandMixin {

    @Inject(method = "method_44144", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void injected(ServerCommandSource serverCommandSource, Collection collection, MessageType.Parameters parameters, SignedMessage message, CallbackInfo ci, SentMessage sentMessage, boolean bl, Entity entity, boolean bl2, Iterator var8, ServerPlayerEntity serverPlayerEntity, MessageType.Parameters parameters2, boolean bl3) {
        ReplyCommand.setLastMessaged(serverPlayerEntity, serverCommandSource.getPlayer());
        ReplyCommand.setLastMessaged(serverCommandSource.getPlayer(), serverPlayerEntity);
    }
}

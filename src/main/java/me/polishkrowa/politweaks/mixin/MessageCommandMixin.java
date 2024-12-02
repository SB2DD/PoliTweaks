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

    @Inject(method = "execute(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Lnet/minecraft/network/message/SignedMessage;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void injected(ServerCommandSource source, Collection<ServerPlayerEntity> targets, SignedMessage message, CallbackInfo ci, MessageType.Parameters parameters, SentMessage sentMessage, boolean bl, Iterator var6, ServerPlayerEntity serverPlayerEntity, MessageType.Parameters parameters2, boolean bl2) {
        ReplyCommand.setLastMessaged(serverPlayerEntity, source.getPlayer());
        ReplyCommand.setLastMessaged(source.getPlayer(), serverPlayerEntity);
    }
}

package me.polishkrowa.politweaks.mixin;

import me.polishkrowa.politweaks.commands.ReplyCommand;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;

@Mixin(targets = "net.minecraft.server.command.MessageCommand")
public class MessageCommandMixin {

    @Inject(method = "method_44144", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;sendFeedback(Lnet/minecraft/text/Text;Z)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void injected(Collection<ServerPlayerEntity> targets, ServerCommandSource serverCommandSource, FilteredMessage<SignedMessage> signedMessage, CallbackInfo ci, Text text, Iterator var4, ServerPlayerEntity serverPlayerEntity) {
        ReplyCommand.setLastMessaged(serverPlayerEntity, serverCommandSource.getPlayer());
        ReplyCommand.setLastMessaged(serverCommandSource.getPlayer(), serverPlayerEntity);
    }
}

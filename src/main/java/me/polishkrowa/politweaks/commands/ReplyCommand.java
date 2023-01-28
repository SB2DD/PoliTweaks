package me.polishkrowa.politweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class ReplyCommand {

    public static HashMap<ServerPlayerEntity, ServerPlayerEntity> lastMessaged = new HashMap<>();
//player from

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(CommandManager.literal("reply").then(CommandManager.argument("message", MessageArgumentType.message()).executes((context) -> {
            return execute(context.getSource(), MessageArgumentType.getSignedMessage(context, "message"));
        })));
        dispatcher.register(CommandManager.literal("r").redirect(literalCommandNode));

    }

    private static int execute(ServerCommandSource source, MessageArgumentType.SignedMessage signedMessage) {
        signedMessage.decorate(source, (decoratedMessage) -> {
            SentMessage sentMessage = SentMessage.of(decoratedMessage);


            ServerPlayerEntity serverPlayerEntity = lastMessaged.get(source.getPlayer());
            source.sendFeedback(Text.literal("You replied to ").append(serverPlayerEntity.getDisplayName()).append(Text.literal(": ")).append(sentMessage.getContent()).formatted(Formatting.GRAY, Formatting.ITALIC), false);

            MessageType.Parameters parameters = MessageType.params(MessageType.MSG_COMMAND_INCOMING, source);
            boolean bl3 = source.shouldFilterText(serverPlayerEntity);
            serverPlayerEntity.sendChatMessage(sentMessage, bl3, parameters);

//            SignedMessage signedMessage = decoratedMessage.getFilterableFor(source, serverPlayerEntity);
//            if (signedMessage != null) {
//                serverPlayerEntity.sendChatMessage(signedMessage, source.getChatMessageSender(), MessageType.MSG_COMMAND);
//            }


        });
        return 1;
    }



    public static void setLastMessaged(ServerPlayerEntity player, ServerPlayerEntity from) {
        lastMessaged.put(player, from);
    }


}

package me.polishkrowa.politweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

import java.util.ArrayList;
import java.util.List;

public class EnchantExtractorCommand {

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("switchdifficulty");
        List<Difficulty> difficulties = new ArrayList<>();
        difficulties.add(Difficulty.EASY);
        difficulties.add(Difficulty.NORMAL);
        difficulties.add(Difficulty.HARD);

        for (Difficulty difficulty : difficulties) {
            literalArgumentBuilder
                    .then(CommandManager.literal(difficulty.getName())
                            .executes((context) -> execute(context.getSource(), difficulty))
                    );
        }
        literalArgumentBuilder
                .executes((context) -> {
            Difficulty d = context.getSource().getPlayer().getWorld().getDifficulty();
            context.getSource().sendFeedback(Text.literal("The difficulty is currently " + d.getName()), false);
            return d.getId();
        });

        LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literalArgumentBuilder);

        dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>)CommandManager.literal("sw").executes(node.getCommand()).redirect(node));
    }

    private static int execute(ServerCommandSource source, Difficulty difficulty) {
        source.getServer().setDifficulty(difficulty, false);
        source.sendFeedback(Text.literal("The difficulty is now " + difficulty.getName()), false);
        return 0;
    }

}

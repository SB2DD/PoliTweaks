package me.polishkrowa.politweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class StructureCompassCommand {

    private static final DynamicCommandExceptionType STRUCTURE_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType((id) -> {
        return Text.translatable("commands.locate.structure.not_found", new Object[]{id});
    });
    private static final DynamicCommandExceptionType STRUCTURE_INVALID_EXCEPTION = new DynamicCommandExceptionType((id) -> {
        return Text.translatable("commands.locate.structure.invalid", new Object[]{id});
    });
    private static final DynamicCommandExceptionType BIOME_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType((id) -> {
        return Text.translatable("commands.locate.biome.not_found", new Object[]{id});
    });
    private static final DynamicCommandExceptionType BIOME_INVALID_EXCEPTION = new DynamicCommandExceptionType((id) -> {
        return Text.translatable("commands.locate.biome.invalid", new Object[]{id});
    });

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(CommandManager.literal("structure-compass")
        .then(CommandManager.literal("structure")
                .then(CommandManager.argument("structure", RegistryPredicateArgumentType.registryPredicate(Registry.STRUCTURE_KEY)).executes((commandContext) -> {
            return executeLocateStructure(commandContext.getSource(), RegistryPredicateArgumentType.getPredicate(commandContext, "structure", Registry.STRUCTURE_KEY, STRUCTURE_INVALID_EXCEPTION), false);

                    }).then(CommandManager.argument("skipLocated", BoolArgumentType.bool()).executes((commandContext) -> {
            return executeLocateStructure(commandContext.getSource(), RegistryPredicateArgumentType.getPredicate(commandContext, "structure", Registry.STRUCTURE_KEY, STRUCTURE_INVALID_EXCEPTION), BoolArgumentType.getBool(commandContext, "skipLocated"));
        })))).then(CommandManager.literal("biome")
                        .then(CommandManager.argument("biome", RegistryPredicateArgumentType.registryPredicate(Registry.BIOME_KEY)).executes((commandContext) -> {
            return executeLocateBiome(commandContext.getSource(), RegistryPredicateArgumentType.getPredicate(commandContext, "biome", Registry.BIOME_KEY, BIOME_INVALID_EXCEPTION));
        }))));
        dispatcher.register(CommandManager.literal("sc").redirect(literalCommandNode));
        dispatcher.register(CommandManager.literal("s-c").redirect(literalCommandNode));
    }

    private static int executeLocateStructure(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<Structure> predicate, boolean skipFound) throws CommandSyntaxException {
        Registry<Structure> registry = source.getWorld().getRegistryManager().get(Registry.STRUCTURE_KEY);
        RegistryEntryList<Structure> registryEntryList = (RegistryEntryList)getStructureListForPredicate(predicate, registry).orElseThrow(() -> {
            return STRUCTURE_INVALID_EXCEPTION.create(predicate.asString());
        });
        BlockPos blockPos = new BlockPos(source.getPosition());
        ServerWorld serverWorld = source.getWorld();
        Pair<BlockPos, RegistryEntry<Structure>> pair = serverWorld.getChunkManager().getChunkGenerator().locateStructure(serverWorld, registryEntryList, blockPos, 100, skipFound);
        if (pair == null) {
            throw STRUCTURE_NOT_FOUND_EXCEPTION.create(predicate.asString());
        } else {
//            return sendCoordinates(source, predicate, blockPos, pair, "commands.locate.structure.success", false);

            return giveCompass(source,pair.getFirst(), skipFound);
        }
    }

    private static Optional<? extends RegistryEntryList.ListBacked<Structure>> getStructureListForPredicate(RegistryPredicateArgumentType.RegistryPredicate<Structure> predicate, Registry<Structure> structureRegistry) {
        return predicate.getKey().map(key -> structureRegistry.getEntry((RegistryKey<Structure>)key).map(entry -> RegistryEntryList.of(entry)), structureRegistry::getEntryList);
    }

    private static int executeLocateBiome(ServerCommandSource source, RegistryPredicateArgumentType.RegistryPredicate<Biome> predicate) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(source.getPosition());
        Pair<BlockPos, RegistryEntry<Biome>> pair = source.getWorld().locateBiome(predicate, blockPos, 6400, 32, 64);
        if (pair == null) {
            throw BIOME_NOT_FOUND_EXCEPTION.create(predicate.asString());
        } else {
            return giveCompass(source,pair.getFirst(), false);
//            return sendCoordinates(source, predicate, blockPos, pair, "commands.locate.biome.success", true);
        }
    }


    private static int giveCompass(ServerCommandSource source, BlockPos pos, boolean skipFoundCoords) {

        source.sendFeedback(Text.literal(pos.toShortString() + "  f: " + skipFoundCoords), false);
        return 0;
    }


}

package me.polishkrowa.politweaks;

import me.polishkrowa.politweaks.commands.SwitchDifficultyCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolishTweaks implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("politweaks");

    @Override
    public void onInitialize() {
        //TODO: better msg plus, enchant extractor,
        // structure compass, xray enchant, list biomes/missing stuff in achievements command

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> SwitchDifficultyCommand.registerCommand(dispatcher)));

    }
}

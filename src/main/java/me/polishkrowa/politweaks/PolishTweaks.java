package me.polishkrowa.politweaks;

import me.polishkrowa.politweaks.commands.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolishTweaks implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("politweaks");

    @Override
    public void onInitialize() {
        //TODO: structure compass, list biomes/missing stuff in achievements command
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> SwitchDifficultyCommand.registerCommand(dispatcher)));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> ReplyCommand.registerCommand(dispatcher)));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> EnchantExtractorCommand.registerCommand(dispatcher)));
//        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> XrayEnchantCommand.registerCommand(dispatcher)));
        //allow frost walker in boats (Not working)

        //Idk how to make a registry filter or whatever...
//        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> StructureCompassCommand.registerCommand(dispatcher)));
    }
}

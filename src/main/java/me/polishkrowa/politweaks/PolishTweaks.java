package me.polishkrowa.politweaks;

import me.polishkrowa.politweaks.commands.EnchantExtractorCommand;
import me.polishkrowa.politweaks.commands.ReplyCommand;
import me.polishkrowa.politweaks.commands.SwitchDifficultyCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolishTweaks implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("politweaks");

    @Override
    public void onInitialize() {
        //TODO:
        // structure compass, xray enchant, list biomes/missing stuff in achievements command

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> SwitchDifficultyCommand.registerCommand(dispatcher)));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> ReplyCommand.registerCommand(dispatcher)));
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> EnchantExtractorCommand.registerCommand(dispatcher)));
    }
}

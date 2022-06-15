package me.polishkrowa.politweaks.mixin;

import me.polishkrowa.politweaks.HandleRightClick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets = {"net.minecraft.server.network.ServerPlayNetworkHandler$1"})
public class ServerPlayNetworkHandlerMixin {

    @ModifyArgs(method = "processInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler$Interaction;run(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    private void injected(Args args) {
        HandleRightClick.handleRightClickEntity(args.get(0), args.get(1), args.get(2));
    }

}

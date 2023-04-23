package me.polishkrowa.politweaks.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.polishkrowa.politweaks.whatever.BungeeClientConnection;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {
    private boolean bypassProxyBungee = false;
    @Shadow
    @Final
    public ClientConnection connection;
    @Shadow
    GameProfile profile;


    @Shadow private ServerLoginNetworkHandler.State state;

    @Shadow protected abstract void addToServer(ServerPlayerEntity player);

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onHello", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;profile:Lcom/mojang/authlib/GameProfile;", shift = At.Shift.AFTER))
    private void initUuid(CallbackInfo ci) {
//        System.out.println("5");
        if (((BungeeClientConnection) connection).getSpoofedUUID() == null) {
//            System.out.println("5.1");
            bypassProxyBungee = true;
            return;
        }

        this.profile = new GameProfile(((BungeeClientConnection) connection).getSpoofedUUID(), this.profile.getName());

        if (((BungeeClientConnection) connection).getSpoofedProfile() != null) {
            for (Property property : ((BungeeClientConnection) connection).getSpoofedProfile()) {
                this.profile.getProperties().put(property.getName(), property);
//                System.out.println("6.1");
//                System.out.println(property.getName());
//                System.out.println(property);
            }
//            System.out.println("6");
        }

    }

    @Redirect(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isOnlineMode()Z"))
    private boolean skipKeyPacket(MinecraftServer minecraftServer) {
        return bypassProxyBungee && minecraftServer.isOnlineMode();
    }
//
//    @Inject(method = "onHello", at = @At(value = "RETURN"))
//    private void returned(CallbackInfo ci) {
//        System.out.println(this.state);
//        System.out.println(this.profile.isComplete());
//    }

    @Inject(method = "addToServer", at = @At(value = "HEAD"), cancellable = true)
    private void returnedd(ServerPlayerEntity player, CallbackInfo ci) {
//        System.out.println("called");
        this.server.getPlayerManager().onPlayerConnect(this.connection, player);
        ci.cancel();

    }
}
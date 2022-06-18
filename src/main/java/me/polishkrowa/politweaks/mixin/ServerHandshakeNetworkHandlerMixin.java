package me.polishkrowa.politweaks.mixin;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import me.polishkrowa.politweaks.whatever.BungeeClientConnection;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {

    private static final Gson gson = new Gson();

    @Shadow
    @Final
    private ClientConnection connection;

    @Inject(method = "onHandshake", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/ClientConnection;)V"))
    private void injected(HandshakeC2SPacket packet, CallbackInfo ci) {
        if (packet.getIntendedState().equals(NetworkState.LOGIN)) {
            String[] split = ((HandshakeC2SPacketAccessor) packet).getAddress().split("\00");
            if (split.length == 3 || split.length == 4) {
                ((ClientConnectionAccessor) connection).setAddress(new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) connection.getAddress()).getPort()));
                ((BungeeClientConnection) connection).setSpoofedUUID(UUIDTypeAdapter.fromString(split[2]));

                if (split.length == 4) {
                    ((BungeeClientConnection) connection).setSpoofedProfile(gson.fromJson(split[3], Property[].class));
                }
            }
        }
    }

}

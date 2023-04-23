package me.polishkrowa.politweaks.mixin;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import me.polishkrowa.politweaks.whatever.BungeeClientConnection;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {

    private static final Gson gson = new Gson();

    @Shadow
    @Final
    private ClientConnection connection;

    private static String[] seecret = null;

    @Inject(method = "onHandshake", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/ClientConnection;)V"))
    private void injected(HandshakeC2SPacket packet, CallbackInfo ci) throws IOException {
//        System.out.println("0.1");
        if (packet.getIntendedState().equals(NetworkState.LOGIN)) {
//            System.out.println("1");
            String[] split = ((HandshakeC2SPacketAccessor) packet).getAddress().split("\00");
//            System.out.println(split.length);
//            System.out.println(((HandshakeC2SPacketAccessor) packet).getAddress());

            if (split.length == 3 || split.length == 4) {
//                System.out.println("1.5");
                ((ClientConnectionAccessor) connection).setAddress(new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) connection.getAddress()).getPort()));
                ((BungeeClientConnection) connection).setSpoofedUUID(UUIDTypeAdapter.fromString(split[2]));

                if (getSeecret().length == 0) {
                    ((BungeeClientConnection) connection).setSpoofedProfile(gson.fromJson(split[3], Property[].class));
                    return;
                } else if (split.length == 4) {
                    Property[] properties = gson.fromJson(split[3], Property[].class);

                    Property[] modified = new Property[properties.length - 1];
//                    System.out.println("1.6");
                    int i = 0;
                    boolean found = false;
                    for (Property property : properties) {
                        if ("bungeeguard-token".equals(property.getName())) {
                            if (!(found = !found && Arrays.binarySearch(seecret, property.getValue()) >= 0)) {
                                break;
                            }
                        } else if (i != modified.length) {
                            modified[i++] = property;
                        }
                    }
//                    System.out.println("2");
                    if (found) {
                        ((BungeeClientConnection) connection).setSpoofedProfile(modified);
//                        System.out.println("3");
                    } else {
                        Text disconnectMessage = Text.literal("Invalid forwarding info. Please contact Admin.");
                        connection.send(new LoginDisconnectS2CPacket(disconnectMessage));
                        connection.disconnect(disconnectMessage);
                    }

                } else {
                    Text disconnectMessage = Text.literal("If you wish to use IP forwarding, please enable it in your BungeeCord config as well!");
                    connection.send(new LoginDisconnectS2CPacket(disconnectMessage));
                    connection.disconnect(disconnectMessage);
                }
            } else {
                Text disconnectMessage = Text.literal("You are not from a valid proxy!");
                connection.send(new LoginDisconnectS2CPacket(disconnectMessage));
                connection.disconnect(disconnectMessage);
            }
        }
    }

    private static String[] getSeecret() throws IOException {
        if (seecret == null) {
            File config = new File("seecret.txt");
            if (config.exists()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(config)))) {
                    HashSet<String> tokens = new HashSet<>();
                    for (String line; (line = reader.readLine()) != null;) {
                        if (line.startsWith("bungeeguard-token=") && (line = line.substring(18)).length() != 0) {
                            tokens.add(line);
                        }
                    }
                    Arrays.sort(seecret = (tokens.size() != 0)? tokens.toArray(new String[0]) : new String[0]);
                }
            } else {
                seecret = new String[0];
                PrintWriter writer = new PrintWriter(config, StandardCharsets.UTF_8.name());
                writer.println("# As you may know, standard IP forwarding procedure is to accept connections from any and all proxies.");
                writer.println("# If you'd like to change that, you can do so by entering BungeeGuard tokens here.");
                writer.println("# ");
                writer.println("# This file is automatically generated by VanillaCord once a player attempts to join the server.");
                writer.println();
                writer.println("bungeeguard-token=");
                writer.close();
            }
        }
        return seecret;
    }

}

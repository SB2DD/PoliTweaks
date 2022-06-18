package me.polishkrowa.politweaks.mixin;

import com.mojang.authlib.properties.Property;
import me.polishkrowa.politweaks.whatever.BungeeClientConnection;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;

import java.util.UUID;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements BungeeClientConnection {

    UUID sppoofedUUID;
    Property[] spoofedProfile;

    @Override
    public UUID getSpoofedUUID() {
        return sppoofedUUID;
    }

    @Override
    public void setSpoofedUUID(UUID uuid) {
        sppoofedUUID = uuid;
    }

    @Override
    public Property[] getSpoofedProfile() {
        return spoofedProfile;
    }

    @Override
    public void setSpoofedProfile(Property[] profile) {
        spoofedProfile = profile;
    }
}
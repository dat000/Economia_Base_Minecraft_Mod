package net.mcreator.economia.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.mcreator.economia.client.EconomyClientCache;

import java.util.function.Supplier;

public class SyncEconomyConfigPacket {
    private final String currencyName;
    private final String currencyPrefix;

    public SyncEconomyConfigPacket(String currencyName, String currencyPrefix) {
        this.currencyName = currencyName;
        this.currencyPrefix = currencyPrefix;
    }

    public SyncEconomyConfigPacket(FriendlyByteBuf buffer) {
        this.currencyName = buffer.readUtf();
        this.currencyPrefix = buffer.readUtf();
    }

    public static void encode(SyncEconomyConfigPacket message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.currencyName);
        buffer.writeUtf(message.currencyPrefix);
    }

    public static SyncEconomyConfigPacket decode(FriendlyByteBuf buffer) {
        return new SyncEconomyConfigPacket(buffer);
    }

    public static void handle(SyncEconomyConfigPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Se ejecuta en el cliente al recibir el paquete
            EconomyClientCache.set(message.currencyName, message.currencyPrefix);
        });
        context.setPacketHandled(true);
    }
}
package net.mcreator.economia;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.mcreator.economia.network.EconomiaNetwork;
import net.mcreator.economia.network.SyncEconomyConfigPacket;

@Mod.EventBusSubscriber
public class EconomySyncEventHandler {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            String name = EconomyConfig.CURRENCY_NAME.get();
            String prefix = EconomyConfig.CURRENCY_PREFIX.get();

            EconomiaNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SyncEconomyConfigPacket(name, prefix)
            );
        }
    }
}
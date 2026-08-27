package net.mcreator.economia;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mod.EventBusSubscriber
public class BountyEventListener {

    @SubscribeEvent
    public static void onPlayerKill(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        Entity trueSource = event.getSource().getEntity();

        if (event.getEntity() instanceof ServerPlayer victim && trueSource instanceof ServerPlayer killer) {
            if (victim.getUUID().equals(killer.getUUID())) return;

            TransactionManager manager = TransactionManager.get(victim.serverLevel());
            double bountyAmount = manager.getBounty(victim.getUUID());

            if (bountyAmount > 0) {
                // 1. Damos el dinero pero enviamos "" para saltar el historial feo de la API
                EconomyAPI.addMoney(killer, bountyAmount, "");

                // 2. Escribimos el historial a mano imitando tu imagen derecha
                String timeStamp = new SimpleDateFormat("HH:mm, dd-MM").format(new Date());
                String historyText = "§7[" + timeStamp + "] §a+ $" + bountyAmount + " §f(Bounty: " + victim.getScoreboardName() + ")";
                manager.addTransaction(killer.getUUID(), historyText);

                // Limpiamos la recompensa
                manager.removeBounty(victim.getUUID());

                victim.server.getPlayerList().broadcastSystemMessage(
                        Component.literal("§a[BOUNTY] §c" + killer.getScoreboardName() +
                                " §fhas claimed §e$" + bountyAmount +
                                " §ffor killing §c" + victim.getScoreboardName() + "§f!"), false
                );
            }
        }
    }
}
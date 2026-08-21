package net.mcreator.economia.procedures;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class AdminPayAllProcedure {
    public static void execute(CommandSourceStack source, double amount) {
        if (source.getEntity() == null) return;
        Entity entity = source.getEntity();
        ServerLevel serverLevel = source.getLevel();

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm, dd-MM");
        String timestamp = "§7[" + java.time.LocalDateTime.now().format(formatter) + "] ";

        // Iterar sobre todos los jugadores conectados
        for (ServerPlayer targetPlayer : serverLevel.players()) {
            targetPlayer.getCapability(net.mcreator.economia.network.EconomiaModVariables.PLAYER_VARIABLES).ifPresent(capability -> {
                capability.money += amount;
                capability.markSyncDirty();

                // Agregar al historial de cada uno (Corregido cierre de paréntesis)
                net.mcreator.economia.TransactionManager.get(serverLevel).addTransaction(targetPlayer.getUUID(),
                        timestamp + "§a+ " + net.mcreator.economia.EconomyConfig.formatMoney(amount) + " §7from §5Server Admin");

                // Mensaje y sonido para cada jugador (Corregido el boolean "false" y cierre)
                targetPlayer.displayClientMessage(Component.literal("§aYou received §e" + net.mcreator.economia.EconomyConfig.formatMoney(amount) + "§a from a §5Server Administrator§a!"), false);
                targetPlayer.playNotifySound(net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);
            });
        }

        // Mensaje de éxito para el administrador que ejecutó el comando (Corregido para usar formatMoney)
        if (entity instanceof net.minecraft.world.entity.player.Player _admin) {
            _admin.displayClientMessage(Component.literal("§aSuccessfully sent §e" + net.mcreator.economia.EconomyConfig.formatMoney(amount) + "§a to all online players."), false);
        }
    }
}
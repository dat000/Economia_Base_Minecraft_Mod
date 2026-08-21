package net.mcreator.economia.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import com.mojang.brigadier.context.CommandContext;

import net.mcreator.economia.TransactionManager;
import net.mcreator.economia.EconomyConfig;
import net.mcreator.economia.network.EconomiaModVariables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaltopProcedure {
    public static void execute(CommandContext<CommandSourceStack> arguments, Entity entity) {
        if (entity == null) return;
        MinecraftServer server = entity.getServer();
        if (server == null) return;

        ServerLevel serverLevel = server.overworld();
        TransactionManager manager = TransactionManager.get(serverLevel);

        // --- 1. ACTUALIZAR AUTOMÁTICAMENTE A TODOS LOS ONLINE AL EJECUTAR EL COMANDO ---
        for (ServerPlayer onlinePlayer : server.getPlayerList().getPlayers()) {
            double currentMoney = onlinePlayer.getCapability(EconomiaModVariables.PLAYER_VARIABLES)
                    .orElseGet(EconomiaModVariables.PlayerVariables::new).money;

            // Sincroniza su saldo y nombre actual en la base de datos del mundo al instante
            manager.setBalance(onlinePlayer.getUUID(), onlinePlayer.getDisplayName().getString(), currentMoney);
        }

        // --- 2. RECOPILAR TODOS LOS SALDOS (ONLINE ACTUALIZADOS + OFFLINE GUARDADOS) ---
        class PlayerBalance {
            String name;
            double balance;
            PlayerBalance(String name, double balance) {
                this.name = name;
                this.balance = balance;
            }
        }

        List<PlayerBalance> rankings = new ArrayList<>();
        Map<UUID, Double> allBalances = manager.getAllBalances();

        for (Map.Entry<UUID, Double> entry : allBalances.entrySet()) {
            UUID uuid = entry.getKey();
            double balance = entry.getValue();
            String playerName = manager.getPlayerName(uuid);

            rankings.add(new PlayerBalance(playerName, balance));
        }

        // --- 3. ORDENAR DE MAYOR A MENOR Y MOSTRAR ---
        rankings.sort((a, b) -> Double.compare(b.balance, a.balance));

        if (entity instanceof Player _player && !_player.level().isClientSide()) {
            _player.displayClientMessage(Component.literal("§6§l=== 🏆 Economy Leaderboard (Top) ==="), false);

            int limit = Math.min(5, rankings.size());
            for (int i = 0; i < limit; i++) {
                PlayerBalance entry = rankings.get(i);
                String rankSign = switch (i) {
                    case 0 -> "§e§l1. ";
                    case 1 -> "§7§l2. ";
                    case 2 -> "§c§l3. ";
                    default -> "§7" + (i + 1) + ". ";
                };
                _player.displayClientMessage(Component.literal(rankSign + entry.name + " §7- §e" + EconomyConfig.formatMoney(entry.balance)), false);
            }
            _player.displayClientMessage(Component.literal("§6§l=================================="), false);
        }
    }
}
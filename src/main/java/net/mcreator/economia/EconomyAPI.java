package net.mcreator.economia;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import net.mcreator.economia.network.EconomiaModVariables;

import java.util.UUID;

public class EconomyAPI {

    // ==========================================
    // CONSULTAS
    // ==========================================

    public static double getBalance(ServerLevel level, UUID playerUuid) {
        if (level == null || playerUuid == null) return 0.0;

        ServerPlayer player = level.getServer().getPlayerList().getPlayer(playerUuid);

        // Si el jugador está online, leemos el dinero real de la pantalla de MCreator
        if (player != null) {
            double dineroReal = player.getCapability(EconomiaModVariables.PLAYER_VARIABLES, null)
                    .map(cap -> cap.money)
                    .orElse(0.0);

            // Actualizamos la base de datos de fondo silenciosamente (sirve para el baltop)
            TransactionManager.get(level).setBalance(playerUuid, player.getScoreboardName(), dineroReal);

            return dineroReal;
        }

        // Si el jugador está offline, leemos de la base de datos de la API
        return TransactionManager.get(level).getBalance(playerUuid);
    }

    public static boolean hasEnough(ServerLevel level, UUID playerUuid, double amount) {
        return getBalance(level, playerUuid) >= amount;
    }

    public static String getPlayerName(ServerLevel level, UUID playerUuid) {
        if (level == null || playerUuid == null) return "Unknown";
        return TransactionManager.get(level).getPlayerName(playerUuid);
    }

    // ==========================================
    // MODIFICACIONES DE SALDO
    // ==========================================

    public static void setMoney(ServerLevel level, UUID playerUuid, String playerName, double amount) {
        if (level == null || playerUuid == null) return;

        TransactionManager manager = TransactionManager.get(level);
        double cantidadFinal = Math.max(0, amount);

        manager.setBalance(playerUuid, playerName, cantidadFinal);

        ServerPlayer player = level.getServer().getPlayerList().getPlayer(playerUuid);
        if (player != null) {
            player.getCapability(EconomiaModVariables.PLAYER_VARIABLES, null).ifPresent(capability -> {

                // Actualiza el dinero visual
                capability.money = cantidadFinal;

                // Le avisa al juego que debe actualizar la pantalla del jugador
                capability.markSyncDirty();

            });
        }
    }

    public static boolean transferMoney(ServerLevel level, UUID senderUuid, String senderName, UUID receiverUuid, String receiverName, double amount) {
        if (level == null || senderUuid == null || receiverUuid == null || amount <= 0) return false;

        // 1. Validar si el emisor está congelado
        FrozenAccountsManager frozenManager = FrozenAccountsManager.get(level);
        if (frozenManager.isFrozen(senderUuid)) {
            return false;
        }

        // 2. Definir tasa de impuestos (5% = 0.05)
        double taxRate = EconomyConfig.TRANSFER_TAX_RATE.get();
        double taxAmount = amount * taxRate;
        double netAmount = amount - taxAmount;

        // 3. Verificar si el emisor tiene suficiente para el total
        if (!hasEnough(level, senderUuid, amount)) {
            return false;
        }

        // 4. Ejecutar el movimiento monetario a través de la API (esto ya maneja su propio historial base,
        // pero podemos personalizarlo o dejar que la API gestione los saldos limpios).
        double senderBalance = getBalance(level, senderUuid);
        double receiverBalance = getBalance(level, receiverUuid);

        setMoney(level, senderUuid, senderName, senderBalance - amount);
        setMoney(level, receiverUuid, receiverName, receiverBalance + netAmount);

        // 5. El impuesto va directo a la Tesorería del Banco Central
        if (taxAmount > 0) {
            CentralBankManager bankManager = CentralBankManager.get(level);
            bankManager.addTreasury(taxAmount);
        }

        // 6. Registro detallado y limpio en el historial de ambos jugadores
        String timeStamp = new java.text.SimpleDateFormat("HH:mm, dd-MM").format(new java.util.Date());

        TransactionManager manager = TransactionManager.get(level);
        manager.addTransaction(senderUuid, "§7[" + timeStamp + "] §c-$" + amount + " §f(Transfer to " + receiverName + ", Tax: $" + taxAmount + ")");
        manager.addTransaction(receiverUuid, "§7[" + timeStamp + "] §a+$" + netAmount + " §f(Transfer from " + senderName + ")");

        return true;
    }


    public static void addMoney(ServerLevel level, UUID playerUuid, String playerName, double amount, String reason) {
        if (amount <= 0) return;
        double currentBalance = getBalance(level, playerUuid);
        setMoney(level, playerUuid, playerName, currentBalance + amount);

        // Registro opcional en el historial
        if (reason != null && !reason.isEmpty()) {
            TransactionManager.get(level).addTransaction(playerUuid, "+$" + amount + " (" + reason + ")");
        }
    }

    public static boolean removeMoney(ServerLevel level, UUID playerUuid, String playerName, double amount, String reason) {
        if (amount <= 0) return false;

        // --- VALIDACIÓN DE CUENTA CONGELADA ---
        FrozenAccountsManager manager = FrozenAccountsManager.get(level);
        if (manager.isFrozen(playerUuid)) {
            ServerPlayer onlinePlayer = level.getServer().getPlayerList().getPlayer(playerUuid);
            if (onlinePlayer != null) {
                onlinePlayer.sendSystemMessage(Component.literal("§cYour account is frozen. You cannot perform this economic action."));
            }
            return false;
        }
        // -------------------------------------

        if (!hasEnough(level, playerUuid, amount)) return false;

        double currentBalance = getBalance(level, playerUuid);
        setMoney(level, playerUuid, playerName, currentBalance - amount);

        // Registro opcional en el historial
        if (reason != null && !reason.isEmpty()) {
            TransactionManager.get(level).addTransaction(playerUuid, "-$" + amount + " (" + reason + ")");
        }
        return true;
    }

    // ==========================================
    // ATAJOS CON ServerPlayer
    // ==========================================

    public static double getBalance(ServerPlayer player) {
        if (player == null) return 0.0;
        return getBalance((ServerLevel) player.level(), player.getUUID());
    }

    public static boolean hasEnough(ServerPlayer player, double amount) {
        if (player == null) return false;
        return hasEnough((ServerLevel) player.level(), player.getUUID(), amount);
    }

    public static void addMoney(ServerPlayer player, double amount, String reason) {
        if (player == null) return;
        addMoney((ServerLevel) player.level(), player.getUUID(), player.getScoreboardName(), amount, reason);
    }

    public static boolean removeMoney(ServerPlayer player, double amount, String reason) {
        if (player == null) return false;
        return removeMoney((ServerLevel) player.level(), player.getUUID(), player.getScoreboardName(), amount, reason);
    }

    // Atajos simples sin motivo especificado
    public static void addMoney(ServerPlayer player, double amount) {
        addMoney(player, amount, "API Transaction");
    }

    public static boolean removeMoney(ServerPlayer player, double amount) {
        return removeMoney(player, amount, "API Transaction");
    }
}
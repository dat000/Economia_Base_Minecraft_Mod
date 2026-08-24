package net.mcreator.economia;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class EconomyAPI {

    // ==========================================
    // CONSULTAS
    // ==========================================

    public static double getBalance(ServerLevel level, UUID playerUuid) {
        if (level == null || playerUuid == null) return 0.0;
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
        manager.setBalance(playerUuid, playerName, Math.max(0, amount));
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
        if (amount <= 0 || !hasEnough(level, playerUuid, amount)) return false;

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
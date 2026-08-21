package net.mcreator.economia;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class TransactionManager extends SavedData {

    private final Map<UUID, List<String>> historyMap = new HashMap<>();
    private final Map<UUID, Double> balancesMap = new HashMap<>();
    private final Map<UUID, String> playerNamesMap = new HashMap<>(); // NUEVO: Guardar nombres
    private static final int MAX_HISTORY = 30;

    public TransactionManager() {}

    public static TransactionManager load(CompoundTag tag) {
        TransactionManager data = new TransactionManager();

        if (tag.contains("Histories")) {
            CompoundTag playersTag = tag.getCompound("Histories");
            for (String uuidStr : playersTag.getAllKeys()) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    ListTag listTag = playersTag.getList(uuidStr, Tag.TAG_STRING);
                    List<String> history = new ArrayList<>();
                    for (int i = 0; i < listTag.size(); i++) {
                        history.add(listTag.getString(i));
                    }
                    data.historyMap.put(uuid, history);
                } catch (Exception e) {}
            }
        }

        if (tag.contains("PlayerBalances")) {
            CompoundTag balancesTag = tag.getCompound("PlayerBalances");
            for (String uuidStr : balancesTag.getAllKeys()) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    data.balancesMap.put(uuid, balancesTag.getDouble(uuidStr));
                } catch (Exception e) {}
            }
        }

        // NUEVO: Cargar los nombres guardados
        if (tag.contains("PlayerNames")) {
            CompoundTag namesTag = tag.getCompound("PlayerNames");
            for (String uuidStr : namesTag.getAllKeys()) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    data.playerNamesMap.put(uuid, namesTag.getString(uuidStr));
                } catch (Exception e) {}
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag playersTag = new CompoundTag();
        for (Map.Entry<UUID, List<String>> entry : historyMap.entrySet()) {
            ListTag listTag = new ListTag();
            for (String s : entry.getValue()) {
                listTag.add(StringTag.valueOf(s));
            }
            playersTag.put(entry.getKey().toString(), listTag);
        }
        tag.put("Histories", playersTag);

        CompoundTag balancesTag = new CompoundTag();
        for (Map.Entry<UUID, Double> entry : balancesMap.entrySet()) {
            balancesTag.putDouble(entry.getKey().toString(), entry.getValue());
        }
        tag.put("PlayerBalances", balancesTag);

        // NUEVO: Guardar los nombres
        CompoundTag namesTag = new CompoundTag();
        for (Map.Entry<UUID, String> entry : playerNamesMap.entrySet()) {
            namesTag.putString(entry.getKey().toString(), entry.getValue());
        }
        tag.put("PlayerNames", namesTag);

        return tag;
    }

    public static TransactionManager get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
        return overworld.getDataStorage().computeIfAbsent(TransactionManager::load, TransactionManager::new, "economia_histories");
    }

    public void addTransaction(UUID playerUuid, String entry) {
        historyMap.putIfAbsent(playerUuid, new ArrayList<>());
        List<String> list = historyMap.get(playerUuid);
        if (list.size() >= MAX_HISTORY) {
            list.remove(0);
        }
        list.add(entry);
        this.setDirty();
    }

    public List<String> getHistory(UUID playerUuid) {
        return historyMap.getOrDefault(playerUuid, Collections.emptyList());
    }

    // --- MÉTODOS DE BALTOP ACTUALIZADOS ---

    // Ahora pedimos el nombre junto con el UUID
    public void setBalance(UUID playerUuid, String playerName, double balance) {
        balancesMap.put(playerUuid, balance);
        playerNamesMap.put(playerUuid, playerName); // Se guarda el nombre
        this.setDirty();
    }

    public double getBalance(UUID playerUuid) {
        return balancesMap.getOrDefault(playerUuid, 0.0);
    }

    public Map<UUID, Double> getAllBalances() {
        return balancesMap;
    }

    // Nuevo metodo para recuperar el nombre del disco
    public String getPlayerName(UUID uuid) {
        return playerNamesMap.getOrDefault(uuid, "Unknown");
    }
}
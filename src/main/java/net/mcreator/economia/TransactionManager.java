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
    private static final int MAX_HISTORY = 30; // Cambia este número si quieres guardar más transacciones

    // Constructor necesario
    public TransactionManager() {}

    // Metodo que CARGA los datos cuando se abre el servidor/mundo
    public static TransactionManager load(CompoundTag tag) {
        TransactionManager data = new TransactionManager();
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
            } catch (Exception e) {
                // Ignorar si hay algún UUID corrupto
            }
        }
        return data;
    }

    // Metodo que GUARDA los datos antes de que se apague el servidor/mundo
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
        return tag;
    }

    // Obtener la instancia actual de guardado
    public static TransactionManager get(ServerLevel level) {
        // Lo guardamos en el "Overworld" para que los datos sean globales en todas las dimensiones
        ServerLevel overworld = level.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);

        return overworld.getDataStorage().computeIfAbsent(
                TransactionManager::load,
                TransactionManager::new,
                "economia_histories"
        );
    }

    // Agregar nueva transacción
    public void addTransaction(UUID playerUuid, String entry) {
        historyMap.putIfAbsent(playerUuid, new ArrayList<>());
        List<String> list = historyMap.get(playerUuid);

        // Si llega al límite, borra la transacción más antigua (la primera de la lista)
        if (list.size() >= MAX_HISTORY) {
            list.remove(0);
        }
        list.add(entry);

        this.setDirty();
    }

    public List<String> getHistory(UUID playerUuid) {
        return historyMap.getOrDefault(playerUuid, Collections.emptyList());
    }
}
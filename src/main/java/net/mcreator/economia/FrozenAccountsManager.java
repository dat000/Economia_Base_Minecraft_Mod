package net.mcreator.economia;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FrozenAccountsManager extends SavedData {
    private static final String DATA_NAME = "economia_frozen_accounts";
    private final Set<UUID> frozenPlayers = new HashSet<>();

    public FrozenAccountsManager() {}

    public static FrozenAccountsManager load(CompoundTag nbt) {
        FrozenAccountsManager data = new FrozenAccountsManager();
        if (nbt.contains("frozenList")) {
            long[] list = nbt.getLongArray("frozenList");
            for (int i = 0; i < list.length; i += 2) {
                data.frozenPlayers.add(new UUID(list[i], list[i + 1]));
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        long[] list = new long[frozenPlayers.size() * 2];
        int index = 0;
        for (UUID uuid : frozenPlayers) {
            list[index++] = uuid.getMostSignificantBits();
            list[index++] = uuid.getLeastSignificantBits();
        }
        nbt.putLongArray("frozenList", list);
        return nbt;
    }

    public static FrozenAccountsManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FrozenAccountsManager::load, FrozenAccountsManager::new, DATA_NAME);
    }

    public boolean isFrozen(UUID uuid) {
        return frozenPlayers.contains(uuid);
    }

    public void setFrozen(UUID uuid, boolean frozen) {
        if (frozen) {
            frozenPlayers.add(uuid);
        } else {
            frozenPlayers.remove(uuid);
        }
        setDirty();
    }

    // Métodos de atajo para facilitar el uso en los comandos y eventos
    public void freeze(UUID uuid) {
        setFrozen(uuid, true);
    }

    public void unfreeze(UUID uuid) {
        setFrozen(uuid, false);
    }
}
package net.mcreator.economia;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CentralBankManager extends SavedData {
    private static final String DATA_NAME = "economia_central_bank";

    private double treasuryBalance = 0.0;
    private final Map<UUID, Double> activeLoans = new HashMap<>();

    public CentralBankManager() {}

    public static CentralBankManager load(CompoundTag nbt) {
        CentralBankManager data = new CentralBankManager();
        data.treasuryBalance = nbt.getDouble("treasuryBalance");

        if (nbt.contains("loansTag")) {
            CompoundTag loansTag = nbt.getCompound("loansTag");
            for (String key : loansTag.getAllKeys()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    double amount = loansTag.getDouble(key);
                    data.activeLoans.put(uuid, amount);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putDouble("treasuryBalance", treasuryBalance);

        CompoundTag loansTag = new CompoundTag();
        for (Map.Entry<UUID, Double> entry : activeLoans.entrySet()) {
            loansTag.putDouble(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("loansTag", loansTag);
        return nbt;
    }

    public static CentralBankManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(CentralBankManager::load, CentralBankManager::new, DATA_NAME);
    }

    public double getTreasuryBalance() {
        return treasuryBalance;
    }

    public void addTreasury(double amount) {
        treasuryBalance += amount;
        setDirty();
    }

    public void removeTreasury(double amount) {
        treasuryBalance = Math.max(0, treasuryBalance - amount);
        setDirty();
    }

    public double getLoan(UUID uuid) {
        return activeLoans.getOrDefault(uuid, 0.0);
    }

    public void setLoan(UUID uuid, double amount) {
        if (amount <= 0) {
            activeLoans.remove(uuid);
        } else {
            activeLoans.put(uuid, amount);
        }
        setDirty();
    }

    public Map<UUID, Double> getActiveLoans() {
        return activeLoans;
    }
}
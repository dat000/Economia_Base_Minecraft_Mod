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
    private final Map<UUID, Long> loanDueDates = new HashMap<>(); // NUEVO: Guarda el timestamp de expiración (milisegundos)

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

        // Cargar fechas de vencimiento
        if (nbt.contains("dueDatesTag")) {
            CompoundTag dueDatesTag = nbt.getCompound("dueDatesTag");
            for (String key : dueDatesTag.getAllKeys()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    long dueDate = dueDatesTag.getLong(key);
                    data.loanDueDates.put(uuid, dueDate);
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

        // Guardar fechas de vencimiento
        CompoundTag dueDatesTag = new CompoundTag();
        for (Map.Entry<UUID, Long> entry : loanDueDates.entrySet()) {
            dueDatesTag.putLong(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("dueDatesTag", dueDatesTag);

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

    public long getLoanDueDate(UUID uuid) {
        return loanDueDates.getOrDefault(uuid, 0L);
    }

    public void setLoan(UUID uuid, double amount, long dueDateMillis) {
        if (amount <= 0) {
            activeLoans.remove(uuid);
            loanDueDates.remove(uuid);
        } else {
            activeLoans.put(uuid, amount);
            loanDueDates.put(uuid, dueDateMillis);
        }
        setDirty();
    }

    public Map<UUID, Double> getActiveLoans() {
        return activeLoans;
    }
}
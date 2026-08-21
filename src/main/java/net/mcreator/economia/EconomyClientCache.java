package net.mcreator.economia.client;

import net.mcreator.economia.EconomyConfig;

public class EconomyClientCache {
    private static String currencyName = "";
    private static String currencyPrefix = "";
    private static boolean synced = false;

    public static void set(String name, String prefix) {
        currencyName = name;
        currencyPrefix = prefix;
        synced = true;
    }

    public static String getCurrencyName() {
        if (!synced) return EconomyConfig.CURRENCY_NAME.get();
        return currencyName;
    }

    public static String getCurrencyPrefix() {
        if (!synced) return EconomyConfig.CURRENCY_PREFIX.get();
        return currencyPrefix;
    }
}
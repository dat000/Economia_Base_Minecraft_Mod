package net.mcreator.economia;

import net.minecraftforge.common.ForgeConfigSpec;

public class EconomyConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> CURRENCY_NAME;
    public static final ForgeConfigSpec.ConfigValue<String> CURRENCY_PREFIX;
    public static final ForgeConfigSpec.ConfigValue<String> CURRENCY_SUFFIX;
    public static final ForgeConfigSpec.DoubleValue STARTING_BALANCE;

    // --- Configuración de Impuestos ---
    public static final ForgeConfigSpec.DoubleValue TRANSFER_TAX_RATE;

    // --- NUEVO: Configuración de Interés del Banco ---
    public static final ForgeConfigSpec.DoubleValue LOAN_INTEREST_RATE;
    public static final ForgeConfigSpec.DoubleValue MAX_LOAN_AMOUNT;

    static {
        BUILDER.push("General Settings");

        CURRENCY_NAME = BUILDER
                .comment("Name of the currency used on the server")
                .define("currencyName", "Money");

        CURRENCY_PREFIX = BUILDER
                .comment("Prefix for the currency (e.g., '$'). Leave empty for none.")
                .define("currencyPrefix", "$");

        CURRENCY_SUFFIX = BUILDER
                .comment("Suffix for the currency (e.g., ' Coins'). Leave empty for none.")
                .define("currencySuffix", "");

        STARTING_BALANCE = BUILDER
                .comment("Initial balance upon first login")
                .defineInRange("startingBalance", 100.0, 0.0, Double.MAX_VALUE);

        // Definición del impuesto por transferencia
        TRANSFER_TAX_RATE = BUILDER
                .comment("Percentage of tax applied to player-to-player transfers (e.g., 0.05 = 5%, 0.10 = 10%, 0.0 = no tax)")
                .defineInRange("transferTaxRate", 0.05, 0.0, 1.0);

        BUILDER.pop();

        // --- NUEVO: Seccion del Banco Central ---
        BUILDER.push("Central Bank Settings");

        LOAN_INTEREST_RATE = BUILDER
                .comment("Interest rate applied to central bank loans (e.g., 0.10 = 10%, 0.0 = 0%)")
                .defineInRange("loanInterestRate", 0.10, 0.0, 5.0); // 10% por defecto

        MAX_LOAN_AMOUNT = BUILDER
                .comment("Maximum amount of money a player can request in a single loan")
                .defineInRange("maxLoanAmount", 5000.0, 1.0, Double.MAX_VALUE);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // Metodo global para formatear el dinero en todo el mod
    public static String formatMoney(double amount) {
        String prefix;
        // Si estamos en el cliente, usamos el prefijo sincronizado por el servidor
        if (net.minecraft.client.Minecraft.getInstance().level != null) {
            prefix = net.mcreator.economia.client.EconomyClientCache.getCurrencyPrefix();
        } else {
            // Si estamos en el servidor, leemos del archivo de configuración del server
            prefix = CURRENCY_PREFIX.get();
        }
        return prefix + new java.text.DecimalFormat("##.##").format(amount);
    }
}
package net.mcreator.economia;

import net.minecraftforge.common.ForgeConfigSpec;

public class EconomyConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> CURRENCY_NAME;
    public static final ForgeConfigSpec.DoubleValue STARTING_BALANCE;

    static {
        BUILDER.push("General Settings");

        CURRENCY_NAME = BUILDER
                .comment("Name of the currency used on the server")
                .define("currencyName", "Money");

        STARTING_BALANCE = BUILDER
                .comment("Initial balance upon first login")
                .defineInRange("startingBalance", 100.0, 0.0, Double.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}

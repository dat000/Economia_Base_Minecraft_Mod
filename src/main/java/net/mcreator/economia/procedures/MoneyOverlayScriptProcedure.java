package net.mcreator.economia.procedures;

import net.mcreator.economia.EconomyConfig;
import net.minecraft.world.entity.Entity;

import net.mcreator.economia.network.EconomiaModVariables;

public class MoneyOverlayScriptProcedure {
	String currencyName_ = EconomyConfig.CURRENCY_NAME.get();
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		if (entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money >= 1000000) {
			return net.mcreator.economia.EconomyConfig.CURRENCY_NAME.get() + ": \u00A7a$" + new java.text.DecimalFormat("##.##").format(entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money / 1000000) + "\u00A7aM";
		} else if (entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money >= 10000) {
			return net.mcreator.economia.EconomyConfig.CURRENCY_NAME.get() + ": \u00A7a$" + new java.text.DecimalFormat("##.##").format(entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money / 1000) + "\u00A7aK";
		} else if (entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money >= 1000) {
			return net.mcreator.economia.EconomyConfig.CURRENCY_NAME.get() + ": \u00A7a$" + new java.text.DecimalFormat("##.##").format(entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money / 1000) + "\u00A7aK";
		} else if (entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money <= 0) {
			return net.mcreator.economia.EconomyConfig.CURRENCY_NAME.get() + ": \u00A7c" + new java.text.DecimalFormat("##.##").format(entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money);
		}
		return net.mcreator.economia.EconomyConfig.CURRENCY_NAME.get() + ": \u00A7a$" + new java.text.DecimalFormat("##.##").format(entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money);
	}
}
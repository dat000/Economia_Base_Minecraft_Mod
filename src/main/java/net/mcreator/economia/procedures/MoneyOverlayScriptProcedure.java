package net.mcreator.economia.procedures;

import net.mcreator.economia.EconomyConfig;
import net.minecraft.world.entity.Entity;
import net.mcreator.economia.network.EconomiaModVariables;

public class MoneyOverlayScriptProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";

		double money = entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money;
		String currency = net.mcreator.economia.client.EconomyClientCache.getCurrencyName();
		String prefix = net.mcreator.economia.client.EconomyClientCache.getCurrencyPrefix();

		if (money >= 1000000) {
			return currency + ": §a" + prefix + new java.text.DecimalFormat("##.##").format(money / 1000000) + "M";
		} else if (money >= 1000) {
			return currency + ": §a" + prefix + new java.text.DecimalFormat("##.##").format(money / 1000) + "K";
		} else if (money <= 0) {
			return currency + ": §c" + EconomyConfig.formatMoney(money);
		}
		return currency + ": §a" + EconomyConfig.formatMoney(money);
	}
}
package net.mcreator.economia.procedures;

import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.economia.TransactionManager;
import net.mcreator.economia.network.EconomiaModVariables;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class MoneyTestGETProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;

		Entity target = commandParameterEntity(arguments, "name");
		if (target == null)
			return;

		double amount = DoubleArgumentType.getDouble(arguments, "moneyGet");

		target.getCapability(EconomiaModVariables.PLAYER_VARIABLES).ifPresent(capability -> {
			capability.money += amount;
			capability.markSyncDirty();

			// NUEVO: Guardamos saldo Y NOMBRE
			if (world instanceof ServerLevel serverLevel) {
				TransactionManager.get(serverLevel).setBalance(target.getUUID(), target.getDisplayName().getString(), capability.money);
			}
		});

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal("§aYou have successfully GET §e" + net.mcreator.economia.EconomyConfig.formatMoney(amount)), false);

		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
			}
		}
	}

	private static Entity commandParameterEntity(CommandContext<CommandSourceStack> arguments, String parameter) {
		try {
			return EntityArgument.getEntity(arguments, parameter);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
}
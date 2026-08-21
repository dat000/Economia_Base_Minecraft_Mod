package net.mcreator.economia.procedures;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
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

public class TransferSystemProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;

		// Validar que no se transfiera 0 o menos
		double amount = DoubleArgumentType.getDouble(arguments, "moneyWantTransfer");
		if (amount <= 0) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("§cThe transfer amount must be greater than 0."), false);
			return;
		}

		Entity targetEntity = commandParameterEntity(arguments, "name");
		if (targetEntity == null)
			return;

		if (entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money >= amount) {
			targetEntity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).ifPresent(capability -> {
				capability.money += amount;
				capability.markSyncDirty();
			});

			entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).ifPresent(capability -> {
				capability.money -= amount;
				capability.markSyncDirty();

				// Sistema de registro de Historial con hora
				if (world instanceof ServerLevel serverLevel) {
					java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm, dd-MM");
					String timestamp = "§7[" + java.time.LocalDateTime.now().format(formatter) + "] ";

					TransactionManager.get(serverLevel).addTransaction(entity.getUUID(),
							timestamp + "§c- " + net.mcreator.economia.EconomyConfig.formatMoney(amount) + " §7to §f" + targetEntity.getDisplayName().getString());

					TransactionManager.get(serverLevel).addTransaction(targetEntity.getUUID(),
							timestamp + "§a+ " + net.mcreator.economia.EconomyConfig.formatMoney(amount) + " §7from §f" + entity.getDisplayName().getString());
				}
			});

			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("§aYou have successfully sent §e" + net.mcreator.economia.EconomyConfig.formatMoney(amount) + "§a to §f" + targetEntity.getDisplayName().getString()), false);

			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1, false);
				}
			}

			if (targetEntity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("§aYou have received §e" + net.mcreator.economia.EconomyConfig.formatMoney(amount) + "§a from §f" + entity.getDisplayName().getString()), false);
		} else {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), net.minecraft.sounds.SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, net.minecraft.sounds.SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("§eYou don't have enough money to complete this transfer."), false);
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
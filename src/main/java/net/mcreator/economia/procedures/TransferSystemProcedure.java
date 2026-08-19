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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransferSystemProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;
		if (entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money >= DoubleArgumentType.getDouble(arguments, "moneyWantTransfer")) {
			{
				(commandParameterEntity(arguments, "name")).getCapability(EconomiaModVariables.PLAYER_VARIABLES).ifPresent(capability -> {
					capability.money = (commandParameterEntity(arguments, "name")).getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money
							+ DoubleArgumentType.getDouble(arguments, "moneyWantTransfer");
					capability.markSyncDirty();
				});
			}
			{
				entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).ifPresent(capability -> {
					capability.money = entity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).orElseGet(EconomiaModVariables.PlayerVariables::new).money - DoubleArgumentType.getDouble(arguments, "moneyWantTransfer");
					capability.markSyncDirty();

					// Sistema de registro de Historail con hora
					double transferAmount = DoubleArgumentType.getDouble(arguments, "moneyWantTransfer");
					Entity targetEntity = commandParameterEntity(arguments, "name");

					// Asegurarnos de que estamos del lado del servidor para guardar
					if (targetEntity != null && world instanceof ServerLevel serverLevel) {
						java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm, dd-MM");
						String timestamp = "§7[" + java.time.LocalDateTime.now().format(formatter) + "] ";

						// Usamos .get(serverLevel) para obtener la clase que está conectada al disco duro
						TransactionManager.get(serverLevel).addTransaction(entity.getUUID(),
								timestamp + "§c- $" + new java.text.DecimalFormat("##.##").format(transferAmount) + " §7to §f" + targetEntity.getDisplayName().getString());

						TransactionManager.get(serverLevel).addTransaction(targetEntity.getUUID(),
								timestamp + "§a+ $" + new java.text.DecimalFormat("##.##").format(transferAmount) + " §7from §f" + entity.getDisplayName().getString());
					}


				});
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal(("§aYou have successfully sent " + "§e" + new java.text.DecimalFormat("##.##").format(DoubleArgumentType.getDouble(arguments, "moneyWantTransfer")) + "§a to "
						+ ("§f" + (commandParameterEntity(arguments, "name")).getDisplayName().getString()))), false);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if ((commandParameterEntity(arguments, "name")) instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal(
						("§aYou have received " + "§e" + new java.text.DecimalFormat("##.##").format(DoubleArgumentType.getDouble(arguments, "moneyWantTransfer")) + "§a from " + ("§f" + entity.getDisplayName().getString()))),
						false);
		} else {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), net.minecraft.sounds.SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal(("§eYou don't have enough money to complete this transfer." + "§e")), false);
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
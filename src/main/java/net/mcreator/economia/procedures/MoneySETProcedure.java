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

public class MoneySETProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;

		Entity targetEntity = commandParameterEntity(arguments, "name");
		if (targetEntity == null)
			return;

		double setAmount = DoubleArgumentType.getDouble(arguments, "moneySet");

		targetEntity.getCapability(EconomiaModVariables.PLAYER_VARIABLES).ifPresent(capability -> {
			capability.money = setAmount;
			capability.markSyncDirty();

			// NUEVO: Guardamos saldo Y NOMBRE
			if (world instanceof ServerLevel serverLevel) {
				TransactionManager.get(serverLevel).setBalance(targetEntity.getUUID(), targetEntity.getDisplayName().getString(), capability.money);
			}
		});

		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal("§aYou have successfully SET §e" + net.mcreator.economia.EconomyConfig.formatMoney(setAmount)), false);

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
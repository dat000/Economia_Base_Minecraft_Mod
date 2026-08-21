package net.mcreator.economia.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;

import net.mcreator.economia.procedures.MoneyTestGETProcedure;
import net.mcreator.economia.procedures.MoneySETProcedure;
import net.mcreator.economia.procedures.MoneyRmvProcedure;

import com.mojang.brigadier.arguments.DoubleArgumentType;

@Mod.EventBusSubscriber
public class AdminEcoSystemCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("$admin").requires(s -> s.hasPermission(3))
				.then(Commands.literal("get").then(Commands.argument("name", EntityArgument.player()).then(Commands.argument("moneyGet", DoubleArgumentType.doubleArg(0.01)).executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
					double x = arguments.getSource().getPosition().x();
					double y = arguments.getSource().getPosition().y();
					double z = arguments.getSource().getPosition().z();
					Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel _servLevel)
						entity = FakePlayerFactory.getMinecraft(_servLevel);
					Direction direction = Direction.DOWN;
					if (entity != null)
						direction = entity.getDirection();

					MoneyTestGETProcedure.execute(world, x, y, z, arguments, entity);
					return 0;
				})))).then(Commands.literal("rmv").then(Commands.argument("name", EntityArgument.player()).then(Commands.argument("moneyRmv", DoubleArgumentType.doubleArg(0.01)).executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
					double x = arguments.getSource().getPosition().x();
					double y = arguments.getSource().getPosition().y();
					double z = arguments.getSource().getPosition().z();
					Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel _servLevel)
						entity = FakePlayerFactory.getMinecraft(_servLevel);
					Direction direction = Direction.DOWN;
					if (entity != null)
						direction = entity.getDirection();

					MoneyRmvProcedure.execute(world, x, y, z, arguments, entity);
					return 0;
				})))).then(Commands.literal("set").then(Commands.argument("name", EntityArgument.player()).then(Commands.argument("moneySet", DoubleArgumentType.doubleArg(0)).executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
					double x = arguments.getSource().getPosition().x();
					double y = arguments.getSource().getPosition().y();
					double z = arguments.getSource().getPosition().z();
					Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel _servLevel)
						entity = FakePlayerFactory.getMinecraft(_servLevel);
					Direction direction = Direction.DOWN;
					if (entity != null)
						direction = entity.getDirection();

					MoneySETProcedure.execute(world, x, y, z, arguments, entity);
					return 0;
				})))).then(Commands.literal("reset")
				.then(Commands.argument("name", EntityArgument.player())
						.executes(arguments -> {
							Entity targetEntity = EntityArgument.getEntity(arguments, "name");
							Level level = arguments.getSource().getLevel();
							if (targetEntity != null && targetEntity.level() instanceof ServerLevel _servLevel) {
								ServerLevel serverLevel = (ServerLevel) _servLevel;
								// Accedemos a los datos y limpiamos la lista de ese jugador
								net.mcreator.economia.TransactionManager.get(serverLevel).getHistory(targetEntity.getUUID()).clear();

								// Marcamos como "sucio" para que se guarde el cambio en el archivo .dat
								net.mcreator.economia.TransactionManager.get(serverLevel).setDirty();

								arguments.getSource().sendSuccess(() ->
										net.minecraft.network.chat.Component.literal("§f" + targetEntity.getDisplayName().getString() + "§a history reset successfully"), true);
							}
							return 0;
				}))).then(Commands.literal("payall")
				.then(Commands.argument("amount", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0.01))
						.executes(arguments -> {
							net.mcreator.economia.procedures.AdminPayAllProcedure.execute(
									arguments.getSource(),
									com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(arguments, "amount")
							);
							return 1;
				}))));
	}

}
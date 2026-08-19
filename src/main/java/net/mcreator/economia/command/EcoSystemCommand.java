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

import net.mcreator.economia.procedures.TransferSystemProcedure;
import net.mcreator.economia.procedures.MoneyHistoryProcedure;

import com.mojang.brigadier.arguments.DoubleArgumentType;

@Mod.EventBusSubscriber
public class EcoSystemCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("$")

				// --- SUBCOMANDO TRANSFER ---
				.then(Commands.literal("transfer").then(Commands.argument("name", EntityArgument.player()).then(Commands.argument("moneyWantTransfer", DoubleArgumentType.doubleArg(0)).executes(arguments -> {
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

					TransferSystemProcedure.execute(world, x, y, z, arguments, entity);
					return 0;
				}))))

				// --- SUBCOMANDO HISTORY CON PÁGINAS ---
				.then(Commands.literal("history")
						.executes(arguments -> {
							Entity entity = arguments.getSource().getEntity();
							if (entity != null) {
								MoneyHistoryProcedure.execute(entity, 1); // Página por defecto: 1
							}
							return 0;
						})
						.then(Commands.argument("page", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
								.executes(arguments -> {
									Entity entity = arguments.getSource().getEntity();
									int page = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(arguments, "page");
									if (entity != null) {
										MoneyHistoryProcedure.execute(entity, page);
									}
									return 0;
								})
						)
				)
		);
	}
}
package net.mcreator.economia.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import net.mcreator.economia.TransactionManager;
import net.mcreator.economia.procedures.MoneyTestGETProcedure;
import net.mcreator.economia.procedures.MoneySETProcedure;
import net.mcreator.economia.procedures.MoneyRmvProcedure;

import com.mojang.brigadier.arguments.DoubleArgumentType;

@Mod.EventBusSubscriber
public class AdminEcoSystemCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(
				Commands.literal("$admin").requires(s -> s.hasPermission(3))

						// 1. GET
						.then(Commands.literal("get")
								.then(Commands.argument("name", EntityArgument.player())
										.then(Commands.argument("moneyGet", DoubleArgumentType.doubleArg(0.01))
												.executes(arguments -> {
													Level world = arguments.getSource().getUnsidedLevel();
													Entity entity = arguments.getSource().getEntity();
													if (entity == null && world instanceof ServerLevel _servLevel)
														entity = FakePlayerFactory.getMinecraft(_servLevel);
													MoneyTestGETProcedure.execute(world, arguments.getSource().getPosition().x(), arguments.getSource().getPosition().y(), arguments.getSource().getPosition().z(), arguments, entity);
													return 0;
												})
										)
								)
						)

						// 2. REMOVE (rmv)
						.then(Commands.literal("remove")
								.then(Commands.argument("name", EntityArgument.player())
										.then(Commands.argument("moneyRmv", DoubleArgumentType.doubleArg(0.01))
												.executes(arguments -> {
													Level world = arguments.getSource().getUnsidedLevel();
													Entity entity = arguments.getSource().getEntity();
													if (entity == null && world instanceof ServerLevel _servLevel)
														entity = FakePlayerFactory.getMinecraft(_servLevel);
													MoneyRmvProcedure.execute(world, arguments.getSource().getPosition().x(), arguments.getSource().getPosition().y(), arguments.getSource().getPosition().z(), arguments, entity);
													return 0;
												})
										)
								)
						)

						// 3. SET
						.then(Commands.literal("set")
								.then(Commands.argument("name", EntityArgument.player())
										.then(Commands.argument("moneySet", DoubleArgumentType.doubleArg(0))
												.executes(arguments -> {
													Level world = arguments.getSource().getUnsidedLevel();
													Entity entity = arguments.getSource().getEntity();
													if (entity == null && world instanceof ServerLevel _servLevel)
														entity = FakePlayerFactory.getMinecraft(_servLevel);
													MoneySETProcedure.execute(world, arguments.getSource().getPosition().x(), arguments.getSource().getPosition().y(), arguments.getSource().getPosition().z(), arguments, entity);
													return 0;
												})
										)
								)
						)

						// 4. RESET
						.then(Commands.literal("reset")
								.then(Commands.argument("name", EntityArgument.player())
										.executes(arguments -> {
											Entity targetEntity = EntityArgument.getEntity(arguments, "name");
											Level level = arguments.getSource().getLevel();
											if (targetEntity != null && targetEntity.level() instanceof ServerLevel _servLevel) {
												ServerLevel serverLevel = (ServerLevel) _servLevel;
												TransactionManager.get(serverLevel).getHistory(targetEntity.getUUID()).clear();
												TransactionManager.get(serverLevel).setDirty();

												arguments.getSource().sendSuccess(() ->
														Component.literal("§f" + targetEntity.getDisplayName().getString() + "§a history reset successfully"), true);
											}
											return 0;
										})
								)
						)

						// 5. PAYALL
						.then(Commands.literal("payall")
								.then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.01))
										.executes(arguments -> {
											net.mcreator.economia.procedures.AdminPayAllProcedure.execute(
													arguments.getSource(),
													DoubleArgumentType.getDouble(arguments, "amount")
											);
											return 1;
										})
								)
						)

						// 6. BOUNTY REMOVE
						.then(Commands.literal("bounty")
								.then(Commands.literal("remove")
										.then(Commands.argument("target", EntityArgument.player())
												.executes(arguments -> {
													ServerPlayer target = EntityArgument.getPlayer(arguments, "target");
													ServerLevel level = arguments.getSource().getLevel();
													TransactionManager manager = TransactionManager.get(level);

													if (manager.getBounty(target.getUUID()) > 0) {
														manager.removeBounty(target.getUUID());
														arguments.getSource().sendSuccess(() ->
																Component.literal("§aSuccessfully removed the bounty from §f" + target.getScoreboardName()), true);
													} else {
														arguments.getSource().sendSuccess(() ->
																Component.literal("§c" + target.getScoreboardName() + " does not have an active bounty."), false);
													}
													return 1;
												})
										)
								)
						)

						// 7. HISTORY|
						.then(Commands.literal("history")
								.then(Commands.argument("target", EntityArgument.player())
										.executes(arguments -> {
											ServerPlayer targetPlayer = EntityArgument.getPlayer(arguments, "target");
											CommandSourceStack source = arguments.getSource();
											ServerLevel level = source.getLevel();

											TransactionManager manager = TransactionManager.get(level);
											java.util.List<String> history = manager.getHistory(targetPlayer.getUUID());

											if (history.isEmpty()) {
												source.sendSuccess(() -> Component.literal("§c" + targetPlayer.getScoreboardName() + " has no transaction history."), false);
												return 1;
											}

											source.sendSuccess(() -> Component.literal("§6=== History for " + targetPlayer.getScoreboardName() + " ==="), false);

											int end = history.size() - 1;
											int start = Math.max(0, history.size() - 10);
											for (int i = end; i >= start; i--) {
												String entry = history.get(i);
												source.sendSuccess(() -> Component.literal("§7- " + entry), false);
											}

											return 1;
										})
								)
						)
		);
	}
}
package net.mcreator.economia.command;

import net.mcreator.economia.EconomyAPI;
import net.mcreator.economia.FrozenAccountsManager;
import net.mcreator.economia.TransactionManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.commands.CommandSourceStack;

import net.mcreator.economia.procedures.TransferSystemProcedure;
import net.mcreator.economia.procedures.MoneyHistoryProcedure;
import net.mcreator.economia.procedures.BaltopProcedure;

import com.mojang.brigadier.arguments.DoubleArgumentType;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EcoSystemCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("$")

				// --- SUBCOMANDO TRANSFER ---
				.then(Commands.literal("transfer").then(Commands.argument("name", EntityArgument.player()).then(Commands.argument("moneyWantTransfer", DoubleArgumentType.doubleArg(0)).executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
					Entity entity = arguments.getSource().getEntity();

					// --- VALIDACIÓN DE CUENTA CONGELADA ---
					if (entity instanceof ServerPlayer serverPlayer) {
						FrozenAccountsManager manager = FrozenAccountsManager.get(serverPlayer.serverLevel());
						if (manager.isFrozen(serverPlayer.getUUID())) {
							serverPlayer.sendSystemMessage(Component.literal("§cYour account is frozen. You cannot perform transfers."));
							return 0; // Detiene la ejecución por completo
						}
					}
					// -------------------------------------

					double x = arguments.getSource().getPosition().x();
					double y = arguments.getSource().getPosition().y();
					double z = arguments.getSource().getPosition().z();
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

				// --- SUBCOMANDO BALTOP ---
				.then(Commands.literal("baltop")
						.executes(arguments -> {
							Entity entity = arguments.getSource().getEntity();
							if (entity != null) {
								BaltopProcedure.execute(arguments, entity);
							}
							return 0;
						})
				)

				// --- SUBCOMANDO BOUNTIES ---
				// --- SUBCOMANDO BOUNTIES REESTRUCTURADO ---
				.then(Commands.literal("bounty")

						// 1. /$ bounty list
						.then(Commands.literal("list")
								.executes(context -> {
									CommandSourceStack source = context.getSource();
									TransactionManager manager = TransactionManager.get(source.getLevel());
									Map<UUID, Double> bounties = manager.getAllBounties();

									Map<UUID, Double> activeBounties = new HashMap<>();
									for (Map.Entry<UUID, Double> entry : bounties.entrySet()) {
										if (entry.getValue() > 0) {
											activeBounties.put(entry.getKey(), entry.getValue());
										}
									}

									if (activeBounties.isEmpty()) {
										source.sendSuccess(() -> Component.literal("§cThere are no active bounties right now."), false);
										return 1;
									}

									source.sendSuccess(() -> Component.literal("§6=== ACTIVE BOUNTIES ==="), false);

									for (Map.Entry<UUID, Double> entry : activeBounties.entrySet()) {
										String targetName = manager.getPlayerName(entry.getKey());
										double amount = entry.getValue();
										source.sendSuccess(() -> Component.literal("§7- §c" + targetName + "§7: §e$" + amount), false);
									}
									return 1;
								})
						)

						// 2. /$ bounty set <target> <amount>
						.then(Commands.literal("set")
								.then(Commands.argument("target", EntityArgument.player())
										.then(Commands.argument("amount", DoubleArgumentType.doubleArg(1.0))
												.executes(context -> {
													ServerPlayer source = context.getSource().getPlayerOrException();
													ServerPlayer target = EntityArgument.getPlayer(context, "target");
													double amount = DoubleArgumentType.getDouble(context, "amount");

													if (source.getUUID().equals(target.getUUID())) {
														source.sendSystemMessage(Component.literal("§cYou cannot place a bounty on yourself."));
														return 0;
													}

													double currentBalance = EconomyAPI.getBalance(source);

													if (currentBalance >= amount) {
														// Quitamos el dinero silenciosamente
														EconomyAPI.removeMoney(source, amount, "");

														// Escribimos el historial a mano
														TransactionManager manager = TransactionManager.get(source.serverLevel());
														String timeStamp = new java.text.SimpleDateFormat("HH:mm, dd-MM").format(new java.util.Date());
														String historyText = "§7[" + timeStamp + "] §c- $" + amount + " §f(Bounty on " + target.getScoreboardName() + ")";
														manager.addTransaction(source.getUUID(), historyText);

														// Guardamos el bounty
														manager.addBounty(target.getUUID(), amount);
														double totalBounty = manager.getBounty(target.getUUID());

														// Anuncio global
														source.server.getPlayerList().broadcastSystemMessage(
																Component.literal("§c[BOUNTY] §f" + source.getScoreboardName() +
																		" has placed a §e$" + amount + " §fbounty on §c" + target.getScoreboardName() +
																		" §f(Total: §e$" + totalBounty + "§f)"), false
														);
													} else {
														source.sendSystemMessage(Component.literal("§cYou don't have enough money."));
													}
													return 1;
												})
										)
								)
						)

						// 3. /$ bounty remove <target> (Solo Administradores)
						.then(Commands.literal("remove")
								// El nivel 2 de permisos requiere ser OP/Admin en el servidor
								.requires(s -> s.hasPermission(2))
								.then(Commands.argument("target", EntityArgument.player())
										.executes(context -> {
											CommandSourceStack source = context.getSource();
											ServerPlayer target = EntityArgument.getPlayer(context, "target");

											TransactionManager manager = TransactionManager.get(source.getLevel());
											double currentBounty = manager.getBounty(target.getUUID());

											if (currentBounty > 0) {
												manager.removeBounty(target.getUUID());
												source.sendSuccess(() -> Component.literal("§aSuccessfully removed the bounty from " + target.getScoreboardName() + "."), true);
											} else {
												source.sendSuccess(() -> Component.literal("§c" + target.getScoreboardName() + " does not have an active bounty."), false);
											}
											return 1;
										})
								)
						)
				)
		);
	}
}
package net.mcreator.economia.command;

import net.mcreator.economia.*;
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
	// Evento para verificar préstamos vencidos al iniciar sesión
	@SubscribeEvent
	public static void onPlayerLogin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			ServerLevel level = player.serverLevel();
			CentralBankManager bankManager = CentralBankManager.get(level);
			double debt = bankManager.getLoan(player.getUUID());

			if (debt > 0) {
				long dueDate = bankManager.getLoanDueDate(player.getUUID());
				if (System.currentTimeMillis() > dueDate) {
					FrozenAccountsManager frozenManager = FrozenAccountsManager.get(level);
					if (!frozenManager.isFrozen(player.getUUID())) {
						frozenManager.freeze(player.getUUID());
						player.sendSystemMessage(Component.literal("§c[Central Bank] Your loan has expired! Your account has been FROZEN due to overdue debt. Use /$ loan pay to settle it."));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("$")

				// --- SUBCOMANDO TRANSFER ---
				.then(Commands.literal("transfer").then(Commands.argument("name", EntityArgument.player()).then(Commands.argument("moneyWantTransfer", DoubleArgumentType.doubleArg(0)).executes(arguments -> {
					ServerLevel level = arguments.getSource().getLevel();
					ServerPlayer sender;
					try {
						sender = arguments.getSource().getPlayerOrException();
					} catch (Exception e) {
						return 0;
					}

					// --- VALIDACIÓN DE CUENTA CONGELADA ---
					FrozenAccountsManager manager = FrozenAccountsManager.get(level);
					if (manager.isFrozen(sender.getUUID())) {
						sender.sendSystemMessage(Component.literal("§cYour account is frozen. You cannot perform transfers."));
						return 0;
					}
					// -------------------------------------

					ServerPlayer receiver = EntityArgument.getPlayer(arguments, "name");
					double amount = DoubleArgumentType.getDouble(arguments, "moneyWantTransfer");

					if (sender.getUUID().equals(receiver.getUUID())) {
						sender.sendSystemMessage(Component.literal("§cYou cannot transfer money to yourself."));
						return 0;
					}

					// Ejecutamos la transferencia centralizada con impuestos
					boolean success = EconomyAPI.transferMoney(
							level,
							sender.getUUID(),
							sender.getScoreboardName(),
							receiver.getUUID(),
							receiver.getScoreboardName(),
							amount
					);

					if (success) {
						double taxRate = EconomyConfig.TRANSFER_TAX_RATE.get(); // Obtiene la tasa (ej. 0.05)
						double taxAmount = amount * taxRate;                    // Calcula el dinero real del impuesto (ej. 5.0 si envías 100)
						double net = amount - taxAmount;                        // Calcula el neto real que llega

						sender.sendSystemMessage(Component.literal("§aYou have successfully sent " + EconomyConfig.formatMoney(net) + " to " + receiver.getScoreboardName() + " (§6Tax: " + EconomyConfig.formatMoney(taxAmount) + "§a)"));
						receiver.sendSystemMessage(Component.literal("§aYou have received " + EconomyConfig.formatMoney(net) + " from " + sender.getScoreboardName()));
					} else {
						sender.sendSystemMessage(Component.literal("§cTransfer failed. You may not have enough money."));
					}

					return 1;
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

				// --- SUBCOMANDO LOAN (SISTEMA DE PRÉSTAMOS) ---
				.then(Commands.literal("loan")

						// 1. /$ loan info
						.then(Commands.literal("info")
								.executes(context -> {
									ServerPlayer player = context.getSource().getPlayerOrException();
									ServerLevel level = player.serverLevel();

									CentralBankManager bankManager = CentralBankManager.get(level);
									double debt = bankManager.getLoan(player.getUUID());
									double treasury = bankManager.getTreasuryBalance();
									long dueDate = bankManager.getLoanDueDate(player.getUUID());

									player.sendSystemMessage(Component.literal("§6=== CENTRAL BANK ==="));
									player.sendSystemMessage(Component.literal("§7Your current debt: §c" + EconomyConfig.formatMoney(debt)));

									if (debt > 0) {
										long now = System.currentTimeMillis();
										long diff = dueDate - now;

										if (diff <= 0) {
											player.sendSystemMessage(Component.literal("§7Time remaining: §c§lEXPIRED (Overdue!)"));
										} else {
											long days = diff / (1000L * 60L * 60L * 24L);
											long hours = (diff / (1000L * 60L * 60L)) % 24L;
											long minutes = (diff / (1000L * 60L)) % 60L;

											String timeRemainingText;
											if (days > 0) {
												timeRemainingText = days + " days, " + hours + " hours";
											} else if (hours > 0) {
												timeRemainingText = hours + " hours, " + minutes + " minutes";
											} else {
												timeRemainingText = minutes + " minutes";
											}

											player.sendSystemMessage(Component.literal("§7Time remaining: §e" + timeRemainingText));
										}
									}

									player.sendSystemMessage(Component.literal("§7Bank Treasury: §a" + EconomyConfig.formatMoney(treasury)));
									return 1;
								})
						)

						// 2. /$ loan request <amount>
						.then(Commands.literal("request")
								.then(Commands.argument("amount", DoubleArgumentType.doubleArg(1.0))
										.executes(context -> {
											ServerPlayer player = context.getSource().getPlayerOrException();
											ServerLevel level = player.serverLevel();
											double amount = DoubleArgumentType.getDouble(context, "amount");

											CentralBankManager bankManager = CentralBankManager.get(level);

											if (bankManager.getTreasuryBalance() < amount) {
												player.sendSystemMessage(Component.literal("§cThe Central Bank does not have enough funds in its treasury to grant this loan."));
												return 0;
											}

											bankManager.removeTreasury(amount);

											double currentDebt = bankManager.getLoan(player.getUUID());
											long threeDaysMillis = System.currentTimeMillis() + (3L * 24L * 60L * 60L * 1000L);
											// long threeDaysMillis = System.currentTimeMillis() + (30L * 1000L); // !!!! TESTING
											bankManager.setLoan(player.getUUID(), currentDebt + amount, threeDaysMillis);


											// Modificar saldo a través de la API
											double currentBalance = EconomyAPI.getBalance(level, player.getUUID());
											EconomyAPI.setMoney(level, player.getUUID(), player.getScoreboardName(), currentBalance + amount);

											// Registrar en el historial de transacciones con el formato estandarizado
											String timeStamp = new java.text.SimpleDateFormat("HH:mm, dd-MM").format(new java.util.Date());
											TransactionManager managerTrans = TransactionManager.get(level);
											managerTrans.addTransaction(player.getUUID(), "§7[" + timeStamp + "] §a+$" + amount + " §f(Central Bank Loan)");

											player.sendSystemMessage(Component.literal("§aYou have successfully requested a loan of " + EconomyConfig.formatMoney(amount) + "."));
											return 1;
										})
								)
						)

						// 3. /$ loan pay <amount>
						.then(Commands.literal("pay")
								.then(Commands.argument("amount", DoubleArgumentType.doubleArg(1.0))
										.executes(context -> {
											ServerPlayer player = context.getSource().getPlayerOrException();
											ServerLevel level = player.serverLevel();
											double amount = DoubleArgumentType.getDouble(context, "amount");

											CentralBankManager bankManager = CentralBankManager.get(level);
											double currentDebt = bankManager.getLoan(player.getUUID());

											if (currentDebt <= 0) {
												player.sendSystemMessage(Component.literal("§cYou don't have any active debts with the Central Bank."));
												return 0;
											}

											if (amount > currentDebt) {
												amount = currentDebt;
											}

											// Verificar si el jugador tiene suficiente dinero para pagar
											if (!EconomyAPI.hasEnough(level, player.getUUID(), amount)) {
												player.sendSystemMessage(Component.literal("§cYou don't have enough money to pay this amount."));
												return 0;
											}

											// Descontar saldo directamente (permitiendo el pago incluso si está congelado)
											double currentBalance = EconomyAPI.getBalance(level, player.getUUID());
											EconomyAPI.setMoney(level, player.getUUID(), player.getScoreboardName(), currentBalance - amount);

											// Registrar en el historial de transacciones
											String timeStamp = new java.text.SimpleDateFormat("HH:mm, dd-MM").format(new java.util.Date());
											TransactionManager managerTrans = TransactionManager.get(level);
											managerTrans.addTransaction(player.getUUID(), "§7[" + timeStamp + "] §c-$" + amount + " §f(Loan Repayment)");

											// Devolver fondos a la tesorería del banco y actualizar deuda
											bankManager.addTreasury(amount);
											double remainingDebt = currentDebt - amount;
											long existingDueDate = bankManager.getLoanDueDate(player.getUUID());
											bankManager.setLoan(player.getUUID(), remainingDebt, existingDueDate);

											player.sendSystemMessage(Component.literal("§aYou have successfully paid " + EconomyConfig.formatMoney(amount) + " towards your loan. Remaining debt: " + EconomyConfig.formatMoney(remainingDebt)));

											// Si la deuda llega a 0, DESCONGELAR automáticamente la cuenta
											if (remainingDebt <= 0) {
												FrozenAccountsManager frozenManager = FrozenAccountsManager.get(level);
												if (frozenManager.isFrozen(player.getUUID())) {
													frozenManager.unfreeze(player.getUUID());
													player.sendSystemMessage(Component.literal("§a[Central Bank] Congratulations! You have fully paid your debt. Your account has been UNFROZEN."));
												}
											}

											return 1;
										})
								)
						)
				)
		);
	}
}
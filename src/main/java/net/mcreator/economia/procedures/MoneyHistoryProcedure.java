package net.mcreator.economia.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;

import net.mcreator.economia.TransactionManager;

import java.util.List;

public class MoneyHistoryProcedure {
    public static void execute(Entity entity, int page) {
        if (entity == null) return;

        if (entity.level() instanceof ServerLevel serverLevel) {
            List<String> history = TransactionManager.get(serverLevel).getHistory(entity.getUUID());

            if (history.isEmpty()) {
                entity.sendSystemMessage(Component.literal("\u00A7e📜 You have no recent transactions."));
                return;
            }

            // --- LIMPIAR VISUALMENTE EL CHAT genera espacios en blanco---
            for (int j = 0; j < 100; j++) {
                entity.sendSystemMessage(Component.literal(""));
            }

            int pageSize = 10; // Máximo 10 por página como pediste
            int totalItems = history.size();
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);

            // Validar que la página solicitada exista
            if (page > totalPages) page = totalPages;
            if (page < 1) page = 1;

            entity.sendSystemMessage(Component.literal("\u00A76=== 📜 Transaction History (Page " + page + "/" + totalPages + ") ==="));

            // Mostrar los elementos de la página actual (orden inverso para ver lo más nuevo primero)
            int startIndex = totalItems - ((page - 1) * pageSize);
            int endIndex = Math.max(0, totalItems - (page * pageSize));

            for (int i = startIndex - 1; i >= endIndex; i--) {
                entity.sendSystemMessage(Component.literal(history.get(i)));
            }

            // --- BOTONES INTERACTIVOS DE NAVEGACIÓN ---
            net.minecraft.network.chat.MutableComponent navigationBar = net.minecraft.network.chat.Component.literal("");
            boolean hasNavigation = false;

            if (page > 1) {
                Component prevButton = Component.literal("\u00A7b[⬅ Back] ")
                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$ history " + (page - 1))));
                navigationBar.append(prevButton);
                hasNavigation = true;
            }

            if (page < totalPages) {
                Component nextButton = Component.literal("\u00A7b[Next ➡]")
                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$ history " + (page + 1))));
                navigationBar.append(nextButton);
                hasNavigation = true;
            }

            if (hasNavigation) {
                entity.sendSystemMessage(navigationBar);
            }
        }
    }
}
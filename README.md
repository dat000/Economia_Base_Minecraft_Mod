# 💰 Economia Mod

Economia is a lightweight and flexible economy mod for Minecraft that introduces a simple money system for all players.
Every player has their own balance, stored as a variable, which can be easily modified through commands. This makes the mod perfect for servers, RPG experiences, or any custom gameplay that requires a basic economy system.

---

## ⚙️ Features

* 💵 **Personal Balance:** Each player has an individual money balance tracked securely.
* 🌐 **Multiplayer Network Synchronization:** Server-side configurations (currency name and prefix) are automatically synchronized via network packets to all connected clients upon login, ensuring a consistent experience for everyone without manual client-side configuration.
* ⚙️ **Fully Configurable:** Easily customizable currency name and prefix via `.toml` configuration file.
* 🔄 **Player-to-Player Transfers:** Secure transfer system with anti-spam protections and minimum amount rules.
* 📜 **Interactive Transaction History:** Clean pagination system (max 8 per page) with clickable navigation and precise timestamp logs.
* 🛠️ **Advanced Admin Tools:** Robust commands to give, remove, set player balances, and clear transaction histories.
* 💾 **Storage Optimization:** Built-in transaction limits per player to prevent file bloating (`SavedData`).
* 🎨 **Clean UI & Audio Cues:** Professional, color-coded transaction UI styling paired with custom sound feedback.

---

## 💬 Commands

### 👤 Player Commands

| Command | Description |
| :--- | :--- |
| `/$ transfer <player> <amount>` | Send money to another player (Requires amount > 0). |
| `/$ history [page]` | View your personal transaction history. |

### 🛡️ Admin Commands

| Command | Description |
| :--- | :--- |
| `/$admin get <player> <amount>` | Give money to a player (Minimum: 0.01). |
| `/$admin rmv <player> <amount>` | Remove money from a player (Minimum: 0.01). |
| `/$admin set <player> <amount>` | Set a player’s balance directly. |
| `/$admin reset <player>` | Clear a specific player's transaction history. |

---

## 🧠 About the System

The economy is designed to be simple but expandable. At its core, it provides a clean and reliable money system that can be used as a foundation for other mods or features, such as:

* 🏪 Shop systems
* 🛒 Auction House (AH)
* 🎁 Reward systems
* 🧾 Missions and quests
* 🖥️ Custom GUIs
* 🔌 Server utilities and integrations

This allows developers and modpack creators to build entire ecosystems around a single, consistent economy.

---

## 🚧 Current State (Update 1.3)

* **Multiplayer Ready:** Introduced automatic server-to-client config synchronization to unify currency names/prefixes across all players.
* **Security & Anti-Spam:** Added strict value validations (`> 0` minimum limits) on player transfers and administrative modify commands to prevent exploit loops and spamming.
* **Bug Fixes & Polish:** Resolved target scoping bugs in admin modification procedures and cleaned up history UI formatting.

---

## 📦 Perfect For

* 🌲 Survival servers
* ⚔️ RPG-style gameplay
* 🧱 Modpack creators
* ⚙️ Developers looking for an economy base system

---

## 👤 Author & Links

* **Developed by:** devdat / The Economia - Base Team
* **CurseForge:** [devdat's Profile](https://curseforge.com)
* **Reddit:** [u/datonreddit](https://reddit.com)
* **Other Mods:** Check out my full mod collection on CurseForge!

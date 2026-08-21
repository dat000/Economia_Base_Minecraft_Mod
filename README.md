# 💰 Economia Mod

Economia is a lightweight and flexible economy mod for Minecraft that introduces a simple money system for all players. Every player has their own balance, stored as a variable, which can be easily modified through commands. This makes the mod perfect for servers, RPG experiences, or any custom gameplay that requires a basic economy system.

## ⚙️ Features

* 💵 Each player has a personal money balance.
* ⚙️ Fully configurable currency name via `.toml` config file.
* 🔄 Player-to-player money transfers.
* 🏆 **Global Leaderboard (`/$ baltop`)** tracking both online and offline players.
* 📜 Interactive transaction history with pagination (max 8 per page) and clickable navigation buttons.
* 🛠️ Admin tools to give, remove, set money, and reset player transaction histories.
* 💾 Storage optimization with built-in transaction limits per player to prevent file bloating, plus persistent NBT name/balance caching.
* 🎨 Clean, professional, and color-coded transaction UI styling with custom sound cues.

## 💬 Commands

### 👤 Player Commands

| Command | Description |
| :--- | :--- |
| `/$ transfer <player> <amount>` | Send money to another player. |
| `/$ baltop` | View the global economy leaderboard (Top players). |
| `/$ history [page]` | View your personal transaction history. |

### 🛡️ Admin Commands

| Command | Description |
| :--- | :--- |
| `/$admin get <player> <amount>` | Give money to a player. |
| `/$admin rmv <player> <amount>` | Remove money from a player. |
| `/$admin set <player> <amount>` | Set a player’s balance directly. |
| `/$admin reset <player>` | Clear a specific player's transaction history. |

## 🧠 About the System

The economy is designed to be simple but expandable. At its core, it provides a clean and reliable money system that can be used as a foundation for other mods or features, such as:

* 🏪 Shop systems
* 🛒 Auction House (AH)
* 🎁 Reward systems
* 🧾 Missions and quests
* 🖥️ Custom GUIs
* 🔌 Server utilities and integrations

This allows developers and modpack creators to build entire ecosystems around a single, consistent economy.

## 📦 What's New in Update 1.3

* **Global Leaderboard (`/$ baltop`)**: View the richest players on the server anytime, with full tracking of both active online sessions and offline users.
* **Persistent NBT Storage**: Player usernames and balances are securely stored and loaded from the world data, ensuring rankings never lose track of users.
* **Real-Time Data Syncing**: Transfers and administrative commands instantly update the global database. The leaderboard also auto-syncs online players upon execution.
* **Polished Experience**: Fixed missing audio cues on administrative commands and optimized data handling.

## 📦 Perfect For

* 🌲 Survival servers
* ⚔️ RPG-style gameplay
* 🧱 Modpack creators
* ⚙️ Developers looking for an economy base system

## 👤 Author & Links

* Developed by devdat / The Economia - Base Team
* 🔨 CurseForge: [devdat's Profile](https://www.curseforge.com/)
* 👽 Reddit: `u/datonreddit`
* 📦 My Other Mods: Check out my full mod collection on CurseForge!
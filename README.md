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

## 🚧 Current State (Update 1.3 - Pre-release)

The mod has been updated with the highly requested **Global Leaderboard (`/$ baltop`)**. This version introduces persistent NBT storage for player usernames and balances, ensuring that offline players remain properly listed in rankings. It also features automatic data syncing for online players upon command execution, real-time transaction updates, and polished audio feedback for administrative actions.

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

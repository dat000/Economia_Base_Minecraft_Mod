# 💰 Economia Mod

<div align="center">

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1-blue.svg)
![Modloader](https://img.shields.io/badge/Loader-Forge%20%2F%20NeoForge-orange.svg)
![Version](https://img.shields.io/badge/Update-1.2-green.svg)

</div>

**Economia** is a lightweight and flexible economy mod for Minecraft that introduces a simple money system for all players.

Every player has their own balance, stored as a variable, which can be easily modified through commands. This makes the mod perfect for servers, RPG experiences, or any custom gameplay that requires a basic economy system.

---

## ⚙️ Features

* 💵 Each player has a personal money balance.
* ⚙️ Fully configurable currency name via `.toml` config file.
* 🔄 Player-to-player money transfers.
* 📜 Interactive transaction history with pagination (max 8 per page) and clickable navigation buttons.
* 🛠️ Admin tools to give, remove, set money, and reset player transaction histories.
* 💾 Storage optimization with built-in transaction limits per player to prevent file bloating.
* 🎨 Clean, professional, and color-coded transaction UI styling with custom sound cues.

---

## 💬 Commands

### 👤 Player Commands
| Command | Description |
| :--- | :--- |
| `/$ transfer <player> <amount>` | Send money to another player. |
| `/$ history [page]` | View your personal transaction history. |

### 🛡️ Admin Commands
| Command | Description |
| :--- | :--- |
| `/$admin get <player> <amount>` | Give money to a player. |
| `/$admin rmv <player> <amount>` | Remove money from a player. |
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

## 🚧 Current State (Update 1.2)

The mod has been updated with enhanced UI/UX for transaction menus, pagination limits, storage management, and advanced administrative reset tools.

---

## 📦 Perfect For

* 🌲 Survival servers
* ⚔️ RPG-style gameplay
* 🧱 Modpack creators
* ⚙️ Developers looking for an economy base system

---

## 👤 Author & Links

Developed by **devdat** / The Economia - Base Team

* 🔨 **CurseForge:** [devdat's Profile](https://www.curseforge.com/members/devdat/projects)
* 👽 **Reddit:** [u/datonreddit](https://www.reddit.com/user/datonreddit)
* 📦 **My Other Mods:** [Check out my full mod collection on CurseForge!](https://www.curseforge.com/minecraft/mc-mods/simple-andesite)
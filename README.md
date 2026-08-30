# Economia Mod

Economia is a lightweight, flexible, and feature-rich economy mod for Minecraft. It introduces a comprehensive money system where players manage personal balances, interact with a Central Bank, place bounties, and participate in a server-wide economy. Built with stability and customization in mind, it serves as a perfect foundation for servers, RPG experiences, or custom modpacks requiring a robust economic framework.

## Features

* **Core Economy:** Personal money balances with persistent NBT caching for offline support.
* **High Customization:** Fully configurable currency (name, prefix, suffix) via `.toml` configuration file.
* **Player Transfers & Taxes:** Direct player-to-player money transfers, featuring an optional, configurable tax rate.
* **Central Bank System:** A dynamic treasury that allows players to request loans based on server liquidity.
* **Advanced Loans:** Loans include configurable interest rates, maximum borrowing caps, and strict due dates.
* **Automated Account Freezing:** Overdue loans automatically trigger an account freeze upon login, restricting outbound transfers until the debt is cleared.
* **Bounty System:** Players can place and manage bounties on others, adding a competitive edge to the economy.
* **Global Leaderboard (`/$ baltop`):** Tracks and ranks the wealthiest players, seamlessly integrating both online and offline data.
* **Transaction History:** Interactive, paginated logs (max 8 per page) with detailed timestamps and color-coded financial tracking.
* **Admin Controls:** Comprehensive tools to grant, remove, set balances, manage bounties, manually freeze accounts, and distribute global payments.

## Commands

### Player Commands

| Command | Description |
| :--- | :--- |
| `/$ transfer <player> <amount>` | Send money to another player (subject to configured tax rates). |
| `/$ baltop` | View the global economy leaderboard. |
| `/$ history [page]` | View your personal, paginated transaction history. |
| `/$ loan` | Access Central Bank features (info, request, pay). |
| `/$ bounty` | Access the bounty system to place or view bounties on other players. |

### Admin Commands

| Command | Description |
| :--- | :--- |
| `/$admin get <player> <amount>` | Add money to a player's balance. |
| `/$admin remove <player> <amount>` | Remove money from a player's balance. |
| `/$admin set <player> <amount>` | Set a player’s balance to a specific amount. |
| `/$admin reset <player>` | Clear a specific player's transaction history. |
| `/$admin payall <amount>` | Distribute a specific amount of money to all online players. |
| `/$admin freeze <player>` | Manually freeze or unfreeze a player's bank account. |
| `/$admin history <player>` | View the transaction history of a specific player. |
| `/$admin bounty` | Manage, override, or clear server bounties. |

## About the System

The economy is designed to be highly accessible yet deeply expandable. At its core, it provides a clean and reliable money system that can easily serve as a dependency or foundation for other mods or server features, such as:

* Shop systems
* Auction Houses (AH)
* Reward systems
* Missions and quests
* Custom GUIs
* Server utilities and external integrations

This architecture allows developers and modpack creators to build intricate ecosystems around a single, consistent economy.

## Current State (Update 1.4 - Central Bank & Loans)

The mod has officially advanced to version 1.4, introducing macro-economic mechanics. The new Central Bank system allows for a regulated loan environment with configurable interest rates, administrative borrowing caps, and dynamic treasury limits. To enforce the economy, an Auto-Freeze system has been implemented, automatically restricting the accounts of players with overdue debts until they settle their balances. Additionally, this update introduces transfer taxes, a robust Bounty system, global `payall` events, and standardizes transaction logging for a polished user experience.

## Perfect For

* Survival servers
* RPG-style gameplay
* Modpack creators
* Developers looking for a stable economy base API

## Author & Links

* Developed by devdat / The Economia - Base Team
* CurseForge: [devdat's Profile](https://www.curseforge.com/)
* Reddit: u/datonreddit

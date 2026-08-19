# 🪙 DevDat's Economy Mod

<div align="center">

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1-blue.svg)
![Modloader](https://img.shields.io/badge/Loader-Forge%20%2F%20NeoForge-orange.svg)
![Version](https://img.shields.io/badge/Version-1.2.0-green.svg)
[![CurseForge](https://img.shields.io/badge/CurseForge-devdat-red.svg)](https://www.curseforge.com)

A lightweight, high-performance, and immersive economy system designed for Minecraft servers and singleplayer worlds.

</div>

---

## 🚀 Update 1.2 Highlights

The **Update 1.2** introduces major UI/UX overhauls, advanced administrative tools, and critical performance optimizations:

* **Interactive Transaction History**: Fully revamped pagination system supporting up to 8 transactions per page with clickable chat buttons (`[Previous]` / `[Next]`).
* **Visual Polish & Formatting**: Clean, color-coded transaction logs with precise timestamps, distinct styling for deposits/withdrawals, and high-contrast name rendering.
* **Storage Optimization**: Built-in maximum transaction limits per player to prevent `.dat` file bloating over time.
* **Administrative Controls**: New admin subcommand (`/$admin reset <player>`) to safely clear specific player transaction records.
* **Immersive Audio Feedback**: Integrated custom sound cues (Experience orbs for success, Villager denial sounds for failed transfers).

---

## 📋 Features

* **Player-to-Player Transfers**: Secure money transfer system with instant notifications and balance checks.
* **Persistent Transaction Logs**: Server-side data management ensuring no history is lost.
* **Optimized Performance**: Minimal overhead designed to run smoothly on servers of any size.

---

## ⚙️ Commands & Usage

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/economy pay <player> <amount>` | Transfers money to another player. | All Players |
| `/economy history [page]` | Views your personal transaction history. | All Players |
| `/admin reset <player>` | Clears the transaction history of a specified player. | Administrators |

---

## 🛠️ Technical Stack & Compatibility

* **Platform:** Minecraft 1.20.1 (Forge / NeoForge)
* **Architecture:** Server-side capability tracking with localized client feedback and network sync.

---

## 👤 Author

* **devdat**
    * [CurseForge Profile](https://www.curseforge.com) *(devdat)*
    * [Reddit](https://www.reddit.com/user/datonreddit) *(u/datonreddit)*

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
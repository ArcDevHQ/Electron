<img width="1042" height="583" alt="image" src="https://github.com/user-attachments/assets/8d72d6ad-8185-4ac0-9a91-c6a5c14a4bcb" />

![Maintained](https://img.shields.io/badge/maintained-yes-brightgreen?style=for-the-badge) ![Minecraft 1.7-1.8.9](https://img.shields.io/badge/Minecraft-1.7--1.8.9-blue?style=for-the-badge) ![Version 1.7.3](https://img.shields.io/badge/Version-1.7.3-blue?style=for-the-badge)

---

# Electron


**Lightweight Practice Core Base for Minecraft 1.7–1.8.9**

Electron is an open source **Minecraft Practice Core** 

It supports duels, kits, queues, arenas, leaderboards, and more. Still being **semi maintained**, updated periodically.  

Feel free to fork and contribute! 😎

---

## 🤝 Contributing

Before opening a pull request, **read this first**:
👉 [.github/CONTRIBUTING.md](.github/CONTRIBUTING.md)

### ⚠️ Contribution Status

We are **not actively accepting contributions**.

You are still free to open a PR, however:
- It may not be reviewed
- It may be closed without merging

---

## What's Inside?
- **Ranked lock** Unlock ranked queues after 10 unranked wins [toggleable]
- **Forfeit matches** Allow players to forfeit active matches
- **Rematch system** Request a rematch with previous opponents
- **Build mode** Enable arena building for setup and testing
- **Item renaming** Rename items via command
- **Spawn system** Central spawn handling for joins and deaths
- **Queues** Unranked and ranked queue system
- **Kits** Menu based kit editor and management
- **Arenas** Menu based arena editor and management
- **Matches** Full match lifecycle with countdowns and duration tracking
- **Scoreboard** Animated, context aware scoreboard (player state based)
- **Profile storage** MongoDB persistent player profiles
- **Divisions & ELO** Competitive ranking with admin management via `/elo`
- **Private messaging** Conversations using `/msg` and `/r`
- **Duels** Challenge players directly using `/duel <player>`
- **Hotbar system** Interactive hotbar actions
- **Leaderboards** Global and kit-based leaderboards
- **Navigator menu** Central UI navigation menu
- **Settings** Player-configurable options [scoreboard, messages, world time]
*And More...*

---

# Support
Need support? You can join our discord server and create a ticket!
- [Join here](https://discord.gg/a9RhfWEMHJ)

---

## 🛠 Permissions

| Permission          | Description                      |
|--------------------|----------------------------------|
| `electron.admin`    | Full administrative access        |
| `electron.staff`    | Staff-level commands              |
| `electron.user`     | Standard user commands            |

---

# Dependencies
- 1.8
- MongoDB

---

# Credits
- **Vifez** - Main developer & Current maintainer
- **MTR** - Contributed at the start
- **Lugami** - Insane ass pull request
- **Mqaaz** - Added 1 global title to scoreboard

---

# Compiling
- Clone the repo to your intellij
- Let maven do its magic
- run `mvn package`
- add `target/Electron-vX.X.X.jar` to ur server
- run ur server for configs to load, add mongo
- and boom... practice server!

---

© Arc Development @ 2026

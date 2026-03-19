<img width="1042" height="583" alt="image" src="https://github.com/user-attachments/assets/8d72d6ad-8185-4ac0-9a91-c6a5c14a4bcb" />

![Maintained](https://img.shields.io/badge/maintained-yes-brightgreen?style=for-the-badge) ![Minecraft 1.7-1.8.9](https://img.shields.io/badge/Minecraft-1.7--1.8.9-blue?style=for-the-badge) ![Version 1.7.2](https://img.shields.io/badge/Version-1.7.1-blue?style=for-the-badge)

---

# Electron


**Lightweight Practice Core Base for Minecraft 1.7–1.8.9**

Electron is an open source **Minecraft Practice Core** 

It supports duels, kits, queues, arenas, leaderboards, and more. Still being **semi maintained**, updated periodically.  

Feel free to fork and contribute! 😎

## 📣 Support

Need help or want to join the community?  
[Join our Discord Server](https://discord.gg/kKKC85rkXU)

# Features
- **NEW** Navigator menu
- Leaderboards (Per kit + Global elo)
- Conversations (/msg, /r)
- Scoreboard (configurable)
- Tablist (configurable)
- BedFight support
- Chunk Restorer
- FireballFight/bridges support (coming soon..)
- Join MOTD + Title
- PlaceholderAPI Support (and custom placeholders)
- MongoDB For data saving
- Duels
- Spectate
- Auto respawn
- Hotbar
- Build mode
- Elo management
- Spawn system (/setspawn)
- More command (x64 of whatever in ur hand)
*And More...*

# Support
Need support? You can join our discord server and create a ticket!
- https://discord.vifez.lol

## 🛠 Permissions

| Permission          | Description                      |
|--------------------|----------------------------------|
| `electron.admin`    | Full administrative access        |
| `electron.staff`    | Staff-level commands              |
| `electron.user`     | Standard user commands            |

# Dependencies
- Packet events
- ProtocolLib
- 1.8
- MongoDB

# Credits
- **Vifez** - Main developer & Current maintainer
- **MTR** - Contributed heavily with me at the start
- **Lugami** - Insane ass pull request
- **Mqaaz** - Add 1 global title to scoreboard

# Compiling
- Clone the repo to your intellij
- Let maven do its magic
- run `mvn package`
- add `target/Electron.jar` to ur server
- add `libs/packetevents-2.7.0.jar` to ur server
- run ur server for configs to load, add mongo
- and boom... practice server!
---
© vifez 2025

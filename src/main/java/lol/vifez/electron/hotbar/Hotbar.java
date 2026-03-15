package lol.vifez.electron.hotbar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Getter
@RequiredArgsConstructor
public enum Hotbar {

    QUEUES("HOTBAR.LOBBY.QUEUES"),
    UNRANKED("HOTBAR.LOBBY.UNRANKED"),
    RANKED("HOTBAR.LOBBY.RANKED"),
    LEADERBOARDS("HOTBAR.LOBBY.LEADERBOARDS"),
    KIT_EDITOR("HOTBAR.LOBBY.KIT-EDITOR"),
    SETTINGS("HOTBAR.LOBBY.SETTINGS"),
    NAVIGATOR("HOTBAR.LOBBY.NAVIGATOR"),
    LEAVE_QUEUE("HOTBAR.IN-QUEUE.LEAVE_QUEUE");

    private final String path;
}
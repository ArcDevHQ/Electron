package lol.vifez.electron.game.arena;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Data
public class Arena {

    private final String name;

    private String type = "default";
    private Location spawnA;
    private Location spawnB;
    private Material icon = Material.PAPER;

    private final List<String> kits = new ArrayList<>();

    private boolean busy;

    private final List<Block> blocksBuilt = new ArrayList<>();
    private final Map<Block, Material> blockBroken = new HashMap<>();

    private Location positionOne;
    private Location positionTwo;

    public Arena(String name) {
        this.name = name;
    }

    public Arena(String name, String type, Location spawnA, Location spawnB,
                 Material icon, Location positionOne, Location positionTwo) {
        this.name = name;
        this.type = type;
        this.spawnA = spawnA;
        this.spawnB = spawnB;
        this.icon = icon;
        this.positionOne = positionOne;
        this.positionTwo = positionTwo;
    }

    public void setKits(List<String> kits) {
        this.kits.clear();
        if (kits != null) {
            this.kits.addAll(kits);
        }
    }
}
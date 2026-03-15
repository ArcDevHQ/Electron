package lol.vifez.electron.util;

import lol.vifez.electron.game.divisions.Divisions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public final class DivisionUtil {

    private DivisionUtil() {
    }

    public static List<Divisions> getAll() {
        return Arrays.asList(Divisions.values());
    }

    public static List<Divisions> getRange(Divisions from, Divisions to) {
        return Arrays.stream(Divisions.values())
                .filter(division -> division.ordinal() >= from.ordinal())
                .filter(division -> division.ordinal() <= to.ordinal())
                .collect(Collectors.toList());
    }

    public static List<Divisions> getPage(int page, int pageSize) {
        List<Divisions> divisions = getAll();

        int startIndex = Math.max(0, (page - 1) * pageSize);
        int endIndex = Math.min(startIndex + pageSize, divisions.size());

        if (startIndex >= divisions.size()) {
            return Arrays.asList();
        }

        return divisions.subList(startIndex, endIndex);
    }

    public static boolean hasNextPage(int page, int pageSize) {
        return page * pageSize < Divisions.values().length;
    }
}
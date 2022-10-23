package com.github.AvhiDh;

import java.util.HashMap;
import java.util.UUID;

public class Helpers {

    public enum AvailableCmds {
        FREEZE,
        DEATHSPOT
    }

    public static HashMap<UUID, Boolean> frozenPlayers;

    public static void initialize() {
        frozenPlayers = new HashMap<UUID, Boolean>();
    }

    public static void performCleanup() {
        if (frozenPlayers != null) {
            frozenPlayers.clear();
            frozenPlayers = null;
        }
    }

}

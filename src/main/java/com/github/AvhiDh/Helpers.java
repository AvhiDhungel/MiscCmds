package com.github.AvhiDh;

import java.util.HashMap;
import java.util.UUID;

public class Helpers {

    public enum AvailableCmds {
        FREEZE,
        DEATHSPOT
    }

    public static HashMap<UUID, Boolean> frozenPlayers = new HashMap<UUID, Boolean>();

    public static void performCleanup() {
        frozenPlayers.clear();
        frozenPlayers = null;
    }

}

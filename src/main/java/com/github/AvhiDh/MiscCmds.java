package com.github.AvhiDh;

import com.github.AvhiDh.SqlUtilities.SqlDatabaseConnection;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MiscCmds extends JavaPlugin {

    private FileConfiguration config = getConfig();
    private SqlDatabaseConnection conn;

    @Override
    public void onEnable() {
        setupConfig();
        setupDB();
        setupCommands();
        setupListeners();
    }

    @Override
    public void onDisable() { Helpers.performCleanup(); }

    private void setupConfig() {
        List<String> header = new ArrayList<String>();
        header.add("List of Permissions:");
        header.add("Freeze: MiscCmds.Freeze");

        config.addDefault("sql.url", "localhost:3306");
        config.addDefault("sql.database", "Survival-MTH");
        config.addDefault("sql.user", "admin");
        config.addDefault("sql.password", "passwordhere");

        config.options().setHeader(header);
        saveConfig();
    }

    public void setupDB() {
        String url = config.getString("sql.url");
        String db = config.getString("sql.database");
        String user = config.getString("sql.user");
        String password = config.getString("sql.password");

        if (!password.toLowerCase().equals("passwordhere")) {
            conn = new SqlDatabaseConnection(url, db, user, password);
            DBInitializer.execute(conn);
        }
    }

    private void setupCommands() {
        Runnable reloadConfig = new Runnable() {
            @Override
            public void run() { reload(); }
        };

        PluginCommand freezeCmd = this.getCommand("freeze");
        freezeCmd.setExecutor(new Commands(Helpers.AvailableCmds.FREEZE, reloadConfig));
        freezeCmd.setTabCompleter(new TabCompleter(Helpers.AvailableCmds.FREEZE));

        PluginCommand dsCmd = this.getCommand("deathspot");
        dsCmd.setExecutor(new Commands(Helpers.AvailableCmds.DEATHSPOT, reloadConfig, conn));
        dsCmd.setTabCompleter(new TabCompleter(Helpers.AvailableCmds.DEATHSPOT));
    }

    private void setupListeners() {
        EventListeners l = new EventListeners(conn);
        getServer().getPluginManager().registerEvents(l, this);
    }

    private void reload() {
        reloadConfig();
        config = getConfig();
        setupDB();
    }


}

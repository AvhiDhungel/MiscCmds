package com.github.AvhiDh;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private Helpers.AvailableCmds cmd;

    public TabCompleter(Helpers.AvailableCmds cmd) {
        this.cmd = cmd;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> li = new ArrayList<String>();

        String arg;
        if (args.length > 0) { arg = args[0].toLowerCase(); } else {arg = "";}

        List<Helpers.AvailableCmds> usesPlayerList = new ArrayList<>();
        usesPlayerList.add(Helpers.AvailableCmds.DEATHSPOT);
        usesPlayerList.add(Helpers.AvailableCmds.FREEZE);
        usesPlayerList.add(Helpers.AvailableCmds.HP);

        if (usesPlayerList.contains(cmd)) { playerListCompleter(li, arg); }

        if (cmd == Helpers.AvailableCmds.DEATHSPOT) {
            if (sender.hasPermission("deathspot.admin") && (arg.length() == 0 || arg.contains("reload"))) {
                li.add("reload");
            }
        }

        return li;
    }

    private void playerListCompleter(List<String> li, String arg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            String name = p.getName().toLowerCase();
            if (arg.length() == 0 || name.contains(arg)) { li.add(p.getName()); }
        }
    }
}

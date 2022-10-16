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

        if (cmd == Helpers.AvailableCmds.FREEZE) {
            playerListCompleter(li);
        } else if (cmd == Helpers.AvailableCmds.DEATHSPOT) {
            if (sender.hasPermission("deathspot.admin")) {
                li.add("reload");
            }
            playerListCompleter(li);
        }

        return li;
    }

    private void playerListCompleter(List<String> li) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            li.add(p.getName());
        }
    }
}

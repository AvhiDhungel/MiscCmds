package com.github.AvhiDh;

import com.github.AvhiDh.SqlUtilities.SqlDataReader;
import com.github.AvhiDh.SqlUtilities.SqlDatabaseConnection;
import com.github.AvhiDh.SqlUtilities.SqlQueryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;
import java.util.Date;

public class Commands implements CommandExecutor {
    private Runnable reloadConfig;
    private Helpers.AvailableCmds cmd;
    private SqlDatabaseConnection conn;
    private static String prefix = String.format("%s[%sMiscCmd%s] %s",
            ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY, ChatColor.GREEN);

    public Commands(Helpers.AvailableCmds cmd, Runnable reloadConfig) {
        this.cmd = cmd;
        this.reloadConfig = reloadConfig;
    }

    public Commands(Helpers.AvailableCmds cmd, Runnable reloadConfig, SqlDatabaseConnection conn) {
        this.cmd = cmd;
        this.reloadConfig = reloadConfig;
        this.conn = conn;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (cmd == Helpers.AvailableCmds.FREEZE) {
            onFreezeCommand(sender, command, label, args);
        } else if (cmd == Helpers.AvailableCmds.DEATHSPOT) {
            onDeathSpotCommand(sender, command, label, args);
        } else if (cmd == Helpers.AvailableCmds.HELPOP) {
            onHPCommand(sender, command, label, args);
        } else if (cmd == Helpers.AvailableCmds.STAFFCHAT) {
            onStaffChatCommand(sender, command, label, args);
        }
        return true;
    }

    private void onFreezeCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            SendMessage(sender, ChatColor.DARK_GRAY + "Usage: " + ChatColor.GRAY + "/freeze <playername>");
        } else {
            String arg = args[0];
            Player pl = Bukkit.getPlayer(arg);

            if (pl == null) {
                SendMessage(sender, ChatColor.RED + "No player found by the name of " + ChatColor.YELLOW + arg);
            } else {
                if (pl.getWalkSpeed() == 0) {
                    pl.setWalkSpeed(0.2f);
                    Helpers.frozenPlayers.put(pl.getUniqueId(), false);
                    SendMessage(sender, ChatColor.AQUA + "Player has been unfrozen!");
                } else {
                    pl.setWalkSpeed(0);
                    Helpers.frozenPlayers.put(pl.getUniqueId(), true);
                    SendMessage(sender, ChatColor.AQUA + "Player has been frozen!");
                }
            }
        }
    }

    private void onDeathSpotCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            SendMessage(sender, ChatColor.DARK_GRAY + "Usage: " + ChatColor.GRAY + "/ds <playername>");
        } else {
            String arg = args[0].toLowerCase();

            if (arg.equals("reload")) {
                if (sender.hasPermission("deathspot.admin")) {
                    reloadConfig.run();
                    SendMessage(sender, ChatColor.GREEN + "Config Reloaded!");
                } else {
                    SendMessage(sender, ChatColor.RED + "You do not have the permission for this");
                }
            } else {
                Player pl = Bukkit.getPlayer(arg);

                if (pl == null) {
                    SendMessage(sender, ChatColor.RED + "No player found by the name of " + ChatColor.YELLOW + arg);
                } else {
                    SqlQueryBuilder sql = new SqlQueryBuilder();
                    sql.appendLine("SELECT * FROM av_tblDeathSpot ");
                    sql.appendLine("WHERE fldPlayerId = '%s' ", pl.getUniqueId().toString());
                    sql.appendLine("ORDER BY fldDate DESC ");
                    sql.appendLine("LIMIT 1");

                    Integer xPos, yPos, zPos;
                    String world, msg, recommended;
                    Date d;

                    SqlDataReader dr = conn.execute(sql);

                    if (dr.read()) {
                        xPos = dr.getInteger("fldXPos");
                        yPos = dr.getInteger("fldYPos");
                        zPos = dr.getInteger("fldZPos");
                        world = dr.getString("fldWorld");
                        d = dr.getDate("fldDate");

                        recommended = String.format("/tppos %s %s %s %s", xPos, yPos, zPos, world);
                        msg = String.format("%s%s %slast died in %s%s %s %s %s %son %s%s",
                                ChatColor.YELLOW, pl.getName(), ChatColor.GREEN,
                                ChatColor.YELLOW, xPos, yPos, zPos, world,
                                ChatColor.GREEN, ChatColor.YELLOW, d.toString());


                    } else {
                        msg = "No previous death location found for " + ChatColor.YELLOW + pl.getName();
                        recommended = "";
                    }

                    conn.close();

                    if (sender instanceof Player && !recommended.isEmpty()) {
                        SendClickableMessage(sender, msg + ChatColor.WHITE + " - Click to Teleport", recommended);
                    } else {
                        SendMessage(sender, msg);
                    }
                }
            }

        }
    }

    private void onHPCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.performCommand(String.format("helpop %s", String.join(" ", args)));
        }
    }

    private void onStaffChatCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.chat(String.format("@ %s", String.join(" ", args)));
        }
    }

    private void SendMessage(CommandSender s, String msg) {s.sendMessage(prefix + msg);}
    private void SendClickableMessage(CommandSender s, String msg, String recommendedCommand) {
        TextComponent component = new TextComponent(msg);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, recommendedCommand));

        Player sendingPlayer = (Player) s;
        sendingPlayer.spigot().sendMessage(component);
    }
}

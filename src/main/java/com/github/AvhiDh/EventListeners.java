package com.github.AvhiDh;

import com.github.AvhiDh.SqlUtilities.SqlDatabaseConnection;
import com.github.AvhiDh.SqlUtilities.SqlQueryBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventListeners implements Listener {

    private SqlDatabaseConnection conn;

    public EventListeners(SqlDatabaseConnection conn) {
        this.conn = conn;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (Helpers.frozenPlayers == null) {Helpers.initialize();}

        Player pl = e.getPlayer();
        UUID plId = pl.getUniqueId();

        if (!Helpers.frozenPlayers.containsKey(plId)) {
            Helpers.frozenPlayers.put(plId, pl.getWalkSpeed() == 0);
        }

        if (Helpers.frozenPlayers.get(plId)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player pl = e.getEntity().getPlayer();
        Location l = pl.getLocation();

        SqlQueryBuilder sql = new SqlQueryBuilder();
        sql.appendLine("INSERT INTO av_tblDeathSpot ");
        sql.appendLine("(fldPlayerId, fldXPos, fldYPos, fldZPos, fldWorld, fldDate) ");
        sql.appendLine("VALUES('%s','%s','%s','%s','%s','%s')",
                pl.getUniqueId(),
                (int) l.getX(),
                (int) l.getY(),
                (int) l.getZ(),
                l.getWorld().getName(),
                LocalDateTime.now().toString()
        );

        conn.executeNonQuery(sql);
    }

}

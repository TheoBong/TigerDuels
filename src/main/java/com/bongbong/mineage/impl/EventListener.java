package com.bongbong.mineage.impl;

import com.bongbong.mineage.match.Match;
import com.bongbong.mineage.match.MatchState;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@RequiredArgsConstructor
public class EventListener implements Listener {
    final State state;

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        Match match = state.getMatch(player);

        if (match == null) return;

        if (match.getState() == MatchState.WAITING) {
            match.leave(player);
            return;
        }

        if (match.getPlayer(player).isDead()) return;

        match.forfeit(player);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        Match match = state.getMatch(player);

        if (match == null) return;

        if (match.getState() != MatchState.PLAYING) return;
        if (match.getPlayer(player).isDead()) return;

        player.sendMessage("You cannot perform commands other than /ff while dueling.");
        event.setCancelled(true);
    }

    public void onDisconnect(Player player) {
        Match match = state.getMatch(player);

        if (match == null) return;

        if (match.getState() != MatchState.PLAYING) return;
        if (match.getPlayer(player).isDead()) return;

        match.sendMessage(player.getName() + " disconnected.");
        match.playerDied(match.getPlayer(player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        onDisconnect(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        onDisconnect(event.getPlayer());
    }


}



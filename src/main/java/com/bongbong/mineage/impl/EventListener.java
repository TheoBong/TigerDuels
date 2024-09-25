package com.bongbong.mineage.impl;

import com.bongbong.mineage.Arena;
import com.bongbong.mineage.DuelsPlugin;
import com.bongbong.mineage.match.Match;
import com.bongbong.mineage.match.MatchPlayer;
import com.bongbong.mineage.match.MatchState;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class EventListener implements Listener {
    final State state;

    static final List<String> ALLOWED_COMMANDS = Arrays.asList(
            "/msg",
            "/m",
            "/message",
            "/w",
            "/whisper",
            "/reply",
            "/r",
            "/forfeit",
            "/ff",
            "/duelinvite",
            "/di",
            "/dinvite",
            "/cancelduel",
            "/cancel"
    );

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        Match match = state.getMatch(player);

        if (match == null) return;

        if (event.getTo() == Arena.ARENA_SPAWN_1 || event.getTo() == Arena.ARENA_SPAWN_2
                || event.getTo() == Arena.ARENA_MIDDLE) return;

        if (match.getState() == MatchState.WAITING) {
            match.leave(player);
            return;
        }

        if (match.getPlayer(player).isDead()) return;

        if (match.getState() == MatchState.STARTING) return;
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        match.forfeit(player);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(DuelsPlugin.ADMIN_PERMISSION)) return;

        Match match = state.getMatch(player);

        if (match == null) return;

//        if (match.getState() != MatchState.PLAYING) return;
        if (match.getPlayer(player).isDead()) return;

        for (String cmd : ALLOWED_COMMANDS)
            if (event.getMessage().startsWith(cmd)) return;

        player.sendMessage("Allowed commands while dueling: /ff, /w, /r, /dinvite, /cancel");
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

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;


        Player player = (Player) event.getEntity();
        Match match = state.getMatch(player);

        if (match == null) return;

        MatchPlayer matchPlayer = match.getPlayer(player);
        if (matchPlayer == null) return;

        if (matchPlayer.isDead() || match.getState() != MatchState.PLAYING) {
            event.setCancelled(true);
            return;
        }

        if ((player.getHealth() - event.getFinalDamage()) <= 0) {
            event.setCancelled(true);
            match.playerDied(matchPlayer);

            // TODO: make them invisible to all other players until the "ENDING" stage without teleporting him anywhere
        }
    }

}



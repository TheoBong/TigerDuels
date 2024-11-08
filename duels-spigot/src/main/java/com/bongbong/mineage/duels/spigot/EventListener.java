package com.bongbong.mineage.duels.spigot;

import io.grpc.Channel;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class EventListener implements Listener {
    private final Logger logger;
    private final Channel channel;

    public void onDeath(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        RPCHandler.postDeath(player, channel, logger);
    }


    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if ((player.getHealth() - event.getFinalDamage()) <= 0) {
            event.setCancelled(true);
            onDeath(player);
        }
    }

}



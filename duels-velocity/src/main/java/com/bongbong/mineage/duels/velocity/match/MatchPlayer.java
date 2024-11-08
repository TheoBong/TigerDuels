package com.bongbong.mineage.duels.velocity.match;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Data
@RequiredArgsConstructor
public class MatchPlayer {
    private final Player player;
    private final RegisteredServer originalServer;

    private boolean disconnected, dead;
    private int potionsThrown, potionsMissed, longestCombo, combo, hits, initialFood;
}

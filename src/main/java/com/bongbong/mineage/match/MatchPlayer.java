package com.bongbong.mineage.match;

import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

@Data
public class MatchPlayer {
    private final Player player;

    private boolean disconnected, dead;
    private int potionsThrown, potionsMissed, longestCombo, combo, hits;

    private String initialInventory, initialArmor;
    private double initialHealth;
    private int initialFood;
    private GameMode initialGamemode;
    private Location initialLocation;
    private Collection<PotionEffect> initialPotionEffects;

    public MatchPlayer(Player player) {
        this.player = player;
    }

    public void incPotionsThrown() {
        this.potionsThrown++;
    }

    public void incPotionsMissed() {
        this.potionsMissed++;
    }

    public void handleHit() {
        this.hits++;
        this.combo++;

        if (this.combo > this.longestCombo)
            this.longestCombo = this.combo;
    }

    public void resetCombo() {
        this.combo = 0;
    }
}

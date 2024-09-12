package com.bongbong.mineage.match;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
public class MatchPlayer {
    private final Player player;

    private boolean disconnected;
    private boolean dead;
    private int hits;
    private int combo;
    private int longestCombo;
    private int potionsThrown;
    private int potionsMissed;
    private ItemStack[] inventory;

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

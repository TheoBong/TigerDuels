package com.bongbong.mineage.match;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchTask extends BukkitRunnable {
    private final Match match;
    private int action;

    public MatchTask(Match match) {
        this.match = match;
        this.action = 6;
    }

    @Override
    public void run() {
        this.action--;

        if (match.getState() != MatchState.STARTING) return;

        if (action == 0) {
            match.setState(MatchState.PLAYING);
            match.startPlaying();
            match.sendSound(Sound.ORB_PICKUP, 1.0F, 1.0F);
            match.sendMessage("Match Started!");
        } else {
            match.sendSound(Sound.ORB_PICKUP, 1.0F, 15F);
            match.sendMessage("Match starting in " + action + " second" + (action == 1 ? "." : "s."));
        }
    }
}
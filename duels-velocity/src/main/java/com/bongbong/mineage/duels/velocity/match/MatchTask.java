package com.bongbong.mineage.duels.velocity.match;


import com.bongbong.mineage.duels.velocity.utils.Colors;
import dev.simplix.protocolize.data.Sound;

public class MatchTask implements Runnable {
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
            match.sendSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            match.sendMessage(Colors.get("Match Started!"));
        } else {
            match.sendSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 15F);
            match.sendMessage(Colors.get("Match starting in " + action + " second" + (action == 1 ? "." : "s.")));
        }
    }
}
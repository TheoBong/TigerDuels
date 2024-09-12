package com.bongbong.mineage.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.bongbong.mineage.match.Match;
import com.bongbong.mineage.match.MatchTask;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("acceptduel")
@RequiredArgsConstructor
public class AcceptDuelCommand extends BaseCommand {
    final TaskScheduler taskScheduler;
    final State state;

    @Default
    @Syntax("<match-id>")
    public void execute(Player sender, String rawmatch) {
        UUID matchid;

        try {
            matchid = UUID.fromString(rawmatch);
        } catch (IllegalArgumentException exception){
            sender.sendMessage("Match must be a valid UUID.");
            return;
        }

        Match match = state.getMatch(matchid);

        if (match == null) {
            sender.sendMessage("That is not a valid match id.");
            return;
        }

        if (match.duelAccepted(sender)) {
            taskScheduler.runTaskTimer(new MatchTask(match), 0L, 20L);
            match.startPlaying();
        }
    }

}
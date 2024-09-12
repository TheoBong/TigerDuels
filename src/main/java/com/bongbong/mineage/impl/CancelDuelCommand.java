package com.bongbong.mineage.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.bongbong.mineage.match.Match;
import com.bongbong.mineage.match.MatchState;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@CommandAlias("cancelduel|leaveduel|ff")
@RequiredArgsConstructor
public class CancelDuelCommand extends BaseCommand {
    final State state;

    @Default
    public void execute(Player sender) {
        Match match = state.getMatch(sender);

        if (match == null) {
            sender.sendMessage("You are not waiting for a match");
            return;
        }

        if (match.getState() == MatchState.WAITING) {
            match.leave(sender);
            return;
        }

        match.forfeit(sender);
    }

}
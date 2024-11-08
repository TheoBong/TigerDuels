package com.bongbong.mineage.duels.velocity.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.bongbong.mineage.duels.velocity.match.Match;
import com.bongbong.mineage.duels.velocity.match.MatchState;
import com.bongbong.mineage.duels.velocity.utils.Colors;
import lombok.RequiredArgsConstructor;
import com.velocitypowered.api.proxy.Player;

@CommandAlias("cancelduel|cancel|forfeit|ff")
@RequiredArgsConstructor
public class CancelDuelCommand extends BaseCommand {
    final State state;

    @Default
    public void execute(Player sender) {
        Match match = state.getMatch(sender);

        if (match == null) {
            sender.sendMessage(Colors.get("You are not in a match"));
            return;
        }

        if (match.getState() == MatchState.WAITING) {
            match.leave(sender);
            return;
        }

        match.forfeit(sender);
        sender.sendMessage(Colors.get("You forfeited."));
    }

}
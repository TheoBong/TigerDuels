package com.bongbong.mineage.duels.velocity.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.velocity.contexts.OnlinePlayer;
import com.bongbong.mineage.duels.velocity.match.Match;
import com.bongbong.mineage.duels.velocity.utils.Colors;
import lombok.RequiredArgsConstructor;
import com.velocitypowered.api.proxy.Player;

@CommandAlias("endduel")
@RequiredArgsConstructor
public class EndDuelCommand extends BaseCommand {
    final State state;

    @Default
    @Syntax("<target>")
    public void execute(Player sender, OnlinePlayer target) {
        Match match = state.getMatch(target.getPlayer());

        if (match == null) {
            sender.sendMessage(Colors.get("Target is not in a match."));
            return;
        }

        match.endMatch();
        sender.sendMessage(Colors.get("Ended match successfully."));
    }

}
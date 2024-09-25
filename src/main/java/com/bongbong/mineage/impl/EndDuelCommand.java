package com.bongbong.mineage.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.bongbong.mineage.match.Match;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@CommandAlias("endduel")
@RequiredArgsConstructor
public class EndDuelCommand extends BaseCommand {
    final State state;

    @Default
    @Syntax("<target>")
    public void execute(Player sender, OnlinePlayer target) {
        Match match = state.getMatch(target.getPlayer());

        if (match == null) {
            sender.sendMessage("Target is not in a match.");
            return;
        }

        match.endMatch();
        sender.sendMessage("Ended match successfully.");
    }

}
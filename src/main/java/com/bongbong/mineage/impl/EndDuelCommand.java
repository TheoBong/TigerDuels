package com.bongbong.mineage.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.bongbong.mineage.match.Match;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@CommandAlias("endduel")
@RequiredArgsConstructor
public class EndDuelCommand extends BaseCommand {
    final State state;

    @Default
    @Syntax("<target>")
    public void execute(Player sender, Player target) {

        if (!target.isOnline()) {
            sender.sendMessage("Target must be online");
            return;
        }

        Match match = state.getMatch(target);

        if (match == null) {
            sender.sendMessage("Target is not in a match.");
            return;
        }

        match.endMatch();
        sender.sendMessage("Ended match successfully.");
    }

}
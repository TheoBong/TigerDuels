package com.bongbong.mineage.duels.velocity.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.bongbong.mineage.duels.velocity.match.Match;
import com.bongbong.mineage.duels.velocity.utils.Colors;
import com.velocitypowered.api.proxy.Player;
import io.grpc.Channel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.UUID;

@CommandAlias("acceptduel")
@RequiredArgsConstructor
public class AcceptDuelCommand extends BaseCommand {
    private final State state;

    @Default
    @Syntax("<match-id>")
    public void execute(Player sender, String rawmatch) {
        UUID matchid;

        try {
            matchid = UUID.fromString(rawmatch);
        } catch (IllegalArgumentException exception){
            sender.sendMessage(Colors.get("Match must be a valid UUID."));
            return;
        }

        Match match = state.getMatch(matchid);

        if (match == null) {
            sender.sendMessage(Colors.get("That is not a valid match id."));
            return;
        }

        if (match.getWager() > 0)
            if (WagerFunctions.getBalance(sender.getUniqueId(), match.getWagerChannel()) < match.getWager()) {
                sender.sendMessage(Colors.get("You do not have enough funds to accept a duel with that wager."));
                return;
            }


        assert sender.getCurrentServer().isPresent();
        match.duelAccepted(sender, sender.getCurrentServer().get().getServer());
    }

}
package com.bongbong.mineage.duels.velocity.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.bongbong.mineage.duels.velocity.match.Match;
import com.bongbong.mineage.duels.velocity.utils.Colors;
import lombok.RequiredArgsConstructor;
import com.velocitypowered.api.proxy.Player;

import java.util.UUID;

@CommandAlias("acceptinvite")
@RequiredArgsConstructor
public class AcceptInviteCommand extends BaseCommand {
    final TaskScheduler taskScheduler;
    final State state;

    @Default
    @Syntax("<invite-id>")
    public void execute(Player sender, String rawInvite) {
        UUID inviteId;

        try {
            inviteId = UUID.fromString(rawInvite);
        } catch (IllegalArgumentException exception){
            sender.sendMessage(Colors.get("Invite must be a valid UUID."));
            return;
        }

        Match match = state.getMatchFromInvite(inviteId);

        if (match == null) {
            sender.sendMessage(Colors.get("That is not a valid invite id."));
            return;
        }

        assert sender.getCurrentServer().isPresent();
        match.addPlayer(sender, inviteId, sender.getCurrentServer().get().getServer());
    }

}
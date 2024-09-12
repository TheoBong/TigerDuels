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
            sender.sendMessage("Invite must be a valid UUID.");
            return;
        }

        Match match = state.getMatchFromInvite(inviteId);

        if (match == null) {
            sender.sendMessage("That is not a valid invite id.");
            return;
        }

        if (match.addPlayer(sender, inviteId)) {
            taskScheduler.runTaskTimer(new MatchTask(match), 0L, 20L);
            match.startPlaying();
        }
    }

}
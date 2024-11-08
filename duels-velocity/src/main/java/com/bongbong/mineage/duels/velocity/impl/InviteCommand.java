package com.bongbong.mineage.duels.velocity.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.velocity.contexts.OnlinePlayer;
import com.bongbong.mineage.duels.velocity.match.Match;
import com.bongbong.mineage.duels.velocity.utils.Colors;
import lombok.RequiredArgsConstructor;
import com.bongbong.mineage.duels.velocity.match.MatchState;
import com.bongbong.mineage.duels.velocity.match.MatchTeam;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.UUID;

@RequiredArgsConstructor
@CommandAlias("duelinvite|dinvite|di")
public class InviteCommand extends BaseCommand {
    final State state;

    @Default
    @Syntax("<target>")
    @CommandCompletion("@players")
    public void execute(Player sender, OnlinePlayer target) {
        Match match = state.getMatch(sender);

        if (match == null) {
            sender.sendMessage(Colors.get("You are not waiting for a match"));
            return;
        }

        if (match.getState() != MatchState.WAITING) {
            sender.sendMessage(Colors.get("You are not currently in waiting stage for your duel."));
            return;
        }

        if (match.getTeam(sender).getLeader().getPlayer() != sender) {
            sender.sendMessage(Colors.get("You are not the leader of this duel."));
            return;
        }

        MatchTeam team = match.getTeam(sender);
        if (team.getFollowers().size() >= match.getSize()) {
            sender.sendMessage(Colors.get("Your team is already full."));
            return;
        }

        UUID inviteId = match.generateInvite(team);

        Component text = Component.text(sender.getUsername() + " wants you on their team for a duel (Kit: "
                + match.getKit().getName() + ") [Click to accept]")
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(
                "Click this message to accept the invite")))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptinvite " + inviteId));

        target.getPlayer().sendMessage(text);

        sender.sendMessage(Colors.get("Successfully invited " + target.getPlayer().getUsername() + " to duel with you."));
    }
}

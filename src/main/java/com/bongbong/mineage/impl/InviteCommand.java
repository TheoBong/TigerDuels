package com.bongbong.mineage.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.bongbong.mineage.match.Match;
import com.bongbong.mineage.match.MatchState;
import com.bongbong.mineage.match.MatchTeam;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

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
            sender.sendMessage("You are not waiting for a match");
            return;
        }

        if (match.getState() != MatchState.WAITING) {
            sender.sendMessage("You are not currently in waiting stage for your duel.");
            return;
        }

        if (match.getTeam(sender).getLeader().getPlayer() != sender) {
            sender.sendMessage("You are not the leader of this duel.");
            return;
        }

        MatchTeam team = match.getTeam(sender);
        if (team.getFollowers().size() >= match.getSize()) {
            sender.sendMessage("Your team is already full.");
            return;
        }

        UUID inviteId = match.generateInvite(team);

        TextComponent text = new TextComponent(sender.getDisplayName() + " wants you on their team for a duel (Kit: "
                + match.getKit().getName() + ") [Click to accept]");

        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                "Click this message to accept the invite").create()));

        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptinvite " + inviteId));
        target.getPlayer().spigot().sendMessage(text);

        sender.sendMessage("Successfully invited " + target.getPlayer().getDisplayName() + " to duel with you.");
    }
}

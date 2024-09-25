package com.bongbong.mineage.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.bongbong.mineage.EconomyHook;
import com.bongbong.mineage.kit.KitType;
import com.bongbong.mineage.match.Match;
import com.bongbong.mineage.match.MatchState;
import com.bongbong.mineage.match.MatchTask;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@CommandAlias("duel|d")
public class DuelCommand extends BaseCommand {
    final State state;
    final TaskScheduler taskScheduler;

    @Default
    @Syntax("<target> <nodebuff/gapple> [wager] [# of teammates]")
    @CommandCompletion("@players")
    public void execute(Player sender, OnlinePlayer target, @Values("nodebuff|gapple") String rawKit, @Default("0") int wager, @Default("0") int size) {
        if (sender == target.getPlayer()) {
            sender.sendMessage("You cannot duel yourself");
            return;
        }

        KitType kit = KitType.getKitByName(rawKit.toUpperCase());
        assert kit != null;

        // logic for whether both parties have sufficient funds for wager
        if (wager > 0) {
            if (EconomyHook.getBalance(sender) < wager) {
                sender.sendMessage("You do not have sufficient funds for that wager.");
                return;
            }

            if (EconomyHook.getBalance(target.getPlayer()) < wager) {
                sender.sendMessage("Your opponent does not have sufficient funds for that wager.");
                return;
            }
        }

        Match match = new Match(kit, wager, size) {
            @Override
            public void onStart() {
                this.setState(MatchState.STARTING);
                taskScheduler.runTaskTimer(new MatchTask(this), 0L, 20L);
            }

            @Override
            public void onEnd() {
                taskScheduler.runTaskDelay(3, this::cleanup);
                state.removeMatch(this.getId());
            }
        };

        match.duelInitiated(sender);
        state.addMatch(match);

        TextComponent text = new TextComponent(sender.getDisplayName() + " has requested to duel you (Kit: "
                + kit.getName() + " / Wager: $" + wager + " / Teammates: " + size + ") [Click to accept]");

        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                "Click this message to accept the duel").create()));

        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptduel " + match.getId()));
        target.getPlayer().spigot().sendMessage(text);

        sender.sendMessage("Sent duel request to " + target.getPlayer().getDisplayName() + "  (Kit: "
                + kit.getName() + " / Wager: $" + wager + " / Teammates: " + size + ").");
    }
}

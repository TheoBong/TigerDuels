package com.bongbong.mineage.duels.velocity.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.velocity.contexts.OnlinePlayer;
import com.bongbong.mineage.duels.shared.Kit;
import com.bongbong.mineage.duels.velocity.match.Match;
import com.bongbong.mineage.duels.velocity.utils.Colors;
import com.bongbong.mineage.duels.velocity.utils.IPPortPair;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.grpc.Channel;
import lombok.RequiredArgsConstructor;
import com.bongbong.mineage.duels.velocity.match.MatchState;
import com.bongbong.mineage.duels.velocity.match.MatchTask;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@CommandAlias("duel|d")
public class DuelCommand extends BaseCommand {
    private final State state;
    private final TaskScheduler taskScheduler;
    private final Map<String, Channel> wagerChannels;
    private final Channel duelsChannel;
    private final RegisteredServer duelServer;

    @Default
    @Syntax("<target> <nodebuff/gapple> [wager] [# of teammates]")
    @CommandCompletion("@players")
    public void execute(Player sender, OnlinePlayer target, @Values("nodebuff|gapple") String rawKit, @Default("0") int wager, @Default("0") int size) {
        if (sender == target.getPlayer()) {
            sender.sendMessage(Colors.get("You cannot duel yourself"));
            return;
        }

        Kit.KitType kit = Kit.KitType.getKitByName(rawKit.toUpperCase());
        assert kit != null;

        assert sender.getCurrentServer().isPresent();
        RegisteredServer server = sender.getCurrentServer().get().getServer();
        Channel wagerChannel = wagerChannels.get(server.getServerInfo().getName());

        if (wager > 0) {
            if (WagerFunctions.getBalance(sender.getUniqueId(), wagerChannel) < wager) {
                sender.sendMessage(Colors.get("You do not have sufficient funds for that wager."));
                return;
            }

            if (WagerFunctions.getBalance(target.getPlayer().getUniqueId(), wagerChannel) < wager) {
                sender.sendMessage(Colors.get("Your opponent does not have sufficient funds for that wager."));
                return;
            }
        }

        Match match = new Match(UUID.randomUUID(), kit, wager, size,
                duelsChannel, wagerChannel, duelServer) {

            @Override
            public void executeWagers(UUID winner, UUID loser) {
                WagerFunctions.postWager(winner, loser, getWager(), wagerChannel);
            }

            @Override
            public void onStart() {
                this.setState(MatchState.STARTING);
                taskScheduler.runTaskTimer(new MatchTask(this), 0, 1);
            }

            @Override
            public void onEnd() {
                taskScheduler.runTaskDelay(3, this::cleanup);
                state.removeMatch(this.getId());
            }
        };

        match.duelInitiated(sender, server);
        state.addMatch(match);

        Component text = Component.text(
                sender.getUsername() + " has requested to duel you (Kit: "
                        + kit.getName() + " / Wager: $" + wager + " (" + server.getServerInfo().getName()
                        + ") / Teammates: " + size + ") [Click to accept]")
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(
                "Click this message to accept the duel")))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptduel " + match.getId()));

        target.getPlayer().sendMessage(text);

        sender.sendMessage(Colors.get("Sent duel request to " + target.getPlayer().getUsername() + "  (Kit: "
                + kit.getName() + " / Wager: $" + wager + " / Teammates: " + size + ")."));
    }
}

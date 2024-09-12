package com.bongbong.mineage.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.bongbong.mineage.kit.KitType;
import com.bongbong.mineage.match.Match;
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

    @Default
    @Syntax("<target> <nodebuff/gapple> [wager] [# of teammates]")
    @CommandCompletion("@players")
    public void execute(Player sender, Player target, String rawKit, @Default("0") int wager, @Default("0") int size) {

        if (!target.isOnline()) {
            sender.sendMessage("Target must be online");
            return;
        }

        KitType kit = KitType.getKitByName(rawKit.toUpperCase());

        if (kit == null) {
            sender.sendMessage("Please select kits: nodebuff or gapple");
            return;
        }

        // logic for whether both parties have sufficient funds for wager

        Match match = new Match(kit, wager, size);
        match.duelInitiated(sender);
        state.addMatch(match);


        TextComponent text = new TextComponent(sender.getDisplayName() + " has requested to duel you (Kit: "
                + kit.getName() + " / Wager: $" + wager + " / Teammates: " + size + ") [Click to accept]");

        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                "Click this message to accept the duel").create()));

        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptduel " + match.getId()));
        target.spigot().sendMessage(text);

        sender.sendMessage("Sent duel request to " + target.getDisplayName() + "  (Kit: "
                + kit.getName() + " / Wager: $" + wager + " / Teammates: " + size + ").");
    }
}

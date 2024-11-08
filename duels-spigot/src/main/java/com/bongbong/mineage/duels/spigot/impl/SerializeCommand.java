package com.bongbong.mineage.duels.spigot.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import lombok.RequiredArgsConstructor;
import com.bongbong.mineage.duels.spigot.utils.InventoryUtil;
import org.bukkit.entity.Player;

@CommandAlias("serialize")
@RequiredArgsConstructor
public class SerializeCommand extends BaseCommand {

    @Default
    public void execute(Player sender) {
        System.out.println(InventoryUtil.serializeInventory(sender.getInventory().getContents()));
    }

}
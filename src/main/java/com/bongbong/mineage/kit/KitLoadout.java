package com.bongbong.mineage.kit;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class KitLoadout {
    private String name = "Default";
    private ItemStack[] armor;
    private ItemStack[] contents;

    public KitLoadout() {
        this.armor = new ItemStack[4];
        this.contents = new ItemStack[36];
    }

    public KitLoadout(String name) {
        this.name = name;
        this.armor = new ItemStack[4];
        this.contents = new ItemStack[36];
    }

}

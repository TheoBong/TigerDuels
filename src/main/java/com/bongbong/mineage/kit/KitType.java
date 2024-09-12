package com.bongbong.mineage.kit;

import com.bongbong.mineage.utils.InventoryUtil;
import com.bongbong.mineage.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@Getter
public enum KitType {
    NODEBUFF("NODEBUFF", new ItemBuilder(Material.POTION, 16421).flags(ItemFlag.HIDE_ATTRIBUTES).build()),
    GAPPLE("GAPPLE", new ItemBuilder(Material.GOLDEN_APPLE, 1).build());

    private final KitLoadout kitLoadout;
    final String name;
    final ItemStack icon;

    KitType(String name, ItemStack icon) {
        this.kitLoadout = new KitLoadout();
        this.name = name;
        this.icon = icon;
    }

    public static void init() {
        NODEBUFF.getKitLoadout().setArmor(InventoryUtil.deserializeInvetory("t@DIAMOND_BOOTS:d@0:a@1:e@0@3:u@1;t@DIAMOND_LEGGINGS:d@0:a@1:e@0@3:u@1;t@DIAMOND_CHESTPLATE:d@0:a@1:e@0@3:u@1;t@DIAMOND_HELMET:d@0:a@1:e@0@3:u@1;"));
        NODEBUFF.getKitLoadout().setContents(InventoryUtil.deserializeInvetory("t@DIAMOND_SWORD:d@0:a@1:e@16@2:e@20@2:u@1;t@ENDER_PEARL:d@0:a@16;t@POTION:d@35:a@1:pd@3-2=12-9600-0;t@POTION:d@34:a@1:pd@2-2=1-1800-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@COOKED_BEEF:d@0:a@64;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@34:a@1:pd@2-2=1-1800-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@34:a@1:pd@2-2=1-1800-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@34:a@1:pd@2-2=1-1800-1;"));

        GAPPLE.getKitLoadout().setArmor(InventoryUtil.deserializeInvetory("t@DIAMOND_BOOTS:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_LEGGINGS:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_CHESTPLATE:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_HELMET:d@0:a@1:e@0@4:e@34@3:u@0;"));
        GAPPLE.getKitLoadout().setContents(InventoryUtil.deserializeInvetory("t@DIAMOND_SWORD:d@0:a@1:e@16@5:e@34@3:e@20@2:u@0;t@ENDER_PEARL:d@0:a@16;t@DIAMOND_HELMET:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_CHESTPLATE:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_LEGGINGS:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_BOOTS:d@0:a@1:e@0@4:e@34@3:u@0;t@POTION:d@8265:a@1:pd@9-1=5-9600-0;t@POTION:d@8226:a@1:pd@2-2=1-1800-1;t@GOLDEN_APPLE:d@1:a@64;null;null;null;null;null;null;null;null;t@POTION:d@8226:a@1:pd@2-2=1-1800-1;null;null;null;null;null;null;null;null;t@POTION:d@8226:a@1:pd@2-2=1-1800-1;null;null;null;null;null;null;null;null;t@POTION:d@8226:a@1:pd@2-2=1-1800-1;"));

    }

    public static KitType getKitByName(String name) {
        for (KitType type : KitType.values())
            if (type.getName().equals(name)) return type;

        return null;
    }

}
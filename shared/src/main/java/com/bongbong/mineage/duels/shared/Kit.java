package com.bongbong.mineage.duels.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


public class Kit {
    private static final String NODEBUFF_NAME = "nodebuff";
    private static final String NODEBUFF_ARMOR = "t@DIAMOND_BOOTS:d@0:a@1:e@0@3:u@1;t@DIAMOND_LEGGINGS:d@0:a@1:e@0@3:u@1;t@DIAMOND_CHESTPLATE:d@0:a@1:e@0@3:u@1;t@DIAMOND_HELMET:d@0:a@1:e@0@3:u@1;";
    private static final String NODEBUFF_INVENTORY = "t@DIAMOND_SWORD:d@0:a@1:e@16@2:e@20@2:u@1;t@ENDER_PEARL:d@0:a@16;t@POTION:d@35:a@1:pd@3-2=12-9600-0;t@POTION:d@34:a@1:pd@2-2=1-1800-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@COOKED_BEEF:d@0:a@64;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@34:a@1:pd@2-2=1-1800-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@34:a@1:pd@2-2=1-1800-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@16421:a@1:pd@5-2=6-1-1;t@POTION:d@34:a@1:pd@2-2=1-1800-1;";

    private static final String GAPPLE_NAME = "gapple";
    private static final String GAPPLE_ARMOR = "t@DIAMOND_BOOTS:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_LEGGINGS:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_CHESTPLATE:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_HELMET:d@0:a@1:e@0@4:e@34@3:u@0;";
    private static final String GAPPLE_INVENTORY = "t@DIAMOND_SWORD:d@0:a@1:e@16@5:e@34@3:e@20@2:u@0;t@ENDER_PEARL:d@0:a@16;t@DIAMOND_HELMET:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_CHESTPLATE:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_LEGGINGS:d@0:a@1:e@0@4:e@34@3:u@0;t@DIAMOND_BOOTS:d@0:a@1:e@0@4:e@34@3:u@0;t@POTION:d@8265:a@1:pd@9-1=5-9600-0;t@POTION:d@8226:a@1:pd@2-2=1-1800-1;t@GOLDEN_APPLE:d@1:a@64;null;null;null;null;null;null;null;null;t@POTION:d@8226:a@1:pd@2-2=1-1800-1;null;null;null;null;null;null;null;null;t@POTION:d@8226:a@1:pd@2-2=1-1800-1;null;null;null;null;null;null;null;null;t@POTION:d@8226:a@1:pd@2-2=1-1800-1;";

    @Getter
    @RequiredArgsConstructor
    public enum KitType {
        NODEBUFF(NODEBUFF_NAME, NODEBUFF_ARMOR, NODEBUFF_INVENTORY),
        GAPPLE(GAPPLE_NAME, GAPPLE_ARMOR, GAPPLE_INVENTORY);

        private final String name, armor, inventory;

        public static KitType getKitByName(String name) {
            for (KitType type : KitType.values())
                if (type.getName().equals(name)) return type;

            return null;
        }
    }


}



package com.bongbong.mineage.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class InventoryUtil {
    private InventoryUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String serializeInventory(final ItemStack[] source) {
        final StringBuilder out = new StringBuilder();

        Arrays.stream(source).forEach(i -> out.append(serializeItemStack(i)).append(";"));

        return out.toString();
    }

    public static ItemStack[] deserializeInvetory(final String in) {
        final List<ItemStack> items = new ArrayList<>();
        final String[] split = in.split(";");

        Arrays.stream(split).forEach(i -> items.add(deserializeItemStack(i)));

        return items.toArray(new ItemStack[items.size()]);
    }

    public static String serializeItemStack(final ItemStack item) {
        final StringBuilder out = new StringBuilder();

        if (item == null) return "null";

        out.append("t@").append(item.getType().toString());
        out.append(":d@").append(item.getDurability());
        out.append(":a@").append(item.getAmount());

        final Map<Enchantment, Integer> enchs = item.getEnchantments();
        if (!enchs.isEmpty()) enchs.forEach((e, i) -> out.append(":e@").append(e.getId()).append("@").append(i));

        if (item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();

            if (meta.hasDisplayName()) {
                out.append(":dn@").append(meta.getDisplayName());
            }

            if (meta.hasLore()) {
                out.append(":l@").append(meta.getLore());
            }

            out.append(":u@").append(meta.spigot().isUnbreakable() ? 1 : 0);
        }

        if (item.getType() == Material.POTION) {
            final Potion potion = Potion.fromItemStack(item);

            out.append(":pd@")
                    .append(potion.getType().getDamageValue())
                    .append("-")
                    .append(potion.getLevel()
                    );

            potion.getEffects().forEach(eff -> out.append("=")
                    .append(eff.getType().getId())
                    .append("-")
                    .append(eff.getDuration())
                    .append("-")
                    .append(eff.getAmplifier())
            );
        }

        return out.toString();
    }

    public static ItemStack deserializeItemStack(final String in) {
        ItemStack item = null;
        ItemMeta meta = null;

        if (in.equals("null")) {
            return new ItemStack(Material.AIR);
        }

        final String[] tags = in.split(":");

        for (final String tag : tags) {
            final String[] tagAtt = tag.split("@");
            final String tagId = tagAtt[0];

            switch (tagId) {
                case "t": {
                    item = new ItemStack(Material.getMaterial(tagAtt[1]));
                    meta = item.getItemMeta();
                    break;
                }
                case "d": {
                    if (item != null) {
                        item.setDurability(Short.parseShort(tagAtt[1]));
                        break;
                    }
                    break;
                }
                case "a": {
                    if (item != null) {
                        item.setAmount(Integer.parseInt(tagAtt[1]));
                        break;
                    }
                    break;
                }
                case "e": {
                    if (meta != null) {
                        meta.addEnchant(
                                Enchantment.getById(Integer.parseInt(tagAtt[1])),
                                Integer.parseInt(tagAtt[2]),
                                true
                        );
                        break;
                    }
                    break;
                }
                case "dn": {
                    if (meta != null) {
                        meta.setDisplayName(tagAtt[1]);
                        break;
                    }
                    break;
                }
                case "l": {
                    tagAtt[1] = tagAtt[1].replace("[", "");
                    tagAtt[1] = tagAtt[1].replace("]", "");
                    final List<String> lore = Arrays.asList(tagAtt[1].split(","));

                    for (int x = 0; x < lore.size(); ++x) {
                        String s = lore.get(x);

                        if (s != null) {
                            if (s.toCharArray().length != 0) {
                                if (s.charAt(0) == ' ') {
                                    s = s.replaceFirst(" ", "");
                                }

                                lore.set(x, s);
                            }
                        }
                    }

                    if (meta != null) {
                        meta.setLore(lore);
                        break;
                    }
                    break;
                }
                case "u": {
                    if (item != null) {
                        meta.spigot().setUnbreakable(tagAtt[1].equals("1"));
                        break;

                    }
                    break;
                }
                case "pd": {
                    if (item != null && item.getType().equals(Material.POTION)) {
                        final String[] effList = tagAtt[1].split("=");
                        final String[] potData = effList[0].split("-");

                        final Potion potion = new Potion(
                                PotionType.getByDamageValue(Integer.parseInt(potData[0])),
                                Integer.parseInt(potData[1])
                        );
                        potion.setSplash(item.getDurability() >= 16000);

                        final PotionMeta potMeta = (PotionMeta) item.getItemMeta();

                        for (int i = 1; i < effList.length; ++i) {
                            final String[] effData = effList[1].split("-");

                            final PotionEffect potEffect = new PotionEffect(
                                    PotionEffectType.getById(Integer.parseInt(effData[0])),
                                    Integer.parseInt(effData[1]),
                                    Integer.parseInt(effData[2]),
                                    false
                            );


                            potMeta.addCustomEffect(potEffect, true);
                        }

                        item = potion.toItemStack(item.getAmount());
                        item.setItemMeta(potMeta);

                        break;
                    }

                    break;
                }
            }
        }

        if (meta != null) {
            item.setItemMeta(meta);
        }

        return item;
    }
}

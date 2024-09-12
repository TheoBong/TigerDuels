package com.bongbong.mineage.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ItemBuilder {
    private final ItemStack item;

    private ItemBuilder(final ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(final Material material, final int damage) {
        this(new ItemStack(material, 1, (short) damage));
    }

    public ItemBuilder(final Material material) {
        this(new ItemStack(material, 1));
    }

    public static ItemBuilder from(final ItemStack item) {
        return new ItemBuilder(item);
    }

    public ItemBuilder amount(final int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder durability(final int durability) {
        this.item.setDurability((short) durability);
        return this;
    }

    public ItemBuilder name(final String name) {
        final ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(name);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final String... lore) {
        final ItemMeta meta = this.item.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        this.item.setItemMeta(meta);
        return this;
    }

    /**
     * Adds a line to the lore of this builder at a specific position.
     *
     * @param line the line to add
     * @return this instance
     */
    public ItemBuilder loreLine(final String line) {
        final ItemMeta meta = this.item.getItemMeta();

        final boolean hasLore = meta.hasLore();
        final List<String> lore = hasLore ? meta.getLore() : new ArrayList<>();
        lore.add(hasLore ? lore.size() : 0, line);

        meta.setLore(lore);

        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder flags(final ItemFlag... flags) {
        final ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(flags);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder enchant(final Enchantment enchantment, final int level) {
        this.item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder skullOwner(final String playerName) {
        if (this.item.getType() != Material.SKULL_ITEM) {
            throw new IllegalStateException("Non-skull items can't have a skull owner");
        }

        final SkullMeta meta = (SkullMeta) this.item.getItemMeta();
        meta.setOwner(playerName);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder color(final Color color) {
        if (Stream.of(Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS).anyMatch(material -> this.item.getType() == material)) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) this.item.getItemMeta();
            meta.setColor(color);
            this.item.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    public ItemBuilder unbreakable(final boolean unbreakable) {
        final ItemMeta meta = this.item.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return this.item;
    }
}

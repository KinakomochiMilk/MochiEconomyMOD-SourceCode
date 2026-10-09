package jp.kinakomochi.mochieconomy.config;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ShopManager {

    private static final List<ShopEntry> ENTRIES =
            new ArrayList<>();

    private ShopManager() {
    }

    public static void load() {

        ENTRIES.clear();

        ShopConfig config =
                MochiConfigManager.getShopConfig();

        if (config == null) {
            return;
        }

        for (ShopEntry entry :
                config.getEnabledEntries()) {

            if (createItemStack(entry) == null) {
                System.err.println(
                        "[MochiEconomy] "
                                + "Skipping invalid shop item: "
                                + entry.getId()
                );

                continue;
            }

            ENTRIES.add(entry);
        }

        System.out.println(
                "[MochiEconomy] Loaded "
                        + ENTRIES.size()
                        + " shop entries."
        );
    }

    public static List<ShopEntry> getEntries() {
        return Collections.unmodifiableList(
                ENTRIES
        );
    }

    public static ShopEntry getEntry(
            String id
    ) {
        if (id == null) {
            return null;
        }

        for (ShopEntry entry : ENTRIES) {

            if (id.equals(entry.getId())) {
                return entry;
            }
        }

        return null;
    }

    public static ItemStack createItemStack(
            ShopEntry entry
    ) {
        if (entry == null) {
            return null;
        }

        ResourceLocation location =
                new ResourceLocation(
                        entry.getItemRegistryName()
                );

        Item item =
                Item.REGISTRY.getObject(
                        location
                );

        if (item == null) {
            System.err.println(
                    "[MochiEconomy] "
                            + "Unknown item: "
                            + entry.getItemRegistryName()
            );

            return null;
        }

        return new ItemStack(
                item,
                entry.getAmount()
        );
    }
}
package jp.kinakomochi.mochieconomy.config;

import com.moandjiezana.toml.Toml;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ShopConfigGenerator {

    private ShopConfigGenerator() {
    }

    /**
     * shop.tomlに存在しないItemを
     * 仮エントリとして追加する。
     *
     * 既存エントリは一切変更しない。
     */
    public static void generateMissingEntries(
            File file
    ) {
        if (file == null) {
            return;
        }

        if (!file.isFile()) {
            return;
        }

        try {

            Toml toml =
                    new Toml().read(file);

            List<Toml> shops =
                    toml.getTables("shop");

            Set<String> existingItems =
                    new HashSet<>();

            if (shops != null) {

                for (Toml shop : shops) {

                    String item =
                            shop.getString("item");

                    if (item != null
                            && !item.trim().isEmpty()) {

                        existingItems.add(
                                item
                        );
                    }
                }
            }

            int added = 0;

            try (
                    PrintWriter writer =
                            new PrintWriter(
                                    new FileWriter(
                                            file,
                                            true
                                    )
                            )
            ) {

                for (ResourceLocation location :
                        Item.REGISTRY.getKeys()) {

                    if (location == null) {
                        continue;
                    }

                    String registryName =
                            location.toString();

                    if (existingItems.contains(
                            registryName
                    )) {
                        continue;
                    }

                    Item item =
                            Item.REGISTRY.getObject(
                                    location
                            );

                    if (item == null) {
                        continue;
                    }

                    String id =
                            createId(
                                    registryName
                            );

                    writer.println();
                    writer.println(
                            "[[shop]]"
                    );
                    writer.println(
                            "id = \""
                                    + escape(id)
                                    + "\""
                    );
                    writer.println(
                            "item = \""
                                    + escape(
                                    registryName
                            )
                                    + "\""
                    );
                    writer.println(
                            "amount = 1"
                    );
                    writer.println(
                            "price = 0"
                    );
                    writer.println(
                            "enabled = false"
                    );

                    existingItems.add(
                            registryName
                    );

                    added++;
                }
            }

            System.out.println(
                    "[MochiEconomy] "
                            + "Shop config generation complete. "
                            + "Added "
                            + added
                            + " new entries."
            );

        } catch (Exception e) {

            System.err.println(
                    "[MochiEconomy] "
                            + "Failed to generate shop.toml."
            );

            e.printStackTrace();
        }
    }

    private static String createId(
            String registryName
    ) {
        return registryName;
    }

    private static String escape(
            String value
    ) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
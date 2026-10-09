package jp.kinakomochi.mochieconomy.config;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ShopConfig {

    private final File file;

    private final List<ShopEntry> entries =
            new ArrayList<>();

    public ShopConfig(File file) {
        this.file = file;
    }

    public void load() {

        entries.clear();

        if (!file.exists()) {
            createDefaultFile();
        }

        try {
            Toml toml =
                    new Toml().read(file);

            List<Toml> shops =
                    toml.getTables(
                            "shop"
                    );

            if (shops == null) {
                return;
            }

            for (Toml shop : shops) {

                String id =
                        shop.getString(
                                "id"
                        );

                String item =
                        shop.getString(
                                "item"
                        );

                Long amount =
                        shop.getLong(
                                "amount"
                        );

                Long price =
                        shop.getLong(
                                "price"
                        );

                Boolean enabled =
                        shop.getBoolean(
                                "enabled"
                        );

                if (id == null
                        || item == null
                        || amount == null
                        || price == null) {

                    System.err.println(
                            "[MochiEconomy] "
                                    + "Invalid shop entry."
                    );

                    continue;
                }

                if (enabled == null) {
                    enabled = true;
                }

                if (amount <= 0L) {
                    System.err.println(
                            "[MochiEconomy] "
                                    + "Invalid amount: "
                                    + id
                    );

                    continue;
                }

                if (price < 0L) {
                    System.err.println(
                            "[MochiEconomy] "
                                    + "Invalid price: "
                                    + id
                    );

                    continue;
                }

                if (amount > Integer.MAX_VALUE) {
                    System.err.println(
                            "[MochiEconomy] "
                                    + "Amount is too large: "
                                    + id
                    );

                    continue;
                }

                entries.add(
                        new ShopEntry(
                                id,
                                item,
                                amount.intValue(),
                                price,
                                enabled
                        )
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "[MochiEconomy] "
                            + "Failed to load shop.toml"
            );

            e.printStackTrace();
        }
    }

    private void createDefaultFile() {

        File parent =
                file.getParentFile();

        if (parent != null
                && !parent.exists()) {

            parent.mkdirs();
        }

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(
                                        file
                                )
                        )
        ) {

            writer.println(
                    "# Kinakomochi Economy"
            );

            writer.println(
                    "# Shop configuration"
            );

            writer.println();

            writer.println(
                    "[[shop]]"
            );

            writer.println(
                    "id = \"diamond\""
            );

            writer.println(
                    "item = \"minecraft:diamond\""
            );

            writer.println(
                    "amount = 1"
            );

            writer.println(
                    "price = 100"
            );

            writer.println(
                    "enabled = true"
            );

            writer.println();

            writer.println(
                    "[[shop]]"
            );

            writer.println(
                    "id = \"iron_ingot\""
            );

            writer.println(
                    "item = \"minecraft:iron_ingot\""
            );

            writer.println(
                    "amount = 8"
            );

            writer.println(
                    "price = 50"
            );

            writer.println(
                    "enabled = true"
            );

        } catch (IOException e) {

            System.err.println(
                    "[MochiEconomy] "
                            + "Failed to create shop.toml"
            );

            e.printStackTrace();
        }
    }

    public List<ShopEntry> getEntries() {

        return Collections.unmodifiableList(
                entries
        );
    }

    public List<ShopEntry> getEnabledEntries() {

        List<ShopEntry> result =
                new ArrayList<>();

        for (ShopEntry entry :
                entries) {

            if (entry.isEnabled()) {
                result.add(entry);
            }
        }

        return result;
    }
}
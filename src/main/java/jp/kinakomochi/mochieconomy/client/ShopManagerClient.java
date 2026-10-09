package jp.kinakomochi.mochieconomy.client;

import jp.kinakomochi.mochieconomy.network.message.MessageShopData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ShopManagerClient {

    private static final List<
            MessageShopData.ShopEntryData
            > ENTRIES =
            new ArrayList<>();

    private ShopManagerClient() {
    }

    public static void setEntries(
            List<
                    MessageShopData.ShopEntryData
                    > entries
    ) {
        ENTRIES.clear();

        if (entries == null) {
            return;
        }

        ENTRIES.addAll(
                entries
        );

        System.out.println(
                "[MochiEconomy] Received "
                        + ENTRIES.size()
                        + " shop entries."
        );
    }

    public static List<
            MessageShopData.ShopEntryData
            > getEntries() {

        return Collections.unmodifiableList(
                ENTRIES
        );
    }

    public static void clear() {
        ENTRIES.clear();
    }
}
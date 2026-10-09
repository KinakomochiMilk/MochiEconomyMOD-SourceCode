package jp.kinakomochi.mochieconomy.network.message;

import io.netty.buffer.ByteBuf;

import jp.kinakomochi.mochieconomy.config.ShopEntry;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MessageShopData
        implements IMessage {

    private static final int MAX_ENTRIES = 1024;
    private static final int MAX_STRING_LENGTH = 4096;

    private final List<ShopEntryData> entries =
            new ArrayList<>();

    public MessageShopData() {
    }

    public MessageShopData(
            List<ShopEntry> source
    ) {
        if (source == null) {
            return;
        }

        for (ShopEntry entry : source) {
            if (entry == null) {
                continue;
            }

            entries.add(
                    new ShopEntryData(
                            entry.getId(),
                            entry.getItemRegistryName(),
                            entry.getAmount(),
                            entry.getPrice()
                    )
            );
        }
    }

    @Override
    public void fromBytes(
            ByteBuf buf
    ) {
        entries.clear();

        int size = buf.readInt();

        if (size < 0 || size > MAX_ENTRIES) {
            return;
        }

        for (int i = 0; i < size; i++) {
            String id = readString(buf);
            String item = readString(buf);

            if (id == null || item == null) {
                return;
            }

            int amount = buf.readInt();
            long price = buf.readLong();

            entries.add(
                    new ShopEntryData(
                            id,
                            item,
                            amount,
                            price
                    )
            );
        }
    }

    @Override
    public void toBytes(
            ByteBuf buf
    ) {
        buf.writeInt(
                entries.size()
        );

        for (ShopEntryData entry : entries) {
            writeString(
                    buf,
                    entry.getId()
            );

            writeString(
                    buf,
                    entry.getItem()
            );

            buf.writeInt(
                    entry.getAmount()
            );

            buf.writeLong(
                    entry.getPrice()
            );
        }
    }

    private static void writeString(
            ByteBuf buf,
            String value
    ) {
        if (value == null) {
            value = "";
        }

        byte[] bytes =
                value.getBytes(
                        StandardCharsets.UTF_8
                );

        if (bytes.length > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException(
                    "Shop data string is too long."
            );
        }

        buf.writeInt(
                bytes.length
        );

        buf.writeBytes(
                bytes
        );
    }

    private static String readString(
            ByteBuf buf
    ) {
        int length =
                buf.readInt();

        if (length < 0
                || length > MAX_STRING_LENGTH
                || length > buf.readableBytes()) {
            return null;
        }

        byte[] bytes =
                new byte[length];

        buf.readBytes(
                bytes
        );

        return new String(
                bytes,
                StandardCharsets.UTF_8
        );
    }

    public List<ShopEntryData> getEntries() {
        return entries;
    }

    public static class ShopEntryData {

        private final String id;
        private final String item;
        private final int amount;
        private final long price;

        public ShopEntryData(
                String id,
                String item,
                int amount,
                long price
        ) {
            this.id = id;
            this.item = item;
            this.amount = amount;
            this.price = price;
        }

        public String getId() {
            return id;
        }

        public String getItem() {
            return item;
        }

        public int getAmount() {
            return amount;
        }

        public long getPrice() {
            return price;
        }
    }
}
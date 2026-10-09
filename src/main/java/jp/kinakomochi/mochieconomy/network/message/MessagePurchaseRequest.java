package jp.kinakomochi.mochieconomy.network.message;

import io.netty.buffer.ByteBuf;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.nio.charset.StandardCharsets;

public class MessagePurchaseRequest
        implements IMessage {

    private static final int MAX_ID_LENGTH = 256;

    private String shopId;

    public MessagePurchaseRequest() {
    }

    public MessagePurchaseRequest(
            String shopId
    ) {
        this.shopId = shopId;
    }

    @Override
    public void fromBytes(
            ByteBuf buf
    ) {
        int length =
                buf.readInt();

        if (length < 0
                || length > MAX_ID_LENGTH
                || length > buf.readableBytes()) {

            shopId = null;

            return;
        }

        byte[] bytes =
                new byte[length];

        buf.readBytes(
                bytes
        );

        shopId =
                new String(
                        bytes,
                        StandardCharsets.UTF_8
                );
    }

    @Override
    public void toBytes(
            ByteBuf buf
    ) {
        if (shopId == null) {
            buf.writeInt(0);
            return;
        }

        byte[] bytes =
                shopId.getBytes(
                        StandardCharsets.UTF_8
                );

        if (bytes.length > MAX_ID_LENGTH) {
            throw new IllegalArgumentException(
                    "Shop ID is too long."
            );
        }

        buf.writeInt(
                bytes.length
        );

        buf.writeBytes(
                bytes
        );
    }

    public String getShopId() {
        return shopId;
    }
}
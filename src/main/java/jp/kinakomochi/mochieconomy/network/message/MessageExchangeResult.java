package jp.kinakomochi.mochieconomy.network.message;

import io.netty.buffer.ByteBuf;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.nio.charset.StandardCharsets;

public class MessageExchangeResult
        implements IMessage {

    private static final int MAX_MESSAGE_LENGTH = 1024;

    private boolean success;

    private String message;

    private long balance;

    public MessageExchangeResult() {
    }

    public MessageExchangeResult(
            boolean success,
            String message,
            long balance
    ) {
        this.success = success;
        this.message = message;
        this.balance = balance;
    }

    @Override
    public void fromBytes(
            ByteBuf buf
    ) {
        success =
                buf.readBoolean();

        balance =
                buf.readLong();

        int length =
                buf.readInt();

        if (length < 0
                || length > MAX_MESSAGE_LENGTH
                || length > buf.readableBytes()) {

            message = "";

            return;
        }

        byte[] bytes =
                new byte[length];

        buf.readBytes(
                bytes
        );

        message =
                new String(
                        bytes,
                        StandardCharsets.UTF_8
                );
    }

    @Override
    public void toBytes(
            ByteBuf buf
    ) {
        buf.writeBoolean(
                success
        );

        buf.writeLong(
                balance
        );

        String value =
                message == null
                        ? ""
                        : message;

        byte[] bytes =
                value.getBytes(
                        StandardCharsets.UTF_8
                );

        if (bytes.length > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException(
                    "Exchange result message is too long."
            );
        }

        buf.writeInt(
                bytes.length
        );

        buf.writeBytes(
                bytes
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public long getBalance() {
        return balance;
    }
}
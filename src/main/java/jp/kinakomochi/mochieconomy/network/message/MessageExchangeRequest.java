package jp.kinakomochi.mochieconomy.network.message;

import io.netty.buffer.ByteBuf;

import jp.kinakomochi.mochieconomy.item.MochiCoinType;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageExchangeRequest
        implements IMessage {

    private MochiCoinType fromType;

    private MochiCoinType toType;

    private long amount;

    public MessageExchangeRequest() {
    }

    public MessageExchangeRequest(
            MochiCoinType fromType,
            MochiCoinType toType,
            long amount
    ) {
        this.fromType = fromType;
        this.toType = toType;
        this.amount = amount;
    }

    @Override
    public void fromBytes(
            ByteBuf buf
    ) {
        int fromOrdinal =
                buf.readInt();

        int toOrdinal =
                buf.readInt();

        amount =
                buf.readLong();

        MochiCoinType[] types =
                MochiCoinType.values();

        if (fromOrdinal >= 0
                && fromOrdinal < types.length) {

            fromType =
                    types[fromOrdinal];
        }

        if (toOrdinal >= 0
                && toOrdinal < types.length) {

            toType =
                    types[toOrdinal];
        }
    }

    @Override
    public void toBytes(
            ByteBuf buf
    ) {
        buf.writeInt(
                fromType == null
                        ? -1
                        : fromType.ordinal()
        );

        buf.writeInt(
                toType == null
                        ? -1
                        : toType.ordinal()
        );

        buf.writeLong(
                amount
        );
    }

    public MochiCoinType getFromType() {
        return fromType;
    }

    public MochiCoinType getToType() {
        return toType;
    }

    public long getAmount() {
        return amount;
    }
}
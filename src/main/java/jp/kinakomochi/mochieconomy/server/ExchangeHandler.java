package jp.kinakomochi.mochieconomy.server;

import jp.kinakomochi.mochieconomy.api.MochiEconomyAPI;
import jp.kinakomochi.mochieconomy.api.MochiExchangeAPI;
import jp.kinakomochi.mochieconomy.item.MochiCoinType;
import jp.kinakomochi.mochieconomy.network.MochiNetwork;
import jp.kinakomochi.mochieconomy.network.message.MessageExchangeRequest;
import jp.kinakomochi.mochieconomy.network.message.MessageExchangeResult;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ExchangeHandler
        implements IMessageHandler<
        MessageExchangeRequest,
        IMessage> {

    @Override
    public IMessage onMessage(
            MessageExchangeRequest message,
            MessageContext context
    ) {
        EntityPlayerMP player =
                context.getServerHandler()
                        .player;

        player.getServerWorld()
                .addScheduledTask(
                        () -> processExchange(
                                player,
                                message
                        )
                );

        return null;
    }

    private static void processExchange(
            EntityPlayerMP player,
            MessageExchangeRequest message
    ) {
        if (message == null) {

            sendResult(
                    player,
                    false,
                    "両替要求が正しくありません。"
            );

            return;
        }

        MochiCoinType fromType =
                message.getFromType();

        MochiCoinType toType =
                message.getToType();

        long amount =
                message.getAmount();

        if (fromType == null
                || toType == null) {

            sendResult(
                    player,
                    false,
                    "両替するコインの種類が正しくありません。"
            );

            return;
        }

        if (fromType == toType) {

            sendResult(
                    player,
                    false,
                    "同じ種類のコインには両替できません。"
            );

            return;
        }

        if (amount <= 0L) {

            sendResult(
                    player,
                    false,
                    "両替する数量が正しくありません。"
            );

            return;
        }

        long totalValue;

        try {
            totalValue =
                    Math.multiplyExact(
                            fromType.getValue(),
                            amount
                    );
        } catch (ArithmeticException e) {

            sendResult(
                    player,
                    false,
                    "数量が大きすぎます。"
            );

            return;
        }

        if (totalValue
                % toType.getValue() != 0L) {

            sendResult(
                    player,
                    false,
                    "その数量では両替できません。"
            );

            return;
        }

        long targetAmount =
                totalValue
                        / toType.getValue();

        if (targetAmount <= 0L) {

            sendResult(
                    player,
                    false,
                    "両替数量が正しくありません。"
            );

            return;
        }

        long available =
                MochiExchangeAPI.countCoins(
                        player,
                        fromType
                );

        if (available < amount) {

            sendResult(
                    player,
                    false,
                    "両替元のコインが足りません。"
            );

            return;
        }

        boolean success =
                MochiExchangeAPI.exchange(
                        player,
                        fromType,
                        toType,
                        amount
                );

        if (!success) {

            sendResult(
                    player,
                    false,
                    "両替に失敗しました。"
            );

            return;
        }

        sendResult(
                player,
                true,
                "両替しました。"
        );
    }

    private static void sendResult(
            EntityPlayerMP player,
            boolean success,
            String message
    ) {
        long balance =
                MochiEconomyAPI.getBalance(
                        player
                );

        MochiNetwork.INSTANCE.sendTo(
                new MessageExchangeResult(
                        success,
                        message,
                        balance
                ),
                player
        );
    }
}
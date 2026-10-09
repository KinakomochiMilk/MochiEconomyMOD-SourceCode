package jp.kinakomochi.mochieconomy.network;

import jp.kinakomochi.mochieconomy.MochiEconomy;

import jp.kinakomochi.mochieconomy.client.MessageExchangeResultClientHandler;
import jp.kinakomochi.mochieconomy.client.MessagePurchaseResultClientHandler;
import jp.kinakomochi.mochieconomy.client.MessageShopDataClientHandler;

import jp.kinakomochi.mochieconomy.network.message.MessageExchangeRequest;
import jp.kinakomochi.mochieconomy.network.message.MessageExchangeResult;
import jp.kinakomochi.mochieconomy.network.message.MessagePurchaseRequest;
import jp.kinakomochi.mochieconomy.network.message.MessagePurchaseResult;
import jp.kinakomochi.mochieconomy.network.message.MessageShopData;

import jp.kinakomochi.mochieconomy.server.ExchangeHandler;
import jp.kinakomochi.mochieconomy.server.PurchaseHandler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(
        modid = MochiEconomy.MODID
)
public final class MochiNetwork {

    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel(
                    "mochieconomy"
            );

    private static int discriminator = 0;

    private MochiNetwork() {
    }

    public static void register() {

        /*
         * ショップデータ
         *
         * Server -> Client
         */
        INSTANCE.registerMessage(
                MessageShopDataClientHandler.class,
                MessageShopData.class,
                discriminator++,
                Side.CLIENT
        );

        /*
         * ショップ購入要求
         *
         * Client -> Server
         */
        INSTANCE.registerMessage(
                PurchaseHandler.class,
                MessagePurchaseRequest.class,
                discriminator++,
                Side.SERVER
        );

        /*
         * ショップ購入結果
         *
         * Server -> Client
         */
        INSTANCE.registerMessage(
                MessagePurchaseResultClientHandler.class,
                MessagePurchaseResult.class,
                discriminator++,
                Side.CLIENT
        );

        /*
         * もちコイン両替要求
         *
         * Client -> Server
         */
        INSTANCE.registerMessage(
                ExchangeHandler.class,
                MessageExchangeRequest.class,
                discriminator++,
                Side.SERVER
        );

        /*
         * もちコイン両替結果
         *
         * Server -> Client
         */
        INSTANCE.registerMessage(
                MessageExchangeResultClientHandler.class,
                MessageExchangeResult.class,
                discriminator++,
                Side.CLIENT
        );
    }
}
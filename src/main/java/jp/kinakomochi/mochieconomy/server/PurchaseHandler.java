package jp.kinakomochi.mochieconomy.server;

import jp.kinakomochi.mochieconomy.api.MochiEconomyAPI;
import jp.kinakomochi.mochieconomy.config.ShopEntry;
import jp.kinakomochi.mochieconomy.config.ShopManager;
import jp.kinakomochi.mochieconomy.network.MochiNetwork;
import jp.kinakomochi.mochieconomy.network.message.MessagePurchaseRequest;
import jp.kinakomochi.mochieconomy.network.message.MessagePurchaseResult;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PurchaseHandler
        implements IMessageHandler<
        MessagePurchaseRequest,
        IMessage> {

    @Override
    public IMessage onMessage(
            MessagePurchaseRequest message,
            MessageContext context
    ) {
        EntityPlayerMP player =
                context.getServerHandler()
                        .player;

        player.getServerWorld()
                .addScheduledTask(
                        () -> processPurchase(
                                player,
                                message.getShopId()
                        )
                );

        return null;
    }

    private static void processPurchase(
            EntityPlayerMP player,
            String shopId
    ) {
        if (shopId == null
                || shopId.trim().isEmpty()) {

            sendResult(
                    player,
                    false,
                    "商品IDが正しくありません。"
            );

            return;
        }

        ShopEntry entry =
                ShopManager.getEntry(
                        shopId
                );

        if (entry == null) {

            sendResult(
                    player,
                    false,
                    "商品が存在しません。"
            );

            return;
        }

        if (!entry.isEnabled()) {

            sendResult(
                    player,
                    false,
                    "この商品は現在購入できません。"
            );

            return;
        }

        ItemStack product =
                ShopManager.createItemStack(
                        entry
                );

        if (product == null
                || product.isEmpty()) {

            sendResult(
                    player,
                    false,
                    "商品のデータが正しくありません。"
            );

            return;
        }

        long price =
                entry.getPrice();

        if (price < 0L) {

            sendResult(
                    player,
                    false,
                    "商品の価格が正しくありません。"
            );

            return;
        }

        if (!MochiEconomyAPI.has(
                player,
                price
        )) {

            sendResult(
                    player,
                    false,
                    "もちコインが足りません。"
            );

            return;
        }

        /*
         * 商品追加前のインベントリを保存する。
         */
        ItemStack[] inventoryBackup =
                MochiEconomyAPI
                        .createInventoryBackup(
                                player
                        );

        /*
         * 商品を完全にインベントリへ入れられるか確認する。
         */
        if (!MochiEconomyAPI.insertFully(
                player,
                product
        )) {

            MochiEconomyAPI.restoreInventory(
                    player,
                    inventoryBackup
            );

            sendResult(
                    player,
                    false,
                    "インベントリに空きがありません。"
            );

            return;
        }

        /*
         * 商品追加後に支払いを行う。
         */
        if (!MochiEconomyAPI.withdraw(
                player,
                price
        )) {

            MochiEconomyAPI.restoreInventory(
                    player,
                    inventoryBackup
            );

            sendResult(
                    player,
                    false,
                    "支払い処理に失敗しました。"
            );

            return;
        }

        sendResult(
                player,
                true,
                "購入しました。"
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
                new MessagePurchaseResult(
                        success,
                        message,
                        balance
                ),
                player
        );
    }
}
package jp.kinakomochi.mochieconomy.api;

import jp.kinakomochi.mochieconomy.item.ItemMochiCoinBase;
import jp.kinakomochi.mochieconomy.item.MochiCoinNBT;
import jp.kinakomochi.mochieconomy.item.MochiCoinType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class MochiExchangeAPI {

    private MochiExchangeAPI() {
    }

    /**
     * 指定した種類のもちコインを、
     * 別の種類のもちコインへ両替する。
     *
     * amount は「交換元コインの枚数」。
     *
     * 例:
     *
     * NORMAL -> COMPRESSED
     * amount = 64
     *
     * NORMAL 64枚
     * ↓
     * COMPRESSED 1枚
     */
    public static boolean exchange(
            EntityPlayer player,
            MochiCoinType fromType,
            MochiCoinType toType,
            long amount
    ) {
        if (player == null
                || fromType == null
                || toType == null
                || amount <= 0L) {
            return false;
        }

        if (fromType == toType) {
            return false;
        }

        /*
         * 交換元の価値。
         */
        long totalValue;

        try {
            totalValue =
                    Math.multiplyExact(
                            fromType.getValue(),
                            amount
                    );
        } catch (ArithmeticException e) {
            return false;
        }

        /*
         * 交換先の単位で割り切れない場合は
         * 両替できない。
         */
        if (totalValue
                % toType.getValue() != 0L) {
            return false;
        }

        long targetAmount =
                totalValue
                        / toType.getValue();

        if (targetAmount <= 0L) {
            return false;
        }

        /*
         * 交換元を実際に持っているか確認。
         */
        long available =
                countCoins(
                        player,
                        fromType
                );

        if (available < amount) {
            return false;
        }

        /*
         * 元のインベントリを保存。
         *
         * 交換先を入れられなかった場合に
         * 完全に元へ戻す。
         */
        ItemStack[] backup =
                MochiEconomyAPI
                        .createInventoryBackup(
                                player
                        );

        /*
         * 交換元を削除。
         */
        if (!removeCoins(
                player,
                fromType,
                amount
        )) {

            MochiEconomyAPI.restoreInventory(
                    player,
                    backup
            );

            return false;
        }

        /*
         * 交換先を生成。
         */
        ItemMochiCoinBase item =
                MochiCoinAPI.getItem(
                        toType
                );

        if (item == null) {

            MochiEconomyAPI.restoreInventory(
                    player,
                    backup
            );

            return false;
        }

        /*
         * 64個を超える場合は複数Stackに分ける。
         */
        long remaining =
                targetAmount;

        while (remaining > 0L) {

            int stackSize =
                    (int) Math.min(
                            remaining,
                            64L
                    );

            ItemStack stack =
                    item.createStack(
                            stackSize
                    );

            if (!MochiEconomyAPI.insertFully(
                    player,
                    stack
            )) {

                MochiEconomyAPI.restoreInventory(
                        player,
                        backup
                );

                return false;
            }

            remaining -= stackSize;
        }

        return true;
    }

    /**
     * 指定種類のコイン枚数を数える。
     */
    public static long countCoins(
            EntityPlayer player,
            MochiCoinType type
    ) {
        if (player == null
                || type == null) {
            return 0L;
        }

        InventoryPlayer inventory =
                player.inventory;

        long total = 0L;

        for (ItemStack stack :
                inventory.mainInventory) {

            total +=
                    countStack(
                            stack,
                            type
                    );
        }

        for (ItemStack stack :
                inventory.offHandInventory) {

            total +=
                    countStack(
                            stack,
                            type
                    );
        }

        return total;
    }

    private static long countStack(
            ItemStack stack,
            MochiCoinType type
    ) {
        if (stack == null
                || stack.isEmpty()) {
            return 0L;
        }

        if (!(stack.getItem()
                instanceof ItemMochiCoinBase)) {
            return 0L;
        }

        ItemMochiCoinBase item =
                (ItemMochiCoinBase)
                        stack.getItem();

        if (item.getCoinType()
                != type) {
            return 0L;
        }

        /*
         * NBTが壊れている場合でも
         * MochiCoinAPI経由で正規化する。
         */
        if (!MochiCoinAPI.isMochiCoin(
                stack
        )) {
            return 0L;
        }

        return stack.getCount();
    }

    /**
     * 指定種類のコインを指定枚数削除する。
     */
    private static boolean removeCoins(
            EntityPlayer player,
            MochiCoinType type,
            long amount
    ) {
        if (player == null
                || type == null
                || amount <= 0L) {
            return false;
        }

        long remaining =
                amount;

        InventoryPlayer inventory =
                player.inventory;

        /*
         * メインインベントリ。
         */
        for (int i = 0;
             i < inventory.mainInventory.size();
             i++) {

            if (remaining <= 0L) {
                break;
            }

            ItemStack stack =
                    inventory.mainInventory.get(
                            i
                    );

            if (!isCoinOfType(
                    stack,
                    type
            )) {
                continue;
            }

            int remove =
                    (int) Math.min(
                            remaining,
                            stack.getCount()
                    );

            stack.shrink(
                    remove
            );

            remaining -=
                    remove;

            if (stack.isEmpty()) {

                inventory.mainInventory.set(
                        i,
                        ItemStack.EMPTY
                );
            }
        }

        /*
         * オフハンド。
         */
        for (int i = 0;
             i < inventory.offHandInventory.size();
             i++) {

            if (remaining <= 0L) {
                break;
            }

            ItemStack stack =
                    inventory.offHandInventory.get(
                            i
                    );

            if (!isCoinOfType(
                    stack,
                    type
            )) {
                continue;
            }

            int remove =
                    (int) Math.min(
                            remaining,
                            stack.getCount()
                    );

            stack.shrink(
                    remove
            );

            remaining -=
                    remove;

            if (stack.isEmpty()) {

                inventory.offHandInventory.set(
                        i,
                        ItemStack.EMPTY
                );
            }
        }

        inventory.markDirty();

        return remaining == 0L;
    }

    private static boolean isCoinOfType(
            ItemStack stack,
            MochiCoinType type
    ) {
        if (stack == null
                || stack.isEmpty()
                || type == null) {
            return false;
        }

        if (!(stack.getItem()
                instanceof ItemMochiCoinBase)) {
            return false;
        }

        ItemMochiCoinBase item =
                (ItemMochiCoinBase)
                        stack.getItem();

        if (item.getCoinType()
                != type) {
            return false;
        }

        return MochiCoinNBT.isValid(
                stack,
                type
        );
    }
}
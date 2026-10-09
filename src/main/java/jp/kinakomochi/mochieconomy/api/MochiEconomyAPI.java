package jp.kinakomochi.mochieconomy.api;

import jp.kinakomochi.mochieconomy.item.ItemMochiCoinBase;
import jp.kinakomochi.mochieconomy.item.MochiCoinType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class MochiEconomyAPI {

    private MochiEconomyAPI() {
    }

    public static long getBalance(
            EntityPlayer player
    ) {
        if (player == null) {
            return 0L;
        }

        InventoryPlayer inventory =
                player.inventory;

        long total = 0L;

        for (ItemStack stack :
                inventory.mainInventory) {

            total +=
                    MochiCoinAPI.getStackValue(
                            stack
                    );
        }

        for (ItemStack stack :
                inventory.offHandInventory) {

            total +=
                    MochiCoinAPI.getStackValue(
                            stack
                    );
        }

        return total;
    }

    public static boolean has(
            EntityPlayer player,
            long amount
    ) {
        if (player == null
                || amount < 0L) {
            return false;
        }

        return getBalance(player)
                >= amount;
    }

    public static long deposit(
            EntityPlayer player,
            long amount
    ) {
        if (player == null
                || amount <= 0L) {
            return 0L;
        }

        ItemStack[] backup =
                createInventoryBackup(
                        player
                );

        List<ItemStack> stacks =
                MochiCoinAPI.createStacks(
                        amount
                );

        for (ItemStack stack : stacks) {

            if (!insertFully(
                    player,
                    stack
            )) {

                restoreInventory(
                        player,
                        backup
                );

                return 0L;
            }
        }

        return amount;
    }

    public static boolean withdraw(
            EntityPlayer player,
            long amount
    ) {
        if (player == null
                || amount < 0L) {
            return false;
        }

        if (amount == 0L) {
            return true;
        }

        long currentBalance =
                getBalance(player);

        if (currentBalance < amount) {
            return false;
        }

        long newBalance =
                currentBalance - amount;

        ItemStack[] backup =
                createInventoryBackup(
                        player
                );

        clearMochiCoins(
                player
        );

        List<ItemStack> remainingStacks =
                MochiCoinAPI.createStacks(
                        newBalance
                );

        for (ItemStack stack :
                remainingStacks) {

            if (!insertFully(
                    player,
                    stack
            )) {

                restoreInventory(
                        player,
                        backup
                );

                return false;
            }
        }

        return true;
    }

    public static long getBalanceAfterWithdraw(
            EntityPlayer player,
            long amount
    ) {
        if (player == null
                || amount < 0L) {
            return -1L;
        }

        long balance =
                getBalance(player);

        if (balance < amount) {
            return -1L;
        }

        return balance - amount;
    }

    /**
     * ItemStackをインベントリへ完全に追加する。
     *
     * 一部しか入らなかった場合はfalse。
     */
    public static boolean insertFully(
            EntityPlayer player,
            ItemStack stack
    ) {
        if (player == null
                || stack == null
                || stack.isEmpty()) {
            return false;
        }

        ItemStack remaining =
                stack.copy();

        boolean result =
                player.inventory
                        .addItemStackToInventory(
                                remaining
                        );

        return result
                && remaining.isEmpty();
    }

    /**
     * インベントリ全体のバックアップを作成する。
     */
    public static ItemStack[] createInventoryBackup(
            EntityPlayer player
    ) {
        if (player == null) {
            return new ItemStack[0];
        }

        InventoryPlayer inventory =
                player.inventory;

        int mainSize =
                inventory.mainInventory.size();

        int offHandSize =
                inventory.offHandInventory.size();

        ItemStack[] backup =
                new ItemStack[
                        mainSize
                                + offHandSize
                        ];

        int index = 0;

        for (ItemStack stack :
                inventory.mainInventory) {

            backup[index++] =
                    stack == null
                            ? ItemStack.EMPTY
                            : stack.copy();
        }

        for (ItemStack stack :
                inventory.offHandInventory) {

            backup[index++] =
                    stack == null
                            ? ItemStack.EMPTY
                            : stack.copy();
        }

        return backup;
    }

    /**
     * バックアップからインベントリを復元する。
     */
    public static void restoreInventory(
            EntityPlayer player,
            ItemStack[] backup
    ) {
        if (player == null
                || backup == null) {
            return;
        }

        InventoryPlayer inventory =
                player.inventory;

        int index = 0;

        for (int i = 0;
             i < inventory.mainInventory.size();
             i++) {

            if (index >= backup.length) {
                break;
            }

            ItemStack stack =
                    backup[index++];

            inventory.mainInventory.set(
                    i,
                    stack == null
                            ? ItemStack.EMPTY
                            : stack.copy()
            );
        }

        for (int i = 0;
             i < inventory.offHandInventory.size();
             i++) {

            if (index >= backup.length) {
                break;
            }

            ItemStack stack =
                    backup[index++];

            inventory.offHandInventory.set(
                    i,
                    stack == null
                            ? ItemStack.EMPTY
                            : stack.copy()
            );
        }

        player.inventory.markDirty();
    }

    private static void clearMochiCoins(
            EntityPlayer player
    ) {
        InventoryPlayer inventory =
                player.inventory;

        for (int i = 0;
             i < inventory.mainInventory.size();
             i++) {

            ItemStack stack =
                    inventory.mainInventory.get(
                            i
                    );

            if (MochiCoinAPI.isMochiCoin(
                    stack
            )) {

                inventory.mainInventory.set(
                        i,
                        ItemStack.EMPTY
                );
            }
        }

        for (int i = 0;
             i < inventory.offHandInventory.size();
             i++) {

            ItemStack stack =
                    inventory.offHandInventory.get(
                            i
                    );

            if (MochiCoinAPI.isMochiCoin(
                    stack
            )) {

                inventory.offHandInventory.set(
                        i,
                        ItemStack.EMPTY
                );
            }
        }

        inventory.markDirty();
    }
}
package jp.kinakomochi.mochieconomy.api;

import jp.kinakomochi.mochieconomy.item.ItemMochiCoinBase;
import jp.kinakomochi.mochieconomy.item.MochiCoinNBT;
import jp.kinakomochi.mochieconomy.item.MochiCoinType;
import jp.kinakomochi.mochieconomy.item.ModItems;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MochiCoinAPI {

    private MochiCoinAPI() {
    }

    /**
     * ItemStackを正規のMochiCoinとして補正する。
     *
     * NBTの有無や内容ではなく、
     * Itemそのものの種類を基準として
     * 正しいNBTを生成する。
     *
     * @return MochiCoinならtrue
     */
    public static boolean normalize(
            ItemStack stack
    ) {
        if (stack == null
                || stack.isEmpty()) {
            return false;
        }

        if (!(stack.getItem()
                instanceof ItemMochiCoinBase)) {
            return false;
        }

        ItemMochiCoinBase item =
                (ItemMochiCoinBase)
                        stack.getItem();

        MochiCoinType type =
                item.getCoinType();

        if (type == null) {
            return false;
        }

        /*
         * NBTが存在しない場合、
         * または内容が不正な場合は
         * Itemの種類を基準として修復する。
         */
        MochiCoinNBT.ensure(
                stack,
                type
        );

        return true;
    }

    /**
     * ItemStackがMochiCoinか判定する。
     *
     * 判定時にNBTも自動補正する。
     */
    public static boolean isMochiCoin(
            ItemStack stack
    ) {
        return normalize(
                stack
        );
    }

    /**
     * コイン1個あたりの価値を取得する。
     */
    public static long getUnitValue(
            ItemStack stack
    ) {
        if (!normalize(stack)) {
            return 0L;
        }

        ItemMochiCoinBase item =
                (ItemMochiCoinBase)
                        stack.getItem();

        return item.getMochiValue();
    }

    /**
     * ItemStack全体の価値を取得する。
     */
    public static long getStackValue(
            ItemStack stack
    ) {
        if (!normalize(stack)) {
            return 0L;
        }

        long unitValue =
                getUnitValue(stack);

        return unitValue
                * stack.getCount();
    }

    /**
     * 複数のItemStackに含まれる
     * MochiCoinの合計価値を取得する。
     */
    public static long getTotalValue(
            List<ItemStack> stacks
    ) {
        if (stacks == null) {
            return 0L;
        }

        long total = 0L;

        for (ItemStack stack : stacks) {
            total += getStackValue(
                    stack
            );
        }

        return total;
    }

    /**
     * 指定した価値を、
     * 可能な限り高額なコインから分解する。
     */
    public static List<CoinAmount> decompose(
            long value
    ) {
        if (value <= 0L) {
            return Collections.emptyList();
        }

        List<CoinAmount> result =
                new ArrayList<>();

        MochiCoinType[] types =
                MochiCoinType.values();

        for (int i = types.length - 1;
             i >= 0;
             i--) {

            MochiCoinType type =
                    types[i];

            long count =
                    value / type.getValue();

            if (count <= 0L) {
                continue;
            }

            result.add(
                    new CoinAmount(
                            type,
                            count
                    )
            );

            value %= type.getValue();

            if (value == 0L) {
                break;
            }
        }

        return result;
    }

    /**
     * 指定した価値をMochiCoinのItemStackへ変換する。
     *
     * 生成されるItemStackには必ず
     * 正しいNBTが付与される。
     */
    public static List<ItemStack> createStacks(
            long value
    ) {
        if (value <= 0L) {
            return Collections.emptyList();
        }

        List<ItemStack> result =
                new ArrayList<>();

        for (CoinAmount amount :
                decompose(value)) {

            MochiCoinType type =
                    amount.getType();

            long remaining =
                    amount.getAmount();

            ItemMochiCoinBase item =
                    getItem(type);

            if (item == null) {
                continue;
            }

            while (remaining > 0L) {

                int stackSize =
                        (int) Math.min(
                                remaining,
                                64L
                        );

                result.add(
                        item.createStack(
                                stackSize
                        )
                );

                remaining -= stackSize;
            }
        }

        return result;
    }

    /**
     * MochiCoinTypeに対応するItemを取得する。
     */
    public static ItemMochiCoinBase getItem(
            MochiCoinType type
    ) {
        if (type == null) {
            return null;
        }

        switch (type) {

            case NORMAL:
                return ModItems.MOCHI_COIN;

            case COMPRESSED:
                return ModItems.COMPRESSED_MOCHI_COIN;

            case SUPER_COMPRESSED:
                return ModItems.SUPER_COMPRESSED_MOCHI_COIN;

            default:
                return null;
        }
    }

    /**
     * コインの種類と個数を表すデータ。
     */
    public static final class CoinAmount {

        private final MochiCoinType type;

        private final long amount;

        public CoinAmount(
                MochiCoinType type,
                long amount
        ) {
            this.type = type;
            this.amount = amount;
        }

        public MochiCoinType getType() {
            return type;
        }

        public long getAmount() {
            return amount;
        }

        public long getTotalValue() {
            return type.getValue()
                    * amount;
        }
    }
}
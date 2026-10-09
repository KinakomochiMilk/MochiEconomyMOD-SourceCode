package jp.kinakomochi.mochieconomy.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class MochiCoinNBT {

    public static final String ROOT_TAG =
            "MochiCoin";

    public static final String TYPE_TAG =
            "MochiCoinType";

    public static final String VALUE_TAG =
            "MochiValue";

    private MochiCoinNBT() {
    }

    /**
     * ItemStackに正しいMochiCoin NBTを書き込む。
     */
    public static void write(
            ItemStack stack,
            MochiCoinType type
    ) {
        if (stack == null
                || stack.isEmpty()
                || type == null) {
            return;
        }

        NBTTagCompound root =
                stack.getTagCompound();

        if (root == null) {
            root = new NBTTagCompound();

            stack.setTagCompound(
                    root
            );
        }

        NBTTagCompound coin =
                new NBTTagCompound();

        coin.setString(
                TYPE_TAG,
                type.name()
        );

        coin.setLong(
                VALUE_TAG,
                type.getValue()
        );

        root.setTag(
                ROOT_TAG,
                coin
        );
    }

    /**
     * Itemの種類を基準としてNBTを自動補正する。
     *
     * NBTが存在しない場合だけでなく、
     * NBTの内容が不正な場合も修復する。
     */
    public static boolean ensure(
            ItemStack stack,
            MochiCoinType type
    ) {
        if (stack == null
                || stack.isEmpty()
                || type == null) {
            return false;
        }

        if (!isValid(
                stack,
                type
        )) {
            write(
                    stack,
                    type
            );
        }

        return true;
    }

    /**
     * MochiCoin NBTが存在するか確認する。
     */
    public static boolean hasData(
            ItemStack stack
    ) {
        if (stack == null
                || stack.isEmpty()) {
            return false;
        }

        NBTTagCompound root =
                stack.getTagCompound();

        return root != null
                && root.hasKey(
                ROOT_TAG,
                10
        );
    }

    /**
     * NBTに保存されているコイン種類を取得する。
     */
    public static MochiCoinType getType(
            ItemStack stack
    ) {
        if (!hasData(stack)) {
            return null;
        }

        NBTTagCompound root =
                stack.getTagCompound();

        NBTTagCompound coin =
                root.getCompoundTag(
                        ROOT_TAG
                );

        if (!coin.hasKey(
                TYPE_TAG,
                8
        )) {
            return null;
        }

        return MochiCoinType.fromName(
                coin.getString(
                        TYPE_TAG
                )
        );
    }

    /**
     * NBTに保存されている価値を取得する。
     */
    public static long getValue(
            ItemStack stack
    ) {
        if (!hasData(stack)) {
            return 0L;
        }

        NBTTagCompound root =
                stack.getTagCompound();

        NBTTagCompound coin =
                root.getCompoundTag(
                        ROOT_TAG
                );

        if (!coin.hasKey(
                VALUE_TAG,
                4
        )) {
            return 0L;
        }

        return coin.getLong(
                VALUE_TAG
        );
    }

    /**
     * NBTが指定されたコイン種類と一致しているか確認する。
     */
    public static boolean isValid(
            ItemStack stack,
            MochiCoinType expectedType
    ) {
        if (stack == null
                || stack.isEmpty()
                || expectedType == null) {
            return false;
        }

        if (!hasData(stack)) {
            return false;
        }

        MochiCoinType storedType =
                getType(stack);

        if (storedType != expectedType) {
            return false;
        }

        return getValue(stack)
                == expectedType.getValue();
    }
}
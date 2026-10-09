package jp.kinakomochi.mochieconomy.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public final class MochiItemStackSerializer {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final String NBT_TAG =
            "itemStackNbt";

    private MochiItemStackSerializer() {
    }

    /**
     * ItemStack全体をJSON文字列へ変換する。
     *
     * ItemStack.writeToNBT()によって
     * ItemStack全体をNBT化し、
     * そのMojangson表現をJSONへ格納する。
     *
     * これによりMod固有の複雑なNBTも
     * そのまま保存できる。
     */
    public static String serialize(
            ItemStack stack
    ) {
        if (stack == null
                || stack.isEmpty()) {
            return null;
        }

        try {
            NBTTagCompound nbt =
                    new NBTTagCompound();

            stack.writeToNBT(
                    nbt
            );

            JsonObject root =
                    new JsonObject();

            root.addProperty(
                    NBT_TAG,
                    nbt.toString()
            );

            return GSON.toJson(
                    root
            );

        } catch (Exception e) {

            System.err.println(
                    "[MochiEconomy] "
                            + "Failed to serialize ItemStack."
            );

            e.printStackTrace();

            return null;
        }
    }

    /**
     * JSON文字列からItemStackを復元する。
     */
    public static ItemStack deserialize(
            String json
    ) {
        if (json == null
                || json.trim().isEmpty()) {
            return ItemStack.EMPTY;
        }

        try {
            JsonObject root =
                    GSON.fromJson(
                            json,
                            JsonObject.class
                    );

            if (root == null
                    || !root.has(NBT_TAG)
                    || root.get(NBT_TAG)
                    .isJsonNull()) {

                return ItemStack.EMPTY;
            }

            String nbtString =
                    root.get(NBT_TAG)
                            .getAsString();

            if (nbtString == null
                    || nbtString.trim().isEmpty()) {

                return ItemStack.EMPTY;
            }

            NBTTagCompound nbt =
                    JsonToNBT.getTagFromJson(
                            nbtString
                    );

            if (nbt == null) {
                return ItemStack.EMPTY;
            }

            /*
             * Forge 1.12.2では
             * ItemStack.loadItemStackFromNBT()
             * は存在しない。
             *
             * NBTTagCompoundから
             * コンストラクタで復元する。
             */
            return new ItemStack(
                    nbt
            );

        } catch (Exception e) {

            System.err.println(
                    "[MochiEconomy] "
                            + "Failed to deserialize ItemStack."
            );

            e.printStackTrace();

            return ItemStack.EMPTY;
        }
    }
}
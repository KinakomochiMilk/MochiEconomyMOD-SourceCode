package jp.kinakomochi.mochieconomy.item;

import jp.kinakomochi.mochieconomy.api.MochiLocalization;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ItemMochiCoinBase extends Item {

    private final MochiCoinType coinType;

    protected ItemMochiCoinBase(
            String registryName,
            MochiCoinType coinType
    ) {
        this.coinType = coinType;

        setRegistryName(
                registryName
        );

        setUnlocalizedName(
                "mochieconomy." + registryName
        );

        setMaxStackSize(64);
    }

    public MochiCoinType getCoinType() {
        return coinType;
    }

    public long getMochiValue() {
        return coinType.getValue();
    }

    public ItemStack createStack(int amount) {
        ItemStack stack = new ItemStack(
                this,
                amount
        );

        MochiCoinNBT.write(
                stack,
                coinType
        );

        return stack;
    }

    public void ensureNBT(ItemStack stack) {
        MochiCoinNBT.ensure(
                stack,
                coinType
        );
    }

    public boolean isValidMochiCoin(
            ItemStack stack
    ) {
        return !stack.isEmpty()
                && stack.getItem() == this
                && MochiCoinNBT.isValid(
                stack,
                coinType
        );
    }

    /**
     * MochiCoinはエンチャント風の
     * グロー表示を常に行う。
     *
     * 見た目の判定はNBTには依存せず、
     * Item自体がMochiCoinであることを
     * 基準とする。
     */
    @Override
    public boolean hasEffect(
            ItemStack stack
    ) {
        return true;
    }

    @Override
    public void onCreated(
            ItemStack stack,
            World world,
            EntityPlayer player
    ) {
        super.onCreated(
                stack,
                world,
                player
        );

        MochiCoinNBT.write(
                stack,
                coinType
        );
    }

    @Override
    public String getItemStackDisplayName(
            ItemStack stack
    ) {
        return MochiLocalization.get(
                coinType.getLocalizationKey()
        );
    }
}
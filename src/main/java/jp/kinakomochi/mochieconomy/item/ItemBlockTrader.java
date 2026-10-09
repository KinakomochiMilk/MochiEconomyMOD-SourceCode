package jp.kinakomochi.mochieconomy.item;

import jp.kinakomochi.mochieconomy.api.MochiLocalization;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockTrader extends ItemBlock {

    public ItemBlockTrader(Block block) {
        super(block);

        setRegistryName(
                block.getRegistryName()
        );
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return MochiLocalization.get("trader");
    }
}
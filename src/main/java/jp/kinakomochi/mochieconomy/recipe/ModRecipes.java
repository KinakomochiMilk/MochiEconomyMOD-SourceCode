package jp.kinakomochi.mochieconomy.recipe;

import jp.kinakomochi.mochieconomy.MochiEconomy;
import jp.kinakomochi.mochieconomy.block.ModBlocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModRecipes {

    private ModRecipes() {
    }

    public static void register() {

        /*
         * もちコイン交換機
         *
         * 鉄インゴット   鉄インゴット   鉄インゴット
         * レッドストーン チェスト       レッドストーン
         * 鉄インゴット   鉄インゴット   鉄インゴット
         */
        GameRegistry.addShapedRecipe(
                new ResourceLocation(
                        MochiEconomy.MODID,
                        "trader"
                ),
                null,
                new ItemStack(
                        ModBlocks.TRADER
                ),
                "III",
                "RCR",
                "III",
                'I',
                Items.IRON_INGOT,
                'R',
                Items.REDSTONE,
                'C',
                Blocks.CHEST
        );
    }
}
package jp.kinakomochi.mochieconomy.item;

import jp.kinakomochi.mochieconomy.MochiEconomy;
import jp.kinakomochi.mochieconomy.block.ModBlocks;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(
        modid = MochiEconomy.MODID,
        value = Side.CLIENT
)
public final class ModItemModels {

    private ModItemModels() {
    }

    @SubscribeEvent
    public static void registerModels(
            ModelRegistryEvent event
    ) {
        registerItem(
                ModItems.MOCHI_COIN,
                "mochi_coin"
        );

        registerItem(
                ModItems.COMPRESSED_MOCHI_COIN,
                "compressed_mochi_coin"
        );

        registerItem(
                ModItems.SUPER_COMPRESSED_MOCHI_COIN,
                "super_compressed_mochi_coin"
        );

        Item traderItem =
                Item.getItemFromBlock(
                        ModBlocks.TRADER
                );

        ModelLoader.setCustomModelResourceLocation(
                traderItem,
                0,
                new ModelResourceLocation(
                        MochiEconomy.MODID + ":trader",
                        "inventory"
                )
        );
    }

    private static void registerItem(
            Item item,
            String modelName
    ) {
        ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new ModelResourceLocation(
                        MochiEconomy.MODID
                                + ":"
                                + modelName,
                        "inventory"
                )
        );
    }
}
package jp.kinakomochi.mochieconomy.block;

import jp.kinakomochi.mochieconomy.MochiEconomy;
import jp.kinakomochi.mochieconomy.item.ItemBlockTrader;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModBlocks {

    public static final Block TRADER =
            new BlockTrader();

    private ModBlocks() {
    }

    @Mod.EventBusSubscriber(
            modid = MochiEconomy.MODID
    )
    public static class Registration {

        @SubscribeEvent
        public static void registerBlocks(
                RegistryEvent.Register<Block> event
        ) {
            event.getRegistry().register(
                    TRADER
            );
        }

        @SubscribeEvent
        public static void registerItems(
                RegistryEvent.Register<Item> event
        ) {
            event.getRegistry().register(
                    new ItemBlockTrader(TRADER)
            );
        }
    }
}
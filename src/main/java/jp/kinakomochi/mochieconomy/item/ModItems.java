package jp.kinakomochi.mochieconomy.item;

import jp.kinakomochi.mochieconomy.MochiEconomy;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModItems {

    public static final ItemMochiCoin MOCHI_COIN =
            new ItemMochiCoin();

    public static final ItemCompressedMochiCoin
            COMPRESSED_MOCHI_COIN =
            new ItemCompressedMochiCoin();

    public static final ItemSuperCompressedMochiCoin
            SUPER_COMPRESSED_MOCHI_COIN =
            new ItemSuperCompressedMochiCoin();

    private ModItems() {
    }

    @Mod.EventBusSubscriber(
            modid = MochiEconomy.MODID
    )
    public static class Registration {

        @SubscribeEvent
        public static void registerItems(
                RegistryEvent.Register<Item> event
        ) {
            event.getRegistry().register(
                    MOCHI_COIN
            );

            event.getRegistry().register(
                    COMPRESSED_MOCHI_COIN
            );

            event.getRegistry().register(
                    SUPER_COMPRESSED_MOCHI_COIN
            );
        }
    }
}
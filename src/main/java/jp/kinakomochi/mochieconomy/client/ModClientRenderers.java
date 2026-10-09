package jp.kinakomochi.mochieconomy.client;

import jp.kinakomochi.mochieconomy.MochiEconomy;
import jp.kinakomochi.mochieconomy.tileentity.TileEntityTrader;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(
        modid = MochiEconomy.MODID,
        value = Side.CLIENT
)
public final class ModClientRenderers {

    private ModClientRenderers() {
    }

    public static void register() {
        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTrader.class,
                new TraderRenderer()
        );
    }
}
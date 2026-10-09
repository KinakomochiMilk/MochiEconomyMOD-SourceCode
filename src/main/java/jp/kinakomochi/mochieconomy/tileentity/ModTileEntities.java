package jp.kinakomochi.mochieconomy.tileentity;

import jp.kinakomochi.mochieconomy.MochiEconomy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModTileEntities {

    private ModTileEntities() {
    }

    public static void register() {
        GameRegistry.registerTileEntity(
                TileEntityTrader.class,
                new ResourceLocation(
                        MochiEconomy.MODID,
                        "trader"
                )
        );
    }
}
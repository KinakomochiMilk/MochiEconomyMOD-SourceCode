package jp.kinakomochi.mochieconomy.gui;

import jp.kinakomochi.mochieconomy.config.ShopManager;
import jp.kinakomochi.mochieconomy.network.MochiNetwork;
import jp.kinakomochi.mochieconomy.network.message.MessageShopData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class MochiGuiHandler
        implements IGuiHandler {

    @Override
    public Object getServerGuiElement(
            int ID,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z
    ) {
        if (ID != 0) {
            return null;
        }

        ContainerTrader container =
                new ContainerTrader(
                        player
                );

        if (player instanceof EntityPlayerMP) {
            MochiNetwork.INSTANCE.sendTo(
                    new MessageShopData(
                            ShopManager.getEntries()
                    ),
                    (EntityPlayerMP) player
            );
        }

        return container;
    }

    @Override
    public Object getClientGuiElement(
            int ID,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z
    ) {
        if (ID != 0) {
            return null;
        }

        return new GuiTrader(
                new ContainerTrader(
                        player
                )
        );
    }
}
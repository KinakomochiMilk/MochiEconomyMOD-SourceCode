package jp.kinakomochi.mochieconomy.client;

import jp.kinakomochi.mochieconomy.network.message.MessageShopData;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageShopDataClientHandler
        implements IMessageHandler<MessageShopData, IMessage> {

    @Override
    public IMessage onMessage(
            MessageShopData message,
            MessageContext context
    ) {
        Minecraft.getMinecraft()
                .addScheduledTask(
                        () -> ShopManagerClient.setEntries(
                                message.getEntries()
                        )
                );

        return null;
    }
}
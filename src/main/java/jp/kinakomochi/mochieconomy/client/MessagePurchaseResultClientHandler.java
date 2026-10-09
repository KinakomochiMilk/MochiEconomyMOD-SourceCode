package jp.kinakomochi.mochieconomy.client;

import jp.kinakomochi.mochieconomy.gui.GuiTrader;
import jp.kinakomochi.mochieconomy.network.message.MessagePurchaseResult;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePurchaseResultClientHandler
        implements IMessageHandler<
        MessagePurchaseResult,
        IMessage> {

    @Override
    public IMessage onMessage(
            MessagePurchaseResult message,
            MessageContext context
    ) {
        Minecraft.getMinecraft()
                .addScheduledTask(
                        () -> {

                            Minecraft minecraft =
                                    Minecraft.getMinecraft();

                            if (minecraft.player
                                    == null) {
                                return;
                            }

                            if (minecraft.currentScreen
                                    instanceof GuiTrader) {

                                ((GuiTrader)
                                        minecraft.currentScreen)
                                        .setServerBalance(
                                                message.getBalance()
                                        );
                            }

                            minecraft.player.sendMessage(
                                    new net.minecraft.util.text.TextComponentString(
                                            message.getMessage()
                                    )
                            );
                        }
                );

        return null;
    }
}
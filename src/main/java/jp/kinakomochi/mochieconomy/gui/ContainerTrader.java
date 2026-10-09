package jp.kinakomochi.mochieconomy.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class ContainerTrader
        extends Container {

    private final EntityPlayer player;

    public ContainerTrader(
            EntityPlayer player
    ) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public ItemStack transferStackInSlot(
            EntityPlayer player,
            int index
    ) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(
            EntityPlayer player
    ) {
        return player == this.player;
    }

    @Override
    public void onContainerClosed(
            EntityPlayer player
    ) {
        super.onContainerClosed(
                player
        );
    }
}
package jp.kinakomochi.mochieconomy.block;

import jp.kinakomochi.mochieconomy.MochiEconomy;
import jp.kinakomochi.mochieconomy.tileentity.TileEntityTrader;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

public class BlockTrader extends Block {

    public BlockTrader() {
        super(Material.IRON);

        setRegistryName(
                MochiEconomy.MODID,
                "trader"
        );

        setUnlocalizedName(
                MochiEconomy.MODID + ".trader"
        );

        setHardness(3.0F);
        setResistance(10.0F);
    }

    @Override
    public boolean hasTileEntity(
            IBlockState state
    ) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(
            World world,
            IBlockState state
    ) {
        return new TileEntityTrader();
    }

    @Override
    public EnumBlockRenderType getRenderType(
            IBlockState state
    ) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(
            World world,
            BlockPos pos,
            IBlockState state,
            EntityPlayer player,
            EnumHand hand,
            EnumFacing facing,
            float hitX,
            float hitY,
            float hitZ
    ) {
        System.out.println(
                "[MochiEconomy] Trader activated. Remote="
                        + world.isRemote
        );

        if (world.isRemote) {
            return true;
        }

        System.out.println(
                "[MochiEconomy] Opening Trader GUI."
        );

        player.openGui(
                MochiEconomy.INSTANCE,
                0,
                world,
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );

        return true;
    }
}
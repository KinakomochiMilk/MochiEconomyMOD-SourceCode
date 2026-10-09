package jp.kinakomochi.mochieconomy.client;

import jp.kinakomochi.mochieconomy.tileentity.TileEntityTrader;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TraderRenderer
        extends TileEntitySpecialRenderer<TileEntityTrader> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    "mochieconomy",
                    "textures/blocks/trader.png"
            );

    @Override
    public void render(
            TileEntityTrader tileEntity,
            double x,
            double y,
            double z,
            float partialTicks,
            int destroyStage,
            float alpha
    ) {
        GlStateManager.pushMatrix();

        /*
         * ブロックの位置へ移動
         */
        GlStateManager.translate(x, y, z);

        /*
         * テクスチャを直接読み込む
         */
        bindTexture(TEXTURE);

        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );

        /*
         * 光源による通常の明るさ処理を有効化
         */
        GlStateManager.enableLighting();

        Tessellator tessellator =
                Tessellator.getInstance();

        BufferBuilder buffer =
                tessellator.getBuffer();

        /*
         * 立方体を描画
         */
        renderCube(buffer);

        tessellator.draw();

        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }

    private void renderCube(BufferBuilder buffer) {

        /*
         * 1ブロック = 0～1
         */

        double min = 0.0D;
        double max = 1.0D;

        /*
         * 下
         */
        buffer.begin(
                GL11.GL_QUADS,
                DefaultVertexFormats.POSITION_TEX
        );

        buffer.pos(min, min, min)
                .tex(0.0D, 0.0D)
                .endVertex();

        buffer.pos(max, min, min)
                .tex(1.0D, 0.0D)
                .endVertex();

        buffer.pos(max, min, max)
                .tex(1.0D, 1.0D)
                .endVertex();

        buffer.pos(min, min, max)
                .tex(0.0D, 1.0D)
                .endVertex();

        /*
         * 上
         */
        buffer.pos(min, max, min)
                .tex(0.0D, 0.0D)
                .endVertex();

        buffer.pos(min, max, max)
                .tex(0.0D, 1.0D)
                .endVertex();

        buffer.pos(max, max, max)
                .tex(1.0D, 1.0D)
                .endVertex();

        buffer.pos(max, max, min)
                .tex(1.0D, 0.0D)
                .endVertex();

        /*
         * 北
         */
        buffer.pos(min, min, min)
                .tex(0.0D, 0.0D)
                .endVertex();

        buffer.pos(min, max, min)
                .tex(0.0D, 1.0D)
                .endVertex();

        buffer.pos(max, max, min)
                .tex(1.0D, 1.0D)
                .endVertex();

        buffer.pos(max, min, min)
                .tex(1.0D, 0.0D)
                .endVertex();

        /*
         * 南
         */
        buffer.pos(min, min, max)
                .tex(0.0D, 0.0D)
                .endVertex();

        buffer.pos(max, min, max)
                .tex(1.0D, 0.0D)
                .endVertex();

        buffer.pos(max, max, max)
                .tex(1.0D, 1.0D)
                .endVertex();

        buffer.pos(min, max, max)
                .tex(0.0D, 1.0D)
                .endVertex();

        /*
         * 西
         */
        buffer.pos(min, min, min)
                .tex(0.0D, 0.0D)
                .endVertex();

        buffer.pos(min, min, max)
                .tex(1.0D, 0.0D)
                .endVertex();

        buffer.pos(min, max, max)
                .tex(1.0D, 1.0D)
                .endVertex();

        buffer.pos(min, max, min)
                .tex(0.0D, 1.0D)
                .endVertex();

        /*
         * 東
         */
        buffer.pos(max, min, min)
                .tex(0.0D, 0.0D)
                .endVertex();

        buffer.pos(max, max, min)
                .tex(0.0D, 1.0D)
                .endVertex();

        buffer.pos(max, max, max)
                .tex(1.0D, 1.0D)
                .endVertex();

        buffer.pos(max, min, max)
                .tex(1.0D, 0.0D)
                .endVertex();
    }
}
package jp.kinakomochi.mochieconomy.gui;

import jp.kinakomochi.mochieconomy.api.MochiEconomyAPI;
import jp.kinakomochi.mochieconomy.api.MochiExchangeAPI;
import jp.kinakomochi.mochieconomy.api.MochiLocalization;
import jp.kinakomochi.mochieconomy.client.ShopManagerClient;
import jp.kinakomochi.mochieconomy.item.MochiCoinType;
import jp.kinakomochi.mochieconomy.network.MochiNetwork;
import jp.kinakomochi.mochieconomy.network.message.MessageExchangeRequest;
import jp.kinakomochi.mochieconomy.network.message.MessagePurchaseRequest;
import jp.kinakomochi.mochieconomy.network.message.MessageShopData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class GuiTrader extends GuiContainer {

    private static final int GUI_WIDTH = 420;
    private static final int GUI_HEIGHT = 300;

    private static final int TAB_PURCHASE = 0;
    private static final int TAB_EXCHANGE = 1;

    private static final int EXCHANGE_FROM_PREVIOUS = 40;
    private static final int EXCHANGE_FROM_NEXT = 41;
    private static final int EXCHANGE_TO_PREVIOUS = 42;
    private static final int EXCHANGE_TO_NEXT = 43;
    private static final int EXCHANGE_BUTTON = 44;

    /*
     * 購入画面のスクロール用。
     */
    private static final int PURCHASE_VISIBLE_ENTRIES = 4;

    private static final int PURCHASE_BUTTON_BASE = 100;

    /*
     * スクロールバーの位置。
     */
    private static final int PURCHASE_SCROLLBAR_X = 402;
    private static final int PURCHASE_SCROLLBAR_Y = 110;
    private static final int PURCHASE_SCROLLBAR_HEIGHT = 164;

    private int currentTab =
            TAB_PURCHASE;

    private GuiButton purchaseTabButton;
    private GuiButton exchangeTabButton;

    private GuiButton[] purchaseButtons;

    /*
     * 現在表示している先頭の商品番号。
     *
     * 例:
     * 0 → 1～4番目
     * 1 → 2～5番目
     * 2 → 3～6番目
     */
    private int purchaseScrollOffset = 0;

    private GuiButton exchangeFromPreviousButton;
    private GuiButton exchangeFromNextButton;
    private GuiButton exchangeToPreviousButton;
    private GuiButton exchangeToNextButton;
    private GuiButton exchangeButton;

    private GuiTextField exchangeAmountField;

    /*
     * Forge 1.12.2のGuiTextFieldには
     * getFocused()が存在しないため、
     * 現在フォーカスしているフィールドを
     * 自前で管理する。
     */
    private GuiTextField focusedTextField;

    private MochiCoinType exchangeFromType =
            MochiCoinType.NORMAL;

    private MochiCoinType exchangeToType =
            MochiCoinType.COMPRESSED;

    private final ContainerTrader containerTrader;

    private final RenderItem renderItem;

    private long serverBalance;

    public GuiTrader(
            ContainerTrader container
    ) {
        super(container);

        this.containerTrader =
                container;

        this.xSize =
                GUI_WIDTH;

        this.ySize =
                GUI_HEIGHT;

        this.renderItem =
                Minecraft.getMinecraft()
                        .getRenderItem();

        this.serverBalance =
                MochiEconomyAPI.getBalance(
                        Minecraft.getMinecraft().player
                );

        this.focusedTextField =
                null;
    }

    public void setServerBalance(
            long balance
    ) {
        if (balance < 0L) {
            balance = 0L;
        }

        this.serverBalance =
                balance;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.clear();

        this.focusedTextField =
                null;

        purchaseScrollOffset = 0;

        purchaseTabButton =
                new GuiButton(
                        TAB_PURCHASE,
                        guiLeft + 10,
                        guiTop + 24,
                        195,
                        20,
                        MochiLocalization.get(
                                "purchase"
                        )
                );

        exchangeTabButton =
                new GuiButton(
                        TAB_EXCHANGE,
                        guiLeft + 215,
                        guiTop + 24,
                        195,
                        20,
                        MochiLocalization.get(
                                "exchange"
                        )
                );

        this.buttonList.add(
                purchaseTabButton
        );

        this.buttonList.add(
                exchangeTabButton
        );

        /*
         * 1画面に4商品表示。
         */
        purchaseButtons =
                new GuiButton[
                        PURCHASE_VISIBLE_ENTRIES
                        ];

        for (int i = 0;
             i < purchaseButtons.length;
             i++) {

            purchaseButtons[i] =
                    new GuiButton(
                            PURCHASE_BUTTON_BASE + i,
                            guiLeft + 330,
                            guiTop + 110 + i * 42,
                            70,
                            20,
                            MochiLocalization.get(
                                    "purchase_button"
                            )
                    );

            purchaseButtons[i].visible =
                    false;

            this.buttonList.add(
                    purchaseButtons[i]
            );
        }

        exchangeFromPreviousButton =
                new GuiButton(
                        EXCHANGE_FROM_PREVIOUS,
                        guiLeft + 45,
                        guiTop + 91,
                        25,
                        20,
                        "<"
                );

        exchangeFromNextButton =
                new GuiButton(
                        EXCHANGE_FROM_NEXT,
                        guiLeft + 350,
                        guiTop + 91,
                        25,
                        20,
                        ">"
                );

        exchangeToPreviousButton =
                new GuiButton(
                        EXCHANGE_TO_PREVIOUS,
                        guiLeft + 45,
                        guiTop + 139,
                        25,
                        20,
                        "<"
                );

        exchangeToNextButton =
                new GuiButton(
                        EXCHANGE_TO_NEXT,
                        guiLeft + 350,
                        guiTop + 139,
                        25,
                        20,
                        ">"
                );

        exchangeButton =
                new GuiButton(
                        EXCHANGE_BUTTON,
                        guiLeft + 155,
                        guiTop + 225,
                        110,
                        20,
                        "両替する"
                );

        this.buttonList.add(
                exchangeFromPreviousButton
        );

        this.buttonList.add(
                exchangeFromNextButton
        );

        this.buttonList.add(
                exchangeToPreviousButton
        );

        this.buttonList.add(
                exchangeToNextButton
        );

        this.buttonList.add(
                exchangeButton
        );

        exchangeAmountField =
                new GuiTextField(
                        40,
                        this.fontRenderer,
                        guiLeft + 155,
                        guiTop + 198,
                        110,
                        20
                );

        exchangeAmountField.setMaxStringLength(
                18
        );

        exchangeAmountField.setText(
                "64"
        );

        exchangeAmountField.setFocused(
                false
        );

        updateTabButtons();
    }

    @Override
    protected void actionPerformed(
            GuiButton button
    ) throws IOException {

        switch (button.id) {

            case TAB_PURCHASE:

                currentTab =
                        TAB_PURCHASE;

                clearTextFieldFocus();

                break;

            case TAB_EXCHANGE:

                currentTab =
                        TAB_EXCHANGE;

                clearTextFieldFocus();

                break;

            /*
             * 購入ボタン。
             *
             * ボタン番号そのものではなく、
             * スクロール位置を加算して実際の商品番号を求める。
             */
            case PURCHASE_BUTTON_BASE:
            case PURCHASE_BUTTON_BASE + 1:
            case PURCHASE_BUTTON_BASE + 2:
            case PURCHASE_BUTTON_BASE + 3:

                purchaseShopEntry(
                        purchaseScrollOffset
                                + (
                                button.id
                                        - PURCHASE_BUTTON_BASE
                        )
                );

                break;

            case EXCHANGE_FROM_PREVIOUS:

                changeExchangeFrom(-1);

                break;

            case EXCHANGE_FROM_NEXT:

                changeExchangeFrom(1);

                break;

            case EXCHANGE_TO_PREVIOUS:

                changeExchangeTo(-1);

                break;

            case EXCHANGE_TO_NEXT:

                changeExchangeTo(1);

                break;

            case EXCHANGE_BUTTON:

                performExchange();

                break;

            default:

                return;
        }

        updateTabButtons();
    }

    private void changeExchangeFrom(
            int direction
    ) {
        MochiCoinType[] types =
                MochiCoinType.values();

        int index =
                exchangeFromType.ordinal();

        index += direction;

        if (index < 0) {
            index =
                    types.length - 1;
        }

        if (index >= types.length) {
            index = 0;
        }

        exchangeFromType =
                types[index];

        if (exchangeFromType
                == exchangeToType) {

            changeExchangeTo(
                    direction >= 0
                            ? 1
                            : -1
            );
        }
    }

    private void changeExchangeTo(
            int direction
    ) {
        MochiCoinType[] types =
                MochiCoinType.values();

        int index =
                exchangeToType.ordinal();

        index += direction;

        if (index < 0) {
            index =
                    types.length - 1;
        }

        if (index >= types.length) {
            index = 0;
        }

        exchangeToType =
                types[index];

        if (exchangeFromType
                == exchangeToType) {

            changeExchangeFrom(
                    direction >= 0
                            ? 1
                            : -1
            );
        }
    }

    private void performExchange() {

        String text =
                exchangeAmountField.getText();

        if (text == null
                || text.trim().isEmpty()) {
            return;
        }

        long amount;

        try {
            amount =
                    Long.parseLong(
                            text.trim()
                    );

        } catch (NumberFormatException e) {

            return;
        }

        if (amount <= 0L) {
            return;
        }

        if (exchangeFromType
                == exchangeToType) {
            return;
        }

        MochiNetwork.INSTANCE.sendToServer(
                new MessageExchangeRequest(
                        exchangeFromType,
                        exchangeToType,
                        amount
                )
        );
    }

    private void purchaseShopEntry(
            int index
    ) {
        List<
                MessageShopData.ShopEntryData
                > entries =
                ShopManagerClient.getEntries();

        if (index < 0
                || index >= entries.size()) {
            return;
        }

        MessageShopData.ShopEntryData entry =
                entries.get(index);

        if (entry == null
                || entry.getId() == null) {
            return;
        }

        MochiNetwork.INSTANCE.sendToServer(
                new MessagePurchaseRequest(
                        entry.getId()
                )
        );
    }

    /**
     * 現在の商品一覧に合わせて
     * スクロール位置を正規化する。
     */
    private void clampPurchaseScroll() {

        List<
                MessageShopData.ShopEntryData
                > entries =
                ShopManagerClient.getEntries();

        int maxScroll =
                Math.max(
                        0,
                        entries.size()
                                - PURCHASE_VISIBLE_ENTRIES
                );

        if (purchaseScrollOffset < 0) {
            purchaseScrollOffset = 0;
        }

        if (purchaseScrollOffset > maxScroll) {
            purchaseScrollOffset =
                    maxScroll;
        }
    }

    /**
     * 購入商品のスクロール位置を変更する。
     */
    private void scrollPurchase(
            int amount
    ) {
        List<
                MessageShopData.ShopEntryData
                > entries =
                ShopManagerClient.getEntries();

        int maxScroll =
                Math.max(
                        0,
                        entries.size()
                                - PURCHASE_VISIBLE_ENTRIES
                );

        purchaseScrollOffset += amount;

        if (purchaseScrollOffset < 0) {
            purchaseScrollOffset = 0;
        }

        if (purchaseScrollOffset > maxScroll) {
            purchaseScrollOffset =
                    maxScroll;
        }

        updateTabButtons();
    }

    private void updateTabButtons() {

        purchaseTabButton.enabled =
                currentTab != TAB_PURCHASE;

        exchangeTabButton.enabled =
                currentTab != TAB_EXCHANGE;

        boolean purchaseMode =
                currentTab
                        == TAB_PURCHASE;

        clampPurchaseScroll();

        if (purchaseButtons != null) {

            for (GuiButton button :
                    purchaseButtons) {

                button.visible =
                        false;
            }

            if (purchaseMode) {

                List<
                        MessageShopData.ShopEntryData
                        > entries =
                        ShopManagerClient
                                .getEntries();

                int remaining =
                        entries.size()
                                - purchaseScrollOffset;

                int maxEntries =
                        Math.min(
                                remaining,
                                purchaseButtons.length
                        );

                for (int i = 0;
                     i < maxEntries;
                     i++) {

                    purchaseButtons[i].visible =
                            true;
                }
            }
        }

        boolean exchangeMode =
                currentTab
                        == TAB_EXCHANGE;

        exchangeFromPreviousButton.visible =
                exchangeMode;

        exchangeFromNextButton.visible =
                exchangeMode;

        exchangeToPreviousButton.visible =
                exchangeMode;

        exchangeToNextButton.visible =
                exchangeMode;

        exchangeButton.visible =
                exchangeMode;

        exchangeAmountField.setVisible(
                exchangeMode
        );

        if (focusedTextField != null
                && !focusedTextField.getVisible()) {

            clearTextFieldFocus();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(
            float partialTicks,
            int mouseX,
            int mouseY
    ) {
        drawRect(
                guiLeft,
                guiTop,
                guiLeft + GUI_WIDTH,
                guiTop + GUI_HEIGHT,
                0xFF181818
        );

        drawRect(
                guiLeft + 2,
                guiTop + 2,
                guiLeft + GUI_WIDTH - 2,
                guiTop + GUI_HEIGHT - 2,
                0xFF303030
        );

        drawRect(
                guiLeft + 5,
                guiTop + 5,
                guiLeft + GUI_WIDTH - 5,
                guiTop + 19,
                0xFF202020
        );

        drawRect(
                guiLeft + 5,
                guiTop + 19,
                guiLeft + GUI_WIDTH - 5,
                guiTop + 49,
                0xFF181818
        );

        drawRect(
                guiLeft + 10,
                guiTop + 50,
                guiLeft + GUI_WIDTH - 10,
                guiTop + GUI_HEIGHT - 10,
                0xFF252525
        );

        drawGuiContent();
    }

    private void drawGuiContent() {

        this.fontRenderer.drawString(
                MochiLocalization.get(
                        "trader"
                ),
                guiLeft + 10,
                guiTop + 7,
                0xFFFFFF
        );

        switch (currentTab) {

            case TAB_PURCHASE:

                drawPurchaseTab();

                break;

            case TAB_EXCHANGE:

                drawExchangeTab();

                break;

            default:

                break;
        }
    }

    private void drawPurchaseTab() {

        this.fontRenderer.drawString(
                MochiLocalization.get(
                        "purchase"
                ),
                guiLeft + 20,
                guiTop + 65,
                0xFFFFFF
        );

        drawBalance();

        List<
                MessageShopData.ShopEntryData
                > entries =
                ShopManagerClient.getEntries();

        if (entries.isEmpty()) {

            this.fontRenderer.drawString(
                    "ショップ商品を読み込んでいます...",
                    guiLeft + 30,
                    guiTop + 110,
                    0xAAAAAA
            );

            return;
        }

        clampPurchaseScroll();

        int remaining =
                entries.size()
                        - purchaseScrollOffset;

        int maxEntries =
                Math.min(
                        remaining,
                        PURCHASE_VISIBLE_ENTRIES
                );

        for (int i = 0;
             i < maxEntries;
             i++) {

            int entryIndex =
                    purchaseScrollOffset + i;

            drawShopEntry(
                    entries.get(entryIndex),
                    i
            );
        }

        drawPurchaseScrollbar(
                entries.size()
        );
    }

    private void drawBalance() {

        drawRect(
                guiLeft + 20,
                guiTop + 82,
                guiLeft + GUI_WIDTH - 20,
                guiTop + 105,
                0xFF202020
        );

        this.fontRenderer.drawString(
                "所持金: "
                        + serverBalance
                        + " "
                        + MochiLocalization.get(
                        "mochi_coin"
                ),
                guiLeft + 28,
                guiTop + 89,
                0xFFFFFF
        );
    }

    /**
     * 購入画面右側のスクロールバーを描画する。
     */
    private void drawPurchaseScrollbar(
            int entryCount
    ) {
        int maxScroll =
                Math.max(
                        0,
                        entryCount
                                - PURCHASE_VISIBLE_ENTRIES
                );

        /*
         * スクロール不要ならバー自体を表示しない。
         */
        if (maxScroll <= 0) {
            return;
        }

        int barX =
                guiLeft
                        + PURCHASE_SCROLLBAR_X;

        int barY =
                guiTop
                        + PURCHASE_SCROLLBAR_Y;

        int barHeight =
                PURCHASE_SCROLLBAR_HEIGHT;

        /*
         * 背景。
         */
        drawRect(
                barX,
                barY,
                barX + 8,
                barY + barHeight,
                0xFF101010
        );

        /*
         * スクロールつまみの高さ。
         *
         * 表示件数 / 全商品数
         */
        int thumbHeight =
                Math.max(
                        20,
                        barHeight
                                * PURCHASE_VISIBLE_ENTRIES
                                / entryCount
                );

        int travel =
                barHeight
                        - thumbHeight;

        int thumbY =
                barY;

        if (maxScroll > 0) {

            thumbY +=
                    travel
                            * purchaseScrollOffset
                            / maxScroll;
        }

        drawRect(
                barX + 1,
                thumbY,
                barX + 7,
                thumbY + thumbHeight,
                0xFF888888
        );
    }

    private void drawExchangeTab() {

        this.fontRenderer.drawString(
                MochiLocalization.get(
                        "exchange"
                ),
                guiLeft + 20,
                guiTop + 65,
                0xFFFFFF
        );

        this.fontRenderer.drawString(
                "両替元",
                guiLeft + 85,
                guiTop + 76,
                0xAAAAAA
        );

        drawExchangeType(
                exchangeFromType,
                guiTop + 91
        );

        this.fontRenderer.drawString(
                "両替先",
                guiLeft + 85,
                guiTop + 124,
                0xAAAAAA
        );

        drawExchangeType(
                exchangeToType,
                guiTop + 139
        );

        this.fontRenderer.drawString(
                "数量",
                guiLeft + 105,
                guiTop + 202,
                0xAAAAAA
        );

        this.fontRenderer.drawString(
                "所持金: "
                        + serverBalance
                        + " "
                        + MochiLocalization.get(
                        "mochi_coin"
                ),
                guiLeft + 280,
                guiTop + 202,
                0xAAAAAA
        );

        if (exchangeAmountField != null
                && exchangeAmountField.getVisible()) {

            exchangeAmountField.drawTextBox();
        }
    }

    private void drawExchangeType(
            MochiCoinType type,
            int y
    ) {
        String name =
                MochiLocalization.get(
                        type.getLocalizationKey()
                );

        long count =
                MochiExchangeAPI.countCoins(
                        this.mc.player,
                        type
                );

        ItemMochiCoinDisplay(
                type,
                guiLeft + 85,
                y
        );

        this.fontRenderer.drawString(
                name,
                guiLeft + 110,
                y + 3,
                0xFFFFFF
        );

        this.fontRenderer.drawString(
                "所持: "
                        + count,
                guiLeft + 110,
                y + 13,
                0xAAAAAA
        );
    }

    private void ItemMochiCoinDisplay(
            MochiCoinType type,
            int x,
            int y
    ) {
        Item item =
                MochiExchangeItem(
                        type
                );

        if (item == null) {
            return;
        }

        ItemStack stack =
                new ItemStack(
                        item,
                        1
                );

        renderItem.renderItemAndEffectIntoGUI(
                stack,
                x,
                y
        );
    }

    private Item MochiExchangeItem(
            MochiCoinType type
    ) {
        if (type == null) {
            return null;
        }

        switch (type) {

            case NORMAL:

                return jp.kinakomochi.mochieconomy
                        .item.ModItems.MOCHI_COIN;

            case COMPRESSED:

                return jp.kinakomochi.mochieconomy
                        .item.ModItems.COMPRESSED_MOCHI_COIN;

            case SUPER_COMPRESSED:

                return jp.kinakomochi.mochieconomy
                        .item.ModItems.SUPER_COMPRESSED_MOCHI_COIN;

            default:

                return null;
        }
    }

    private void drawShopEntry(
            MessageShopData.ShopEntryData entry,
            int index
    ) {
        int y =
                guiTop
                        + 110
                        + index * 42;

        drawRect(
                guiLeft + 20,
                y,
                guiLeft + GUI_WIDTH - 20,
                y + 38,
                0xFF202020
        );

        ItemStack stack =
                createShopStack(
                        entry
                );

        if (stack != null
                && !stack.isEmpty()) {

            renderItem.renderItemAndEffectIntoGUI(
                    stack,
                    guiLeft + 27,
                    y + 11
            );
        }

        String itemName =
                getItemDisplayName(
                        stack
                );

        this.fontRenderer.drawString(
                itemName,
                guiLeft + 52,
                y + 7,
                0xFFFFFF
        );

        this.fontRenderer.drawString(
                "数量: "
                        + entry.getAmount(),
                guiLeft + 52,
                y + 21,
                0xAAAAAA
        );

        this.fontRenderer.drawString(
                MochiLocalization.get(
                        "price"
                )
                        + ": "
                        + entry.getPrice(),
                guiLeft + 225,
                y + 14,
                0xFFFFFF
        );
    }

    private ItemStack createShopStack(
            MessageShopData.ShopEntryData entry
    ) {
        if (entry == null
                || entry.getItem() == null) {

            return ItemStack.EMPTY;
        }

        Item item =
                Item.REGISTRY.getObject(
                        new ResourceLocation(
                                entry.getItem()
                        )
                );

        if (item == null) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(
                item,
                entry.getAmount()
        );
    }

    private String getItemDisplayName(
            ItemStack stack
    ) {
        if (stack == null
                || stack.isEmpty()) {

            return "Unknown Item";
        }

        return stack.getDisplayName();
    }

    @Override
    protected void keyTyped(
            char typedChar,
            int keyCode
    ) throws IOException {

        if (keyCode == 1) {

            this.mc.player.closeScreen();

            return;
        }

        if (focusedTextField != null
                && focusedTextField.getVisible()) {

            focusedTextField.textboxKeyTyped(
                    typedChar,
                    keyCode
            );

            return;
        }

        super.keyTyped(
                typedChar,
                keyCode
        );
    }

    @Override
    protected void mouseClicked(
            int mouseX,
            int mouseY,
            int mouseButton
    ) throws IOException {

        super.mouseClicked(
                mouseX,
                mouseY,
                mouseButton
        );

        if (mouseButton != 0) {
            return;
        }

        boolean clickedTextField =
                false;

        /*
         * 両替数量欄
         */
        if (exchangeAmountField != null
                && exchangeAmountField.getVisible()
                && isInside(
                mouseX,
                mouseY,
                guiLeft + 155,
                guiTop + 198,
                110,
                20
        )) {

            exchangeAmountField.mouseClicked(
                    mouseX,
                    mouseY,
                    mouseButton
            );

            setFocusedTextField(
                    exchangeAmountField
            );

            clickedTextField = true;
        }

        if (!clickedTextField) {

            clearTextFieldFocus();
        }
    }

    /**
     * マウスホイールによる購入商品のスクロール。
     */
    @Override
    public void handleMouseInput()
            throws IOException {

        super.handleMouseInput();

        if (currentTab != TAB_PURCHASE) {
            return;
        }

        int wheel =
                Mouse.getEventDWheel();

        if (wheel == 0) {
            return;
        }

        /*
         * ホイール上方向 → 上へ
         * ホイール下方向 → 下へ
         */
        if (wheel > 0) {

            scrollPurchase(-1);

        } else {

            scrollPurchase(1);
        }
    }

    /**
     * テキストフィールドのフォーカスを
     * 1つだけ設定する。
     */
    private void setFocusedTextField(
            GuiTextField field
    ) {

        if (exchangeAmountField != null) {

            exchangeAmountField.setFocused(
                    false
            );
        }

        focusedTextField =
                field;

        if (focusedTextField != null) {

            focusedTextField.setFocused(
                    true
            );
        }
    }

    /**
     * すべてのテキストフィールドの
     * フォーカスを解除する。
     */
    private void clearTextFieldFocus() {

        if (exchangeAmountField != null) {

            exchangeAmountField.setFocused(
                    false
            );
        }

        focusedTextField =
                null;
    }

    /**
     * 指定した矩形内をクリックしたか判定する。
     */
    private boolean isInside(
            int mouseX,
            int mouseY,
            int x,
            int y,
            int width,
            int height
    ) {
        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
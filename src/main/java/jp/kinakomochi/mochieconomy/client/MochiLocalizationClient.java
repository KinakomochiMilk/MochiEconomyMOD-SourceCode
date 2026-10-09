package jp.kinakomochi.mochieconomy.client;

import jp.kinakomochi.mochieconomy.MochiEconomy;
import jp.kinakomochi.mochieconomy.api.MochiLocalization;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(
        modid = MochiEconomy.MODID,
        value = Side.CLIENT
)
public final class MochiLocalizationClient {

    private static String currentLocale = null;

    private MochiLocalizationClient() {
    }

    @SubscribeEvent
    public static void onClientTick(
            TickEvent.ClientTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.getLanguageManager() == null) {
            return;
        }

        String locale =
                minecraft.getLanguageManager()
                        .getCurrentLanguage()
                        .getLanguageCode();

        if (locale == null) {
            return;
        }

        if (!locale.equals(currentLocale)) {
            MochiLocalization.initialize(locale);
            currentLocale = locale;
        }
    }
}
package jp.kinakomochi.mochieconomy;

import jp.kinakomochi.mochieconomy.command.CommandEconomy;
import jp.kinakomochi.mochieconomy.config.MochiConfigManager;
import jp.kinakomochi.mochieconomy.gui.MochiGuiHandler;
import jp.kinakomochi.mochieconomy.network.MochiNetwork;
import jp.kinakomochi.mochieconomy.recipe.ModRecipes;
import jp.kinakomochi.mochieconomy.tileentity.ModTileEntities;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.Logger;

@Mod(
        modid = MochiEconomy.MODID,
        name = MochiEconomy.NAME,
        version = MochiEconomy.VERSION
)
public class MochiEconomy {

    public static final String MODID =
            "mochieconomy";

    public static final String NAME =
            "Kinakomochi Economy";

    public static final String VERSION =
            "0.1.0-alpha";

    public static MochiEconomy INSTANCE;

    private static Logger logger;

    @Mod.EventHandler
    public void preInit(
            FMLPreInitializationEvent event
    ) {

        INSTANCE = this;

        logger =
                event.getModLog();

        logger.info(
                "Kinakomochi Economy pre-initializing..."
        );

        MochiConfigManager.initialize(
                event.getModConfigurationDirectory()
        );
    }

    @Mod.EventHandler
    public void init(
            FMLInitializationEvent event
    ) {

        logger.info(
                "Kinakomochi Economy initializing..."
        );

        ModTileEntities.register();

        /*
         * レシピ登録
         */
        ModRecipes.register();

        MochiNetwork.register();

        NetworkRegistry.INSTANCE
                .registerGuiHandler(
                        this,
                        new MochiGuiHandler()
                );
    }

    @Mod.EventHandler
    public void serverStarting(
            FMLServerStartingEvent event
    ) {

        event.registerServerCommand(
                new CommandEconomy()
        );
    }

    @Mod.EventHandler
    public void postInit(
            FMLPostInitializationEvent event
    ) {

        logger.info(
                "Kinakomochi Economy loaded successfully."
        );
    }
}
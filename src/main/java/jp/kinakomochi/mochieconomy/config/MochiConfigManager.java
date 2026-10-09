package jp.kinakomochi.mochieconomy.config;

import java.io.File;

public final class MochiConfigManager {

    private static File configDirectory;

    private static ShopConfig shopConfig;

    private MochiConfigManager() {
    }

    public static void initialize(
            File minecraftConfigDirectory
    ) {
        configDirectory =
                new File(
                        minecraftConfigDirectory,
                        "mochieconomy"
                );

        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }

        /*
         * 管理ショップ
         */
        shopConfig =
                new ShopConfig(
                        new File(
                                configDirectory,
                                "shop.toml"
                        )
                );

        /*
         * shop.tomlが存在しない場合は
         * まず初期ファイルを作成する。
         */
        shopConfig.load();

        /*
         * Minecraft / Forgeに登録されている
         * Itemのうち、shop.tomlに存在しないものを
         * 仮エントリとして追加する。
         *
         * 既存エントリは変更しない。
         */
        ShopConfigGenerator.generateMissingEntries(
                new File(
                        configDirectory,
                        "shop.toml"
                )
        );

        /*
         * 自動生成されたエントリを含めて
         * もう一度読み込む。
         */
        shopConfig.load();

        /*
         * 有効なショップ商品を
         * 実際のItemRegistryと照合する。
         */
        ShopManager.load();
    }

    public static ShopConfig getShopConfig() {
        return shopConfig;
    }

    public static File getConfigDirectory() {
        return configDirectory;
    }

    public static void reload() {

        if (shopConfig != null) {

            shopConfig.load();

            ShopConfigGenerator.generateMissingEntries(
                    new File(
                            configDirectory,
                            "shop.toml"
                    )
            );

            shopConfig.load();

            ShopManager.load();
        }
    }
}